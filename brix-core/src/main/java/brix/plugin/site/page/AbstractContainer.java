package brix.plugin.site.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;

import brix.Brix;
import brix.exception.NodeNotFoundException;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.markup.tag.Item;
import brix.markup.variable.VariableKeyProvider;
import brix.markup.variable.VariableTransformer;
import brix.markup.variable.VariableValueProvider;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.tile.TileTag;
import brix.plugin.site.page.tile.TileContainerFacet;

public abstract class AbstractContainer extends BrixFileNode
        implements
            VariableValueProvider,
            VariableKeyProvider
{

    private final TileContainerFacet tileManager;

    public AbstractContainer(Node delegate, JcrSession session)
    {
        super(delegate, session);
        tileManager = new TileContainerFacet(this);
    }

    /** Name of markup attribute used to identify tile id inside brix:tile tag */
    public static final String MARKUP_TILE_ID = "id";


    private static class Properties
    {
        public static final String TITLE = Brix.NS_PREFIX + "title";
        public static final String TEMPLATE = Brix.NS_PREFIX + "template";
        public static final String REQUIRES_SSL = Brix.NS_PREFIX + "requiresSSL";
    }


    public TileContainerFacet tiles()
    {
        return tileManager;
    }

    public String getTitle()
    {
        if (hasProperty(Properties.TITLE))
            return getProperty(Properties.TITLE).getString();
        else
            return null;
    }    
    

    public void setTitle(String title)
    {
        setProperty(Properties.TITLE, title);
    }


    public TemplateNode getTemplate()
    {
        if (hasProperty(Properties.TEMPLATE))
        {
            return (TemplateNode)getProperty(Properties.TEMPLATE).getNode();
        }
        else
        {
            return null;
        }
    }

    public void setTemplate(BrixNode node)
    {
        setProperty(Properties.TEMPLATE, node);
    }

    public void setTemplatePath(String path)
    {
        if (path == null)
        {
            setTemplate(null);
        }
        else
        {
            BrixNode node = (BrixNode)SitePlugin.get().nodeForPath(this, path);

            if (node == null)
            {
                throw new NodeNotFoundException("No node found on path '" + path + "'.");
            }

            setTemplate((BrixNode)node);
        }
    }

    public String getTemplatePath()
    {
        BrixNode template = getTemplate();
        return template != null ? SitePlugin.get().pathForNode(template) : null;
    }

    public void setRequiresSSL(boolean value)
    {
        if (value == false)
        {
            setProperty(Properties.REQUIRES_SSL, (String)null);
        }
        else
        {
            setProperty(Properties.REQUIRES_SSL, true);
        }
    }

    private static final String VARIABLES_NODE_NAME = Brix.NS_PREFIX + "variables";

    public boolean requiresSSL()
    {
        return isRequiresSSL() || tileManager.anyTileRequiresSSL();
    }

    public boolean isRequiresSSL()
    {
        if (hasProperty(Properties.REQUIRES_SSL))
        {
            return getProperty(Properties.REQUIRES_SSL).getBoolean();
        }
        else
        {
            return false;
        }
    }

    @Override
    public Protocol getRequiredProtocol()
    {
        if (requiresSSL())
        {
            return Protocol.HTTPS;
        }
        else
        {
            return Protocol.HTTP;
        }
    }

    public AbstractSitePagePlugin getNodePlugin()
    {
        return (AbstractSitePagePlugin)SitePlugin.get().getNodePluginForNode(this);
    }

    public String getVariableValue(String key, boolean followTemplate)
    {
        if (hasNode(VARIABLES_NODE_NAME))
        {
            JcrNode node = getNode(VARIABLES_NODE_NAME);
            if (node.hasProperty(key))
            {
                return node.getProperty(key).getString();
            }
        }
        if (followTemplate)
        {
        	TemplateNode template = getTemplate();
            if (template != null)
            {
                return template.getVariableValue(key);
            }
            else
            {
            	return SitePlugin.get().getGlobalVariableValue(getSession(), key);
            }
        }    
        return null;
    }
    
    public String getVariableValue(String key)
    {
    	return getVariableValue(key, true);
    }

    public void setVariableValue(String key, String value)
    {
        final JcrNode node;
        if (hasNode(VARIABLES_NODE_NAME))
        {
            node = getNode(VARIABLES_NODE_NAME);
        }
        else
        {
            node = addNode(VARIABLES_NODE_NAME, "nt:unstructured");
        }
        node.setProperty(key, value);        
    }

    /**
     * Returns collection of possible variable keys for this node.
     */
    public Collection<String> getVariableKeys()
    {
        Set<String> keys = new HashSet<String>();
        PageMarkupSource source = new PageMarkupSource(this);
        VariableTransformer transfomer = new VariableTransformer(source, this);
        Item i = transfomer.nextMarkupItem();
        while (i != null)
        {
            if (i instanceof VariableKeyProvider)
            {
                Collection<String> k = ((VariableKeyProvider)i).getVariableKeys();
                if (k != null)
                {
                    keys.addAll(k);
                }
            }
            i = transfomer.nextMarkupItem();
        }
        
        keys.addAll(SitePlugin.get().getGlobalVariableKeys(getSession()));
        
        return keys;
    }


    public List<String> getSavedVariableKeys()
    {
        if (hasNode(VARIABLES_NODE_NAME))
        {
            JcrNode node = getNode(VARIABLES_NODE_NAME);
            List<String> result = new ArrayList<String>();
            JcrPropertyIterator i = node.getProperties();
            while (i.hasNext())
            {
            	String name = i.nextProperty().getName();
            	// filter out jcr: properties (or other possible brix properties)
            	if (!name.contains(":"))
            	{            		
            		result.add(name);
            	}
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public BrixNode getTileNode(String id)
    {
    	BrixNode node = null;
    	AbstractContainer container = this;
    	while (node == null && container != null)
    	{
    		node = container.tiles().getTile(id);
    		container = container.getTemplate();
    	}
    	if (node == null)
    	{
    		container =  SitePlugin.get().getGlobalContainer(getSession());
    		if (container != null)
    		{
    			node = container.tiles().getTile(id);
    		}
    	}
    	return node;
    }
    
    public Collection<String> getTileIDs()
    {
    	 Set<String> keys = new HashSet<String>();
         PageMarkupSource source = new PageMarkupSource(this);         
         Item i = source.nextMarkupItem();
         while (i != null)
         {
        	 if (i instanceof TileTag)
        	 {
        		 keys.add(((TileTag)i).getTileName());
        	 }
        	 i = source.nextMarkupItem();
         }
         
         keys.addAll(SitePlugin.get().getGlobalTileIDs(getSession()));
        
         return keys;
    }
}
