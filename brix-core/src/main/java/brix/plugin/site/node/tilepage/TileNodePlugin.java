package brix.plugin.site.node.tilepage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.node.resource.ResourceNodePlugin;
import brix.plugin.site.node.tilepage.admin.PageManagerPanel;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.exception.TileAlreadyRegisteredException;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.tile.unknown.UnknownTile;

public abstract class TileNodePlugin implements SiteNodePlugin
{

    private Map<String, Tile> tiles = new HashMap<String, Tile>();

    public abstract String getNodeType();

    public NavigationAwarePanel newManageNodePanel(String id, IModel<JcrNode> nodeModel)
    {
        return new PageManagerPanel(id, nodeModel);
    }

    private final BrixNodePageUrlCodingStrategy urlCodingStrategy = new BrixNodePageUrlCodingStrategy()
    {
        @Override
        protected BrixNodeWebPage newPageInstance(IModel<JcrNode> nodeModel,
                BrixPageParameters pageParameters)
        {
            return new TilePageRenderPage(nodeModel, pageParameters);
        }
    };



    public IRequestTarget respond(IModel<JcrNode> nodeModel, RequestParameters requestParameters)
    {
        return urlCodingStrategy.decode(requestParameters, nodeModel);
    }

    public abstract NavigationAwarePanel newCreateNodePanel(String id, IModel<JcrNode> parentNode);

    public NodeConverter getConverterForNode(JcrNode node)
    {
        BrixFileNode fileNode = (BrixFileNode)node;
        if (ResourceNodePlugin.TYPE.equals(fileNode.getNodeType()))
        {
            String mimeType = fileNode.getMimeType();
            if (mimeType != null &&
                    (mimeType.startsWith("text/") || mimeType.equals("application/xml")))
                return new FromResourceConverter(getNodeType());
        }

        return null;
    }

    private static class FromResourceConverter extends SetTypeConverter
    {
        public FromResourceConverter(String type)
        {
            super(type);
        }
    };

    protected static class SetTypeConverter implements NodeConverter
    {
        private final String type;

        public SetTypeConverter(String type)
        {
            this.type = type;
        }

        public void convert(JcrNode node)
        {
            ((BrixNode)node).setNodeType(type);
        }
    }

    public Tile getTileOfType(String type)
    {
        Tile tile = tiles.get(type);
        if (tile != null)
        {
            return tile;
        }
        else
        {
            return UnknownTile.INSTANCE;
        }
    }

    public void addTile(Tile tile)
    {
        if (tiles.containsKey(tile.getTypeName()))
        {
            throw new TileAlreadyRegisteredException("Tile with typeName '" + tile.getTypeName() +
                    "' is already registered.");
        }
        tiles.put(tile.getTypeName(), tile);
    }

    public Collection<Tile> getTiles()
    {
        return Collections.unmodifiableCollection(tiles.values());
    }

}
