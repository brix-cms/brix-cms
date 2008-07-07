package brix.web.tile.menu;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.site.page.tile.admin.GenericTileEditorPanel;
import brix.web.util.AbstractModel;

public class MenuTileEditor extends GenericTileEditorPanel<BrixNode>
{

    public MenuTileEditor(String id, IModel<BrixNode> containerNode)
    {
        super(id, containerNode);

        IModel<List<BrixNode>> listViewModel = new LoadableDetachableModel<List<BrixNode>>()
        {
            @Override
            protected List<BrixNode> load()
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
        form.add(new TextField<String>("selectedItemStyleClass"));
        form.add(new TextField<String>("itemWithSelectedChildStyleClass"));
        form.add(new TextField<Integer>("startAtLevel"));
        form.add(new TextField<Integer>("renderLevels"));
    }

    @Override
    public void load(BrixNode node)
    {
        currentEntry = new MenuContainer();
        currentEntry.load(node);
    }

    @Override
    public void save(BrixNode node)
    {
        currentEntry.save(node);
    }

    private MenuContainer currentEntry = new MenuContainer();

    private class MenuListView extends ListView<BrixNode>
    {
        public MenuListView(String id, IModel<List<BrixNode>> model)
        {
            super(id, model);

        }

        @Override
        protected void populateItem(final ListItem<BrixNode> item)
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
                    BrixNode current = currentEntry.getMenuNode();
                    return current == null || !item.getModelObject().equals(current);
                }
            };
            IModel<String> labelModel = new AbstractModel<String>()
            {
                @Override
                public String getObject()
                {
                    BrixNode node = item.getModelObject();
                    Menu menu = new Menu();
                    menu.loadName(node);
                    return menu.getName();
                }
            };
            select.add(new Label("label", labelModel));
            item.add(select);
        }

        @Override
        protected IModel<BrixNode> getListItemModel(IModel<List<BrixNode>> listViewModel, int index)
        {
            List<BrixNode> nodes = listViewModel.getObject();
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
