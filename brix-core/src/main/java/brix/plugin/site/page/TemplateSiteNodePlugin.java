package brix.plugin.site.page;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.admin.CreatePageOrTemplatePanel;

public class TemplateSiteNodePlugin extends AbstractSitePagePlugin
{

    public static final String TYPE = Brix.NS_PREFIX + "tileTemplate";

    public TemplateSiteNodePlugin(SitePlugin plugin)
    {
        super(plugin);
    }

    @Override
    public NodeConverter getConverterForNode(BrixNode node)
    {
        if (PageSiteNodePlugin.TYPE.equals(((BrixNode)node).getNodeType()))
            return new FromPageConverter(getNodeType());
        else
            return super.getConverterForNode(node);
    }

    private static class FromPageConverter extends SetTypeConverter
    {
        public FromPageConverter(String type)
        {
            super(type);
        }
    };

    @Override
    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack)
    {
        return new CreatePageOrTemplatePanel(id, parentNode, getNodeType(), goBack);
    }

    @Override
    public String getNodeType()
    {
        return TYPE;
    }

    public String getName()
    {
        return "Template";
    }

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode)
    {
        return new Model<String>("Create New Template");
    }
}
