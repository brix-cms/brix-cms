package brix.plugin.site.admin;

import java.util.Collection;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SiteNavigationTreeNode;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.ConvertNodeAction;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.util.TextLink;

public class ConvertNodePanel extends NodeManagerPanel
{

    public ConvertNodePanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        RepeatingView<?> converters = new RepeatingView<Void>("converters");
        add(converters);

        BrixNode node = getNode();
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

                    WebMarkupContainer<?> item = new WebMarkupContainer<Void>(converters.newChildId());
                    converters.add(item);

                    Model<String> typeName = new Model<String>(plugin.getNodeType());
                    item.add(new TextLink("convert", typeName, new Model<String>(plugin.getName()))
                    {

                        @Override
                        public void onClick()
                        {
                            final String type = (String)getModelObject();
                            convertToType(type);
                        }

                    });
                }
            }
        }

        setVisible(found);
    }

    private void convertToType(String type)
    {
        final BrixNode node = getNode();

        node.checkout();
        SitePlugin.get().getNodePluginForType(type).getConverterForNode(getNode()).convert(node);
        node.save();
        node.checkin();
        
        getModel().detach();

        NavigationTreeNode treeNode = new SiteNavigationTreeNode(getNode());
        getNavigation().selectNode(treeNode);

    }

}
