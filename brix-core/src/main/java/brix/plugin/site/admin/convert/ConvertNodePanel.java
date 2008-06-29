package brix.plugin.site.admin.convert;

import java.util.Collection;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.auth.ConvertNodeAction;
import brix.web.util.TextLink;

public class ConvertNodePanel extends NodeManagerPanel
{

    public ConvertNodePanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        RepeatingView converters = new RepeatingView("converters");
        add(converters);

        BrixNode node = getModelObject();
        Collection<SiteNodePlugin> plugins = SitePlugin.get().getNodePlugins();

        boolean found = false;

        for (SiteNodePlugin plugin : plugins)
        {
            if (plugin.getConverterForNode(node) != null)
            {

                Action action = new ConvertNodeAction(Action.Context.ADMINISTRATION, node,
                        plugin.getNodeType());

                if (node.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
                {

                    found = true;

                    WebMarkupContainer item = new WebMarkupContainer(converters.newChildId());
                    converters.add(item);

                    Model<String> typeName = new Model<String>(plugin.getNodeType());
                    item.add(new TextLink<String>("convert", typeName, new Model<String>(plugin.getName()))
                    {
                        @Override
                        public void onClick()
                        {
                            final String type = (String)getModelObject();
                            convertToType(type);
                            getSession().info(getString("nodeConverted"));
                        }
                    });
                }
            }
        }

        setVisible(found);
    }

    private void convertToType(String type)
    {
        final BrixNode node = getModelObject();

        node.checkout();
        SitePlugin.get().getNodePluginForType(type).getConverterForNode(getModelObject()).convert(node);
        node.save();
        node.checkin();
        
        getModel().detach();

        SitePlugin.get().selectNode(this, getModelObject());
    }

}
