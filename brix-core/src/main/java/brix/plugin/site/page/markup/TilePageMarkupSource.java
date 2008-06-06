package brix.plugin.site.page.markup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Remark;
import org.htmlparser.Text;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;

import brix.Brix;
import brix.exception.BrixException;
import brix.plugin.fragment.FragmentTag;
import brix.plugin.site.page.TileContainerNode;
import brix.plugin.site.page.TileTemplateNode;
import brix.web.nodepage.markup.Item;
import brix.web.nodepage.markup.MarkupSource;
import brix.web.nodepage.markup.Tag;
import brix.web.nodepage.markup.Tag.Type;
import brix.web.nodepage.markup.simple.SimpleComment;
import brix.web.nodepage.markup.simple.SimpleTag;
import brix.web.nodepage.markup.simple.SimpleText;

/**
 * {@link MarkupSource} for tile markup. Parses and merges the content of tile container node and
 * it's templates.
 * 
 * @author Matej Knopp
 * 
 */
public class TilePageMarkupSource implements MarkupSource
{
    private final TileContainerNode node;

    public TilePageMarkupSource(TileContainerNode node)
    {
        this.node = node;
    }

    public Object getExpirationToken()
    {
        return getMostRecentLastModifiedDate();
    }

    /**
     * Returns the most recent date of last modification of tile page and it's templates. The date
     * is then used as expiration token.
     * 
     * @return
     */
    private Date getMostRecentLastModifiedDate()
    {
        Date current = null;
        for (TileContainerNode node = this.node; node != null; node = (TileContainerNode)node
            .getTemplate())
        {
            Date lm = node.getLastModified();
            if (lm != null)
            {
                if (current == null || current.compareTo(lm) < 0)
                {
                    current = lm;
                }
            }
        }
        return current;
    }

    public boolean isMarkupExpired(Object expirationToken)
    {
        if (expirationToken != null)
        {
            Date token = (Date)expirationToken;
            Date current = getMostRecentLastModifiedDate();
            if (current != null)
            {
                return token.compareTo(current) < 0;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    List<Item> items = null;

    private void parseMarkup()
    {
        items = new ArrayList<Item>();

        List<TileContainerNode> nodes = new ArrayList<TileContainerNode>();
        nodes.add(node);

        TileContainerNode n = node;
        while ((n = (TileContainerNode)n.getTemplate()) != null)
        {
            if (nodes.contains(n))
            {
                // TODO: Do something nicer
                throw new BrixException("Loop detected.");
            }
            nodes.add(0, n);
        }

        parseNode(nodes, 0, items);
    }

    private void parseNode(List<TileContainerNode> nodes, int current, List<Item> items)
    {
        TileContainerNode node = nodes.get(current);
        final String content = node.getDataAsString();
        final Lexer lexer = new Lexer(content);
        Node cursor = null;
        try
        {
            while ((cursor = lexer.nextNode()) != null)
            {
                if (cursor instanceof Remark)
                {
                    items.add(new SimpleComment(cursor.toHtml()));
                }
                else if (cursor instanceof Text)
                {
                    items.add(new SimpleText(cursor.toHtml()));
                }
                else if (cursor instanceof org.htmlparser.Tag)
                {
                    processTag(nodes, current, items, (org.htmlparser.Tag)cursor);
                }
                else
                {
                    throw new BrixException("Unknown node type " + cursor.getClass().getName());
                }
            }

        }
        catch (ParserException e)
        {
            throw new BrixException("Couldn't parse node content: '" + node.getPath() + "'", e);
        }

    }

    private boolean isKnownBrixTag(String tagName)
    {
        if (!tagName.startsWith(Brix.NS_PREFIX))
        {
            return false;
        }
        String simpleTagName = tagName.substring(Brix.NS_PREFIX.length());
        return TileTemplateNode.CONTENT_TAG.equals(tagName) || "tile".equals(simpleTagName) ||
            "fragment".equals(simpleTagName);
    }

    private boolean isOpenClose(org.htmlparser.Tag tag)
    {
        if (tag.getRawTagName().endsWith("/"))
        {
            return true;
        }
        else
        {
            List< ? > atts = tag.getAttributesEx();
            Attribute a = (Attribute)atts.get(atts.size() - 1);
            return a.getName() != null && a.getName().equals("/");
        }
    }

    private void processTag(List<TileContainerNode> nodes, int current, List<Item> items,
            org.htmlparser.Tag tag)
    {
        final Tag.Type type;
        final String rawName = tag.getRawTagName();
        if (rawName.startsWith("/"))
        {
            type = Tag.Type.CLOSE;
        }
        else if (isOpenClose(tag))
        {
            type = Tag.Type.OPEN_CLOSE;
        }
        else
        {
            type = Tag.Type.OPEN;
        }

        final String tagName = tag.getTagName().toLowerCase();

        if ("!doctype".equals(tagName))
        {
            this.doctype = tag.toHtml();
        }
        else if (type == Tag.Type.CLOSE)
        {
            if (!isKnownBrixTag(tagName))
            {
                Map<String, String> attributes = Collections.emptyMap();
                items.add(new SimpleTag(tagName, type, attributes));
            }
        }
        else
        {
            Map<String, String> attributes = getAttributes(tag);
            if (isKnownBrixTag(tagName))
            {
                processBrixTag(nodes, current, items, tagName, getAttributes(tag), type);
            }
            else
            {
                items.add(new SimpleTag(tagName, type, attributes));
            }
        }
    }

    private void processBrixTag(List<TileContainerNode> nodes, int current, List<Item> items,
            String tagName, Map<String, String> attributes, Tag.Type type)
    {
        TileContainerNode node = nodes.get(current);
        final String simpleTagName = tagName.substring(Brix.NS_PREFIX.length());
        if (TileTemplateNode.CONTENT_TAG.equals(tagName))
        {
            if (current != nodes.size() - 1)
            {
                parseNode(nodes, current + 1, items);
            }
        }
        else if ("tile".equals(simpleTagName))
        {
            String id = attributes.get(TileContainerNode.MARKUP_TILE_ID);
            items.add(new TileTag("div", Type.OPEN, attributes, node, id));
            items.add(new SimpleTag("div", Type.CLOSE, null));
        }
        else if ("fragment".equals(simpleTagName))
        {
            String id = attributes.get(TileContainerNode.MARKUP_TILE_ID);
            items.add(new FragmentTag("div", Type.OPEN, attributes, node, id));
            items.add(new SimpleTag("div", Type.CLOSE, null));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getAttributes(org.htmlparser.Tag tag)
    {
        Map<String, String> result = new HashMap<String, String>();

        List< ? > original = tag.getAttributesEx();
        List<Attribute> list = new ArrayList<Attribute>((Collection< ? extends Attribute>)original
            .subList(1, original.size()));

        for (Attribute a : list)
        {
            if (a.getName() != null && !a.getName().equals("/") && !a.isWhitespace())
            {
                result.put(a.getName(), a.getValue());
            }
        }

        return result;
    }

    private Iterator<Item> iterator;

    private String doctype = null;

    public Item nextMarkupItem()
    {
        if (items == null)
        {
            parseMarkup();
            iterator = items.iterator();
        }
        if (iterator.hasNext())
        {
            return iterator.next();
        }
        else
        {
            return null;
        }
    }

    public String getDoctype()
    {
        return doctype;
    }

}
