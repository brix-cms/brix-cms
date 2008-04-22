package brix.plugin.site.node.folder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import brix.BrixNodeModel;
import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.auth.NodeAction;
import brix.auth.impl.NodeActionImpl;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SiteNavigationTreeNode;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;

public class ListFolderNodesTab extends NavigationAwarePanel<JcrNode>
{
    public ListFolderNodesTab(String id, IModel<JcrNode> folderModel)
    {
        super(id, folderModel);
        add(new DataView("dir-listing", new DirListing())
        {

            @Override
            protected void populateItem(final Item item)
            {
                Link select = new Link("select", item.getModel())
                {

                    @Override
                    public void onClick()
                    {
                        JcrNode node = (JcrNode)getModelObject();
                        NavigationTreeNode treeNode = new SiteNavigationTreeNode(node);
                        getNavigation().selectNode(treeNode);
                    }

                };
                item.add(select);
                select.add(new Label("file-name", new PropertyModel(item.getModel(), "name")));

                IModel<SiteNodePlugin> pluginModel = new IModel<SiteNodePlugin>()
                {
                    public void detach()
                    {
                    }

                    public SiteNodePlugin getObject()
                    {
                        return SitePlugin.get().getNodePluginForNode((JcrNode)item.getModelObject());
                    }

                    public void setObject(SiteNodePlugin object)
                    {
                    }
                };

                 item.add(new Label("type", new PropertyModel(pluginModel, "name")));

                // item.add(new Label("size", new PropertyModel(item.getModel(),
                // "sizeInBytes")));
                // item.add(new Label("revision", new PropertyModel(item
                // .getModel(), "revision")));

                item.add(new Label("author", new PropertyModel(item.getModel(), "lastModifiedBy")));
                item.add(new Label("modified", new PropertyModel(item.getModel(), "lastModified"))
                {
                    @SuppressWarnings("unchecked")
                    @Override
                    public IConverter getConverter(Class type)
                    {
                        return ModifiedConverter.INSTANCE;
                    }
                });

                IModel mimeTypeModel = new LoadableDetachableModel()
                {
                    @Override
                    protected Object load()
                    {
                        BrixNode node = (BrixNode)item.getModelObject();
                        if (node.isFolder())
                        {
                            return "Folder";
                        }
                        else if (node instanceof BrixFileNode)
                        {
                            return ((BrixFileNode)node).getMimeType();
                        }
                        else
                        {
                            return "Unknown";
                        }
                    }
                };
                item.add(new Label("mime", mimeTypeModel));
            }

        });
    }

    private static class ModifiedConverter implements IConverter
    {
        public Object convertToObject(String value, Locale locale)
        {
            return null;
        }

        public String convertToString(Object value, Locale locale)
        {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            return df.format((Date)value);
        }

        public static final ModifiedConverter INSTANCE = new ModifiedConverter();
    };

    private JcrNode getNode()
    {
        return (JcrNode)getModelObject();
    }

    private class DirListing implements IDataProvider
    {

        @SuppressWarnings("unchecked")
        public Iterator iterator(int first, int count)
        {
            return visibleNodes(getNode().getNodes());
        }

        public IModel model(Object object)
        {
            return new BrixNodeModel((JcrNode)object);
        }

        public int size()
        {
            return (int)getNode().getNodes().getSize();
        }

        public void detach()
        {
        }

    }

    private Iterator<JcrNode> visibleNodes(JcrNodeIterator iterator)
    {
        List<JcrNode> res = new ArrayList<JcrNode>();
        while (iterator.hasNext())
        {
            BrixNode node = (BrixNode)iterator.nextNode();
            Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                    NodeAction.Type.NODE_VIEW, node);
            if (!node.isHidden() &&
                    Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
            {
                res.add(node);
            }
        }
        return res.iterator();
    }
}
