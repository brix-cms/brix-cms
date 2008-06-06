package brix.web.tile.treemenu;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.picker.reference.ReferenceEditorConfiguration;
import brix.web.picker.reference.ReferenceEditorModalWindow;
import brix.web.picker.reference.ReferenceEditorPanel;
import brix.web.tile.treemenu.TreeMenuTile.Item;
import brix.web.tile.treemenu.TreeMenuTile.RootItem;

public class TreeMenuTileEditorPanel extends TileEditorPanel<BrixNode>
{

    private static final String VERSION = "1.0";
    private final RootItem root = new RootItem();

    private ReferenceEditorModalWindow referenceEditor;

    public TreeMenuTileEditorPanel(String id, IModel<BrixNode> containerNode)
    {
        super(id, containerNode);

        add(new TextField<String>("containerCssId", new PropertyModel<String>(this, "root.containerCssId"))
                .setLabel(new Model<String>("Container Css Id")));
        add(new TextField<String>("selectedCssClass", new PropertyModel<String>(this, "root.selectedCssClass"))
                .setLabel(new Model<String>("selected Css Class")));

        ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();
        conf.setWorkspaceName(containerNode);
        referenceEditor = new ReferenceEditorModalWindow("referenceEditor", null, conf);
        add(referenceEditor);

        add(new Button<Void>("add-root-child")
        {
            @Override
            public void onSubmit()
            {
                root.getChildren().add(new Item());
            }
        }.setDefaultFormProcessing(false));

        add(new ListView<Item>("items", new PropertyModel<List<Item>>(this, "root.children"))
        {

            @Override
            protected void populateItem(ListItem<Item> item)
            {
                item.add(new ItemViewer("viewer", item.getModel())
                {

                    @Override
                    protected void onDelete()
                    {
                        Item deleted = (Item)getModelObject();
                        root.getChildren().remove(deleted);
                    }

                    @Override
                    protected void onAddChild()
                    {
                        ((Item)getModelObject()).getChildren().add(new Item());
                    }

                });
            }

        }.setReuseItems(true));
    }

    private abstract class ItemViewer extends Fragment<Item>
    {
        public ItemViewer(String id, final IModel<Item> model)
        {
            super(id, "item-viewer-fragment", TreeMenuTileEditorPanel.this,
                    new CompoundPropertyModel<Item>(model));

            add(new TextField<String>("name").setRequired(true).setLabel(new Model<String>("Name")));

            add(new TextField<String>("containerCssId").setLabel(new Model<String>("Container Css Id")));
            add(new TextField<String>("itemCssId").setLabel(new Model<String>("Item Css Id")));

            add(new ReferenceEditorPanel("reference", new PropertyModel(model, "reference"))
            {
                @Override
                protected Component newModalWindow(String id)
                {
                    return new WebMarkupContainer(id);
                }

                @Override
                protected ReferenceEditorModalWindow getModalWindow()
                {
                    return referenceEditor;
                }
            }.setRequired(true));

            add(new Button("delete")
            {
                @Override
                public void onSubmit()
                {
                    onDelete();
                }

            }.setDefaultFormProcessing(false));

            add(new Button("add-child")
            {
                @Override
                public void onSubmit()
                {
                    onAddChild();
                }

            }.setDefaultFormProcessing(false));

            add(new ListView("children")
            {

                @Override
                protected void populateItem(ListItem item)
                {
                    item.add(new ItemViewer("viewer", item.getModel())
                    {
                        @Override
                        protected void onDelete()
                        {
                            Item deleted = (Item)getModelObject();
                            Item parent = (Item)ItemViewer.this.getModelObject();
                            parent.getChildren().remove(deleted);
                        }

                        @Override
                        protected void onAddChild()
                        {
                            Item item = (Item)getModelObject();
                            item.getChildren().add(new Item());
                        }
                    });
                }

            }.setReuseItems(true));

        }

        protected abstract void onAddChild();

        protected abstract void onDelete();

    }

    @Override
    public void load(BrixNode node)
    {
        TreeMenuTile.load(root, node);
    }

    @Override
    public void save(BrixNode node)
    {
        TreeMenuTile.save(root, node);
    }

    @Override
    protected void onDetach()
    {
        super.onDetach();
        root.detach();
    }

}
