package brix.plugin.site.node.tilepage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;
import org.xmlpull.v1.XmlPullParserException;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Path;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.exception.LoopException;
import brix.plugin.site.node.tilepage.exception.UnknownTagException;
import brix.web.nodepage.BrixNodeWebPage;

/**
 * Parses markup of the page and all its templates, composes it, and generates a list of fragments.
 * Fragments can either be static markup fragments, or tile fragments.
 * 
 * @author ivaynberg
 * @author Matej Knopp
 */
class Markup
{

    private List<Fragment> fragments;

    public List<Fragment> getFragments()
    {
        return Collections.unmodifiableList(fragments);
    }

    public void parse(JcrSession session, Path path) throws XmlPullParserException
    {
        fragments = new ArrayList<Fragment>();

        List<TileContainerNode> hierarchy = new LinkedList<TileContainerNode>();
        TileContainerNode cursor = (TileContainerNode)session.getItem(path.toString());
        while (cursor != null)
        {
            hierarchy.add(0, cursor);
            JcrNode templateNode = cursor.getTemplate();

            if (templateNode != null)
            {

                Path template = new Path(templateNode.getPath());

                if (!template.isAbsolute())
                {
                    template = new Path(cursor.getPath()).parent().append(template);
                }

                if (!session.itemExists(template.toString()))
                {
                    throw new IllegalStateException("Could not find template node: " + template +
                            " specified by node: " + cursor.getPath());
                }

                cursor = (TileContainerNode)templateNode;

                if (hierarchy.contains(cursor))
                {
                    throw new LoopException("Nested templates may not form a loop.");
                }

            }
            else
            {
                cursor = null;
            }
        }

        hierarchy = Collections.unmodifiableList(hierarchy);
        new ParseHelper(hierarchy, 0).parse();
    }

    private boolean hasHead = false;

    private class ParseHelper
    {

        private final List<TileContainerNode> hierarchy;
        private final int indexInHierarchy;

        private final TileContainerNode page;
        private final Path path;
        private final String content;
        private final Lexer parser;

        private int rawMarkupStartPos = -1;

        public ParseHelper(final List<TileContainerNode> hierarchy, int index)
        {
            this.hierarchy = hierarchy;
            this.indexInHierarchy = index;

            this.page = hierarchy.get(indexInHierarchy);
            this.path = new Path(page.getPath());
            this.content = page.getDataAsString();
            this.parser = new Lexer(content);
        }

        public void parse()
        {
            try
            {
                parseInternal();
            }
            catch (ParserException e)
            {
                throw new RuntimeException(e);
            }
        }

        Node current = null;

        private void finishRawMarkup(boolean includeCurrent)
        {
            if (rawMarkupStartPos >= 0)
            {
                final int start = rawMarkupStartPos;
                final int end;
                if (includeCurrent)
                {
                    end = current.getEndPosition();
                }
                else
                {
                    end = current.getStartPosition();
                }
                fragments.add(new MarkupFragment(path, start, end));
                rawMarkupStartPos = -1;
            }
        }

        private boolean processHead()
        {
            if (hasHead)
            {
                throw new IllegalStateException(
                        "It is not possible to have multiple <head> tags in templates.");
            }
            else
            {
                finishRawMarkup(true);
                fragments.add(new HeadFragment());
                hasHead = true;
                return false;
            }
        }

        private boolean processBody()
        {
            if (!hasHead)
            {
                // the <head> tag is missing, we need to create the header
                // section before the body tag
                finishRawMarkup(false);
                fragments.add(new StringFragment("<head>\n"));
                fragments.add(new HeadFragment());
                fragments.add(new StringFragment("</head>\n"));
                hasHead = true;
            }
            return true;
        }

        private void skipTagContent() throws ParserException
        {
            final Tag tag = (Tag)current;
            final String tagName = tag.getTagName().toLowerCase();

            if (!tag.isEmptyXmlTag())
            {
                // skip any markup inside fragment's body
                Node cursor = tag;
                while (cursor != null)
                {
                    if (cursor instanceof Tag)
                    {
                        final Tag cursorTag = (Tag)cursor;
                        final String cursorTagName = cursorTag.getTagName();
                        final boolean cursorEndTag = cursorTag.isEndTag();
                        if (tagName.equalsIgnoreCase(cursorTagName) && cursorEndTag)
                        {
                            break;
                        }
                    }
                    cursor = parser.nextNode();
                }
            }
        }

        private boolean processContentTag() throws ParserException
        {
            finishRawMarkup(false);

            if (indexInHierarchy < hierarchy.size() - 1)
            {
                new ParseHelper(hierarchy, indexInHierarchy + 1).parse();
            }

            skipTagContent();

            return false;
        }

        @SuppressWarnings("unchecked")
        private boolean processTileTag() throws ParserException
        {
            finishRawMarkup(false);

            final Tag tag = (Tag)current;
            final String tagName = tag.getTagName().toLowerCase();

            // create fragment and add it to the list
            final String simpleTagName = tagName.substring(Brix.NS_PREFIX.length());

            final TagFragment frag = TagFragment.forTag(simpleTagName, path);

            Vector<Attribute> attributes = tag.getAttributesEx();

            if (null != attributes)
            {
                for (Attribute attribute : attributes)
                {
                    frag.addAttribute(attribute.getName(), attribute.getValue());
                }
            }
            fragments.add(frag);

            skipTagContent();

            return false;
        }

        private void parseInternal() throws ParserException
        {
            current = parser.nextNode();
            while (current != null)
            {
                boolean raw = true;

                if (current instanceof Tag)
                {
                    final Tag tag = (Tag)current;
                    final String tagName = tag.getTagName().toLowerCase();

                    if (tagName.equals("head") && !tag.isEndTag())
                    {
                        raw = processHead();
                    }
                    else if (tagName.equals("body") && !tag.isEndTag())
                    {
                        raw = processBody();
                    }
                    else if (TileTemplateNode.CONTENT_TAG.toLowerCase().equals(tagName))
                    {
                        raw = processContentTag();
                    }
                    else if (tagName.startsWith(Brix.NS_PREFIX.toLowerCase()))
                    {
                        raw = processTileTag();
                    }
                }

                if (raw)
                {
                    if (rawMarkupStartPos < 0)
                    {
                        rawMarkupStartPos = current.getStartPosition();
                    }
                }

                current = parser.nextNode();
            }

            // if there is anything left over in the accumulator add it
            if (rawMarkupStartPos >= 0)
            {
                fragments.add(new MarkupFragment(path, rawMarkupStartPos, -1));
            }
        }
    };

    public static interface Fragment
    {
        public static enum Type {
            STATIC,
            COMPONENT
        }

        Type getType();
    }

    public static interface StaticFragment extends Fragment
    {
        public String getMarkup(JcrSession session);
    }

    public static interface ComponentFragment extends Fragment
    {
        public Component newComponent(String id, BrixNodeWebPage tilePage, JcrSession session);
    };

    private abstract static class TagFragment implements ComponentFragment
    {
        private final String name;
        private Map<String, String> attributes;
        /** path of node in whose content this tag is defined */
        private final Path path;

        public TagFragment(String name, Path path)
        {
            this.name = name;
            this.path = path;

        }

        public Type getType()
        {
            return Type.COMPONENT;
        }

        void addAttribute(String name, String value)
        {
            if (attributes == null)
            {
                attributes = new HashMap<String, String>();
            }
            attributes.put(name, value);
        }

        public String getName()
        {
            return name;
        }

        public String getAttribute(String name)
        {
            return attributes.get(name);
        }

        public Path getPath()
        {
            return path;
        }

        public static TagFragment forTag(String tagName, Path path)
        {
            tagName = tagName.toLowerCase();

            if ("title".equals(tagName))
                return new TitleFragment(tagName, path);
            else if ("tile".equals(tagName))
                return new TileFragment(tagName, path);
            else
                throw new UnknownTagException("Unknown brix tag '" + tagName + "'.");
        }

    }

    private static class HeadFragment implements ComponentFragment
    {
        public Type getType()
        {
            return Type.COMPONENT;
        }

        public Component newComponent(String id, BrixNodeWebPage tilePage, JcrSession session)
        {
            return new HeaderContributorPanel(id);
        }
    };

    private static class TitleFragment extends TagFragment
    {

        public TitleFragment(String name, Path path)
        {
            super(name, path);
        }

        public Component newComponent(String id, BrixNodeWebPage tilePage, JcrSession session)
        {
            TilePageNode node = (TilePageNode)tilePage.getModelObject();
            return new Label(id, node.getTitle()).setRenderBodyOnly(true);
        }
    };

    private static class TileFragment extends TagFragment
    {

        public TileFragment(String name, Path path)
        {
            super(name, path);
        }

        public Component newComponent(String id, BrixNodeWebPage tilePage, JcrSession session)
        {

            TilePageNode node = (TilePageNode)tilePage.getModelObject();

            String tileId = getAttribute(TileContainerNode.MARKUP_TILE_ID);

            final TileContainerNode tileContainerNode = (TileContainerNode)session
                    .getItem(getPath().toString());

            String className = tileContainerNode.getTileClassName(tileId);
            Tile tile = node.getNodePlugin().getTileOfType(className);

            if (tile == null)
            {
                return new Label(id, "Tile '" + tileId + "' is not defined.");
            }
            else
            {
                return tile.newViewer(id, new BrixNodeModel(tileContainerNode.getTile(tileId)),
                        tilePage.getBrixPageParameters()).setRenderBodyOnly(true);
            }
        }
    };

    private static class StringFragment implements StaticFragment
    {
        private final String string;

        public StringFragment(String string)
        {
            this.string = string;
        }

        public String getMarkup(JcrSession session)
        {
            return string;
        }

        public Type getType()
        {
            return Type.STATIC;
        }
    }

    private static class MarkupFragment implements StaticFragment
    {

        private final int start;
        private final int end;
        private final Path path;

        public MarkupFragment(Path path, int start, int end)
        {
            this.path = path;
            this.start = start;
            this.end = end;
        }

        public String getMarkup(JcrSession session)
        {
            final TileContainerNode page = (TileContainerNode)session.getItem(path.toString());
            final String content = page.getDataAsString();

            if (end >= 0)
            {
                final String fragment = content.substring(start, end);
                return fragment;
            }
            else
            {
                final String fragment = content.substring(start);
                return fragment;
            }
        }

        public Type getType()
        {
            return Type.STATIC;
        }
    }
}
