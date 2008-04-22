package brix.web.tile.treemenu;

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

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.picker.reference.ReferenceEditorConfiguration;
import brix.web.picker.reference.ReferenceEditorModalWindow;
import brix.web.picker.reference.ReferenceEditorPanel;
import brix.web.tile.treemenu.TreeMenuTile.Item;
import brix.web.tile.treemenu.TreeMenuTile.RootItem;

public class TreeMenuTileEditorPanel extends TileEditorPanel
{

    private static final String VERSION = "1.0";
    private final RootItem root = new RootItem();

    private ReferenceEditorModalWindow referenceEditor;

    public TreeMenuTileEditorPanel(String id, IModel<JcrNode> containerNode)
    {
        super(id, containerNode);

        add(new TextField("containerCssId", new PropertyModel(this, "root.containerCssId"))
                .setLabel(new Model("Container Css Id")));
        add(new TextField("selectedCssClass", new PropertyModel(this, "root.selectedCssClass"))
                .setLabel(new Model("selected Css Class")));

        ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();
        conf.setWorkspaceName(containerNode);
        referenceEditor = new ReferenceEditorModalWindow("referenceEditor", null, conf);
        add(referenceEditor);

        add(new Button("add-root-child")
        {
            @Override
            public void onSubmit()
            {
                root.getChildren().add(new Item());
            }
        }.setDefaultFormProcessing(false));

        add(new ListView("items", new PropertyModel(this, "root.children"))
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

    private abstract class ItemViewer extends Fragment
    {
        public ItemViewer(String id, final IModel model)
        {
            super(id, "item-viewer-fragment", TreeMenuTileEditorPanel.this,
                    new CompoundPropertyModel(model));

            add(new TextField("name").setRequired(true).setLabel(new Model("Name")));

            add(new TextField("containerCssId").setLabel(new Model("Container Css Id")));
            add(new TextField("itemCssId").setLabel(new Model("Item Css Id")));

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
    public void load(JcrNode node)
    {
        TreeMenuTile.load(root, node);
    }

    @Override
    public void save(JcrNode node)
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
