package brix.web.tile.menu;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.util.AbstractModel;

public class MenuTileEditor extends TileEditorPanel<JcrNode>
{

    public MenuTileEditor(String id, IModel<JcrNode> containerNode)
    {
        super(id, containerNode);

        IModel<List<JcrNode>> listViewModel = new LoadableDetachableModel<List<JcrNode>>()
        {
            @Override
            protected List<JcrNode> load()
            {
                return MenuPlugin.get().getMenuNodes(
                    MenuTileEditor.this.getModelObject().getSession().getWorkspace().getName());
            }
        };

        add(new MenuListView("listView", listViewModel));

        Form<MenuContainer> form;
        add(form = new Form<MenuContainer>("form", new CompoundPropertyModel<MenuContainer>(
            new PropertyModel<MenuContainer>(this, "currentEntry"))));

        form.add(new TextField<String>("outerContainerStyleClass"));
        form.add(new TextField<String>("innerContainerStyleClass"));
        form.add(new TextField<String>("itemStyleClass"));
        form.add(new TextField<String>("selectedItemStyleClass"));
    }

    @Override
    public void load(JcrNode node)
    {
        currentEntry = new MenuContainer();
        currentEntry.load(node);
    }

    @Override
    public void save(JcrNode node)
    {
        currentEntry.save(node);
    }

    private MenuContainer currentEntry = new MenuContainer();

    private class MenuListView extends ListView<JcrNode>
    {
        public MenuListView(String id, IModel<List<JcrNode>> model)
        {
            super(id, model);

        }

        @Override
        protected void populateItem(final ListItem<JcrNode> item)
        {
            Link<Object> select = new Link<Object>("select")
            {
                @Override
                public void onClick()
                {
                    currentEntry.setMenuNode(item.getModelObject());
                }

                @Override
                public boolean isEnabled()
                {
                    JcrNode current = currentEntry.getMenuNode();
                    return current == null || !item.getModelObject().equals(current);
                }
            };
            IModel<String> labelModel = new AbstractModel<String>()
            {
                @Override
                public String getObject()
                {
                    JcrNode node = item.getModelObject();
                    Menu menu = new Menu();
                    menu.loadName(node);
                    return menu.getName();
                }
            };
            select.add(new Label<String>("label", labelModel));
            item.add(select);
        }

        @Override
        protected IModel<JcrNode> getListItemModel(IModel<List<JcrNode>> listViewModel, int index)
        {
            List<JcrNode> nodes = listViewModel.getObject();
            return new BrixNodeModel(nodes.get(index));
        }
    };

    @Override
    protected void onDetach()
    {
        if (currentEntry != null)
        {
            currentEntry.detach();
        }
        super.onDetach();
    }
}
