package brix.plugin.menu.editor;

import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.plugin.menu.Menu;
import brix.plugin.menu.Menu.ChildEntry;

public class MenuEditor extends Panel<Menu>
{

    public MenuEditor(String id, IModel<Menu> model)
    {
        super(id, model);
        init();
    }

    public MenuEditor(String id)
    {
        super(id);
        init();
    }

    private MenuTreeModel treeModel;
    private LinkTree tree;

    private MenuTreeNode getSelected()
    {
        MenuTreeNode node = (MenuTreeNode)tree.getTreeState().getSelectedNodes().iterator().next();
        return node;
    }

    @SuppressWarnings("unchecked")
    private void init()
    {
        treeModel = new MenuTreeModel(getModelObject().getRoot());

        add(tree = new LinkTree("tree", treeModel)
        {
            @Override
            protected void onNodeLinkClicked(TreeNode node, BaseTree tree, AjaxRequestTarget target)
            {
                super.onNodeLinkClicked(node, tree, target);
                selectionChanged(target);
            }

            @Override
            protected ITreeState newTreeState()
            {
                return new DefaultTreeState()
                {
                    @Override
                    public void selectNode(Object node, boolean selected)
                    {
                        if (selected)
                            super.selectNode(node, selected);
                    }
                };
            }
        });

        tree.getTreeState().expandAll();
        tree.getTreeState().setAllowSelectMultiple(false);
        tree.getTreeState().selectNode((TreeNode)treeModel.getRoot(), true);

        links = new WebMarkupContainer("links");
        links.setOutputMarkupId(true);

        links.add(new AjaxLink("add")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                ChildEntry entry = new ChildEntry(getSelected().getEntry());
                entry.setTitle(getString("newEntry"));
                getSelected().getEntry().getChildren().add(entry);
                MenuTreeNode node = getSelected().nodeForEntry(entry);
                treeModel.nodeInserted(node);
                tree.getTreeState().selectNode(node, true);
                tree.updateTree();
                selectionChanged(target);
            }
        });

        links.add(new AjaxLink("remove")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                MenuTreeNode selected = getSelected();
                MenuTreeNode parent = (MenuTreeNode)selected.getParent();
                int index = parent.getIndex(selected);
                parent.getEntry().getChildren().remove(selected.getEntry());

                if (index > parent.getChildCount() - 1)
                {
                    --index;
                }

                MenuTreeNode newSelected = (MenuTreeNode)((index >= 0)
                        ? parent.getChildAt(index)
                        : parent);

                tree.getTreeState().selectNode(newSelected, true);
                selectionChanged(target);
                treeModel.nodeDeleted(selected);
                tree.updateTree();
            }

            @Override
            public boolean isEnabled()
            {
                return getSelected() != treeModel.getRoot();
            }
        });

        links.add(new AjaxLink("moveUp")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                MenuTreeNode selected = getSelected();
                int index = getIndex(selected);
                if (index > 0)
                {
                    treeModel.nodeDeleted(selected);
                    MenuTreeNode parent = (MenuTreeNode)selected.getParent();
                    parent.getEntry().getChildren().remove(selected.getEntry());
                    parent.getEntry().getChildren().add(index - 1, (ChildEntry)selected.getEntry());
                    treeModel.nodeInserted(selected);
                    tree.updateTree();
                }
                target.addComponent(links);
            }

            @Override
            public boolean isEnabled()
            {
                return getSelected().getParent() != null && getIndex(getSelected()) > 0;
            }
        });

        links.add(new AjaxLink("moveDown")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                MenuTreeNode selected = getSelected();
                MenuTreeNode parent = (MenuTreeNode)selected.getParent();
                int index = getIndex(selected);
                if (index < parent.getChildCount() - 1)
                {
                    treeModel.nodeDeleted(selected);
                    parent.getEntry().getChildren().remove(selected.getEntry());
                    parent.getEntry().getChildren().add(index + 1, (ChildEntry)selected.getEntry());
                    treeModel.nodeInserted(selected);
                    tree.updateTree();
                }
                target.addComponent(links);
            }

            @Override
            public boolean isEnabled()
            {
                return getSelected().getParent() != null &&
                        getIndex(getSelected()) < getSelected().getParent().getChildCount() - 1;
            }
        });

        add(links);

        selectionChanged(null);
    }

    private int getIndex(MenuTreeNode node)
    {
        return node.getParent().getIndex(node);
    }

    private WebMarkupContainer<?> links;
    private Component<?> editor;

    private static final String EDITOR_ID = "editor";

    private void selectionChanged(AjaxRequestTarget target)
    {
        Component<?> c;
        if (getSelected().getEntry() instanceof ChildEntry)
        {
            c = new ChildPanel(EDITOR_ID, new Model<ChildEntry>() {
                @Override
                public ChildEntry getObject()
                {
                    return (ChildEntry)MenuEditor.this.getSelected().getEntry();
                }
                @Override
                public void detach()
                {
                    MenuEditor.this.getSelected().getEntry().detach();
                }
            }) {
                @Override
                protected void onUpdate()
                {
                    treeModel.nodeChanged(getSelected());
                    tree.updateTree();
                }
            };
        } 
        else
        {
            c = new RootPanel(EDITOR_ID);
        }
        c.setOutputMarkupId(true);
        
        if (editor != null)
        {
            editor.replaceWith(c);
        }
        else
        {
            add(c);            
        }
        editor = c;
        
        if (target != null)
        {
            target.addComponent(links);
            target.addComponent(c);
        }
    }

}
