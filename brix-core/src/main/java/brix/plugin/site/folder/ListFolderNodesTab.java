package brix.plugin.site.folder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import brix.BrixNodeModel;
import brix.auth.Action;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;

public class ListFolderNodesTab extends Panel<BrixNode>
{
    public ListFolderNodesTab(String id, IModel<BrixNode> folderModel)
    {
        super(id, folderModel);
        add(new DataView<BrixNode>("dir-listing", new DirListing())
        {

            @Override
            protected void populateItem(final Item<BrixNode> item)
            {
                Link<BrixNode> select = new Link<BrixNode>("select", item.getModel())
                {

                    @Override
                    public void onClick()
                    {
                        BrixNode node = (BrixNode)getModelObject();
                        SitePlugin.get().selectNode(this, node);
                    }

                };
                item.add(select);
                select.add(new Label<String>("file-name", new PropertyModel<String>(item.getModel(), "name")));

                IModel<SiteNodePlugin> pluginModel = new IModel<SiteNodePlugin>()
                {
                    public void detach()
                    {
                    }

                    public SiteNodePlugin getObject()
                    {
                        return SitePlugin.get().getNodePluginForNode((BrixNode)item.getModelObject());
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

    private BrixNode getNode()
    {
        return (BrixNode)getModelObject();
    }

    private class DirListing implements IDataProvider<BrixNode>
    {

        @SuppressWarnings("unchecked")
        public Iterator iterator(int first, int count)
        {
            return visibleNodes(getNode().getNodes());
        }

        public IModel<BrixNode> model(BrixNode object)
        {
            return new BrixNodeModel(object);
        }

        public int size()
        {
            return (int)getNode().getNodes().getSize();
        }

        public void detach()
        {
        }

    }

    private Iterator<BrixNode> visibleNodes(JcrNodeIterator iterator)
    {
        List<BrixNode> res = new ArrayList<BrixNode>();
        while (iterator.hasNext())
        {
            BrixNode node = (BrixNode)iterator.nextNode();
            Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
                    SiteNodeAction.Type.NODE_VIEW, node);
            if (!node.isHidden() &&
                    node.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
            {
                res.add(node);
            }
        }
        return res.iterator();
    }
}
