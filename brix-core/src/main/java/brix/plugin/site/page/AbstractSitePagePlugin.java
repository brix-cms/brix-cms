package brix.plugin.site.page;

import java.util.Collection;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.Brix;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.admin.ManageTileNodeTabFactory;
import brix.plugin.site.resource.ResourceNodePlugin;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;

public abstract class AbstractSitePagePlugin implements SiteNodePlugin
{
    public AbstractSitePagePlugin(Brix brix)
    {
        registerManageNodeTabFactory(brix);
    }

    private void registerManageNodeTabFactory(Brix brix)
    {
        Collection<ManageNodeTabFactory> factories = brix.getConfig().getRegistry()
            .lookupCollection(ManageNodeTabFactory.POINT);
        boolean found = false;
        for (ManageNodeTabFactory f : factories)
        {
            if (f instanceof ManageTileNodeTabFactory)
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            SitePlugin sp = SitePlugin.get(brix);
            sp.registerManageNodeTabFactory(new ManageTileNodeTabFactory());
        }
    }

    public abstract String getNodeType();

    private final BrixNodePageUrlCodingStrategy urlCodingStrategy = new BrixNodePageUrlCodingStrategy()
    {
        @Override
        protected BrixNodeWebPage newPageInstance(IModel<BrixNode> nodeModel,
                BrixPageParameters pageParameters)
        {
            return new PageRenderingPage(nodeModel, pageParameters);
        }
    };

    public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters)
    {
        return urlCodingStrategy.decode(requestParameters, nodeModel);
    }

    public abstract Panel<?> newCreateNodePanel(String id, IModel<BrixNode> parentNode);

    public NodeConverter getConverterForNode(BrixNode node)
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

        public void convert(BrixNode node)
        {
            ((BrixNode)node).setNodeType(type);
        }
    }



}
