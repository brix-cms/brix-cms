package brix.plugin.menu;


import java.util.List;

import javax.jcr.ReferentialIntegrityException;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrSession;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.editor.MenuEditor;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.util.AbstractModel;
import brix.workspace.Workspace;

public class ManageMenuPanel extends NavigationAwarePanel<Workspace>
{
    public ManageMenuPanel(String id, final IModel<Workspace> model)
    {
        super(id, model);

        IModel<List<BrixNode>> listViewModel = new LoadableDetachableModel<List<BrixNode>>()
        {
            @Override
            protected List<BrixNode> load()
            {
                return MenuPlugin.get().getMenuNodes(ManageMenuPanel.this.getModelObject().getId());
            }
        };

        add(new MenuListView("listView", listViewModel));
        setupEditor();

        add(new Link<Object>("newMenu")
        {
            @Override
            public void onClick()
            {
                currentMenu = new Menu();
                currentNode.setObject(null);
                setupEditor();
            }

            @Override
            public boolean isEnabled()
            {
                return currentNode.getObject() != null;
            }
        });
    }

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
                    onSelectLinkClicked(item.getModelObject());
                }

                @Override
                public boolean isEnabled()
                {
                    return item.getModelObject().equals(currentNode.getObject()) != true;
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
            select.add(new Label<String>("label", labelModel));
            item.add(select);
        }

        @Override
        protected IModel<BrixNode> getListItemModel(IModel<List<BrixNode>> listViewModel, int index)
        {
            List<BrixNode> nodes = listViewModel.getObject();
            return new BrixNodeModel(nodes.get(index));
        }
    };

    private void onSelectLinkClicked(BrixNode node)
    {
        currentNode.setObject(node);
        currentMenu = new Menu();
        currentMenu.load(node);
        setupEditor();
    }

    private void setupEditor()
    {
        Component<Menu> editor = new EditorPanel("editor", new PropertyModel<Menu>(this,
            "currentMenu"));
        if (this.editor == null)
        {
            add(editor);
        }
        else
        {
            this.editor.replaceWith(editor);
        }
        this.editor = editor;
    }

    private class EditorPanel extends Panel<Menu>
    {

        public EditorPanel(String id, final IModel<Menu> model)
        {
            super(id, model);

            Form<Menu> form = new Form<Menu>("form", new CompoundPropertyModel<Menu>(model));
            add(form);

            form.add(new TextField<String>("name").setRequired(true));

            form.add(new MenuEditor("editor", model));

            form.add(new Button<Object>("save")
            {
                @Override
                public void onSubmit()
                {
                    MenuPlugin plugin = MenuPlugin.get();
                    currentNode.setObject(plugin.saveMenu(model.getObject(), ManageMenuPanel.this
                        .getModelObject().getId(), currentNode.getObject()));
                }
            });

            form.add(new Button<Object>("delete")
            {
                @Override
                public void onSubmit()
                {
                    try
                    {
                        JcrSession session = currentNode.getObject().getSession();
                        currentNode.getObject().remove();
                        session.save();

                        currentNode.setObject(null);
                        currentMenu = new Menu();
                        setupEditor();
                    }
                    catch (JcrException e)
                    {
                        if (e.getCause() instanceof ReferentialIntegrityException)
                        {
                            currentNode.getObject().getSession().refresh(false);
                            currentNode.detach();
                            getSession().error(
                                "Couldn't delete menu, it is referenced from a tile(s).");
                        }
                    }

                }

                @Override
                public boolean isVisible()
                {
                    return currentNode.getObject() != null;
                }
            }.setDefaultFormProcessing(false));

            add(new FeedbackPanel("feedback"));
        }

    };

    private Component<Menu> editor;

    private IModel<BrixNode> currentNode = new BrixNodeModel();
    private Menu currentMenu = new Menu();

    @Override
    protected void onDetach()
    {
        currentNode.detach();
        currentMenu.detach();
        super.onDetach();
    }
}
