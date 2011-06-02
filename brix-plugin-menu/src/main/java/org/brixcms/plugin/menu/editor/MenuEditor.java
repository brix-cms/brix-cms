/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.plugin.menu.editor;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.EditablePropertyTreeColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.plugin.menu.ManageMenuPanel;
import org.brixcms.plugin.menu.Menu;
import org.brixcms.plugin.menu.Menu.ChildEntry;
import org.brixcms.plugin.menu.editor.cell.SwitcherColumn;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorConfiguration;
import org.brixcms.web.generic.BrixGenericPanel;

import java.util.ArrayList;
import java.util.List;

public class MenuEditor extends BrixGenericPanel<Menu> {
    private MenuTreeModel treeModel;
    private AbstractTree tree;

    private WebMarkupContainer links;

    public MenuEditor(String id) {
        super(id);
    }

    public MenuEditor(String id, IModel<Menu> model) {
        super(id, model);
    }

    @Override
    protected void onBeforeRender() {
        if (!hasBeenRendered()) {
            init();
        }
        super.onBeforeRender();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        treeModel = new MenuTreeModel(getModelObject().getRoot());

        final TreeGrid tg;
        add(tg = new TreeGrid("treeGrid", treeModel, newColumns()) {
            @Override
            protected void onItemSelectionChanged(IModel item, boolean newValue) {
                super.onItemSelectionChanged(item, newValue);

                //if (newValue == false)
                setItemEdit(item, newValue);

                selectionChanged(AjaxRequestTarget.get());
                // update();
            }

            @Override
            public void setItemEdit(IModel rowModel, boolean edit) {
                if (edit == true) {
                    selectItem(rowModel, true);
                }

                super.setItemEdit(rowModel, edit);
            }
        });

        tree = tg.getTree();
        tg.setClickRowToSelect(true);
        tg.setClickRowToDeselect(true);
        tg.setSelectToEdit(false);
        tree.setRootLess(true);
        tg.setContentHeight(34, SizeUnit.EM);

        tree.getTreeState().expandAll();
        tree.getTreeState().setAllowSelectMultiple(false);
        // tree.getTreeState().selectNode(treeModel.getRoot(), true);

        links = new WebMarkupContainer("links");
        links.setOutputMarkupId(true);

        links.add(new AjaxLink("addTopLevel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                MenuTreeNode parent = (MenuTreeNode) treeModel.getRoot();
                ChildEntry entry = new ChildEntry(parent.getEntry());
                entry.setTitle(getString("newEntry"));
                parent.getEntry().getChildren().add(entry);
                MenuTreeNode node = new MenuTreeNode(entry);
                treeModel.nodeInserted(tree, parent, node);
                tree.getTreeState().selectNode(node, true);
                tree.updateTree();
            }
        });

        links.add(new AjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ChildEntry entry = new ChildEntry(getSelected().getEntry());
                entry.setTitle(getString("newEntry"));
                getSelected().getEntry().getChildren().add(entry);
                MenuTreeNode node = new MenuTreeNode(entry);
                treeModel.nodeInserted(tree, getSelected(), node);
                tree.getTreeState().selectNode(node, true);
                tree.updateTree();
            }

            @Override
            public boolean isEnabled() {
                return !getSelected().equals(treeModel.getRoot());
            }
        });

        links.add(new AjaxLink("remove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                MenuTreeNode selected = getSelected();
                MenuTreeNode parent = (MenuTreeNode) tree.getParentNode(selected);

                boolean editing = tg.isItemEdited(new Model<MenuTreeNode>(selected));
                int index = parent.getChildren().indexOf(selected);
                treeModel.nodeDeleted(tree, selected);
                parent.getEntry().getChildren().remove(selected.getEntry());

                if (index > parent.getChildren().size() - 1) {
                    --index;
                }

                MenuTreeNode newSelected = (MenuTreeNode) ((index >= 0) ? parent.getChildren().get(index) : parent);
                if (newSelected.equals(treeModel.getRoot()) == false) {
                    tree.getTreeState().selectNode(newSelected, true);
                    tg.setItemEdit(new Model<MenuTreeNode>(newSelected), editing);
                }
                tree.updateTree();
            }

            @Override
            public boolean isEnabled() {
                return getSelected() != treeModel.getRoot();
            }
        });

        links.add(new AjaxLink("moveUp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                MenuTreeNode selected = getSelected();
                int index = getIndex(selected);
                if (index > 0) {
                    boolean editing = tg.isItemEdited(new Model<MenuTreeNode>(selected));

                    MenuTreeNode parent = (MenuTreeNode) tree.getParentNode(selected);
                    treeModel.nodeDeleted(tree, selected);
                    parent.getEntry().getChildren().remove(selected.getEntry());
                    parent.getEntry().getChildren().add(index - 1, (ChildEntry) selected.getEntry());
                    treeModel.nodeInserted(tree, parent, selected);


                    tree.getTreeState().selectNode(selected, true);
                    tg.setItemEdit(new Model<MenuTreeNode>(selected), editing);

                    tree.updateTree();
                }
                target.addComponent(links);
            }

            @Override
            public boolean isEnabled() {
                return getIndex(getSelected()) > 0;
            }
        });

        links.add(new AjaxLink("moveDown") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                MenuTreeNode selected = getSelected();
                MenuTreeNode parent = (MenuTreeNode) tree.getParentNode(selected);
                int index = getIndex(selected);
                if (index < parent.getChildren().size() - 1) {
                    boolean editing = tg.isItemEdited(new Model<MenuTreeNode>(selected));
                    treeModel.nodeDeleted(tree, selected);
                    parent.getEntry().getChildren().remove(selected.getEntry());
                    parent.getEntry().getChildren().add(index + 1, (ChildEntry) selected.getEntry());
                    treeModel.nodeInserted(tree, parent, selected);

                    tree.getTreeState().selectNode(selected, true);
                    tg.setItemEdit(new Model<MenuTreeNode>(selected), editing);

                    tree.updateTree();
                }
                target.addComponent(links);
            }

            @Override
            public boolean isEnabled() {
                int index = getIndex(getSelected());
                MenuTreeNode parent = (MenuTreeNode) tree.getParentNode(getSelected());
                return parent != null && index < parent.getChildren().size() - 1;
            }
        });

        add(links);

        selectionChanged(null);
    }

    private List<IGridColumn> newColumns() {
        List<IGridColumn> columns = new ArrayList<IGridColumn>();

        final ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();
        ManageMenuPanel panel = findParent(ManageMenuPanel.class);
        conf.setWorkspaceName(panel.getModelObject().getId());

        columns.add(new SubmitCancelColumn("submitCancel1", new ResourceModel("edit")));
        columns.add(new EditablePropertyTreeColumn(new ResourceModel("title"), "entry.title") {
            @Override
            protected boolean isClickToEdit() {
                return false;
            }
        }.setInitialSize(300));
        columns.add(new EditablePropertyColumn(new ResourceModel("cssClass"), "entry.cssClass") {
            @Override
            protected boolean isClickToEdit() {
                return false;
            }
        }.setInitialSize(200));

        columns.add(new SwitcherColumn("referenceEditor", new ResourceModel("referenceLabelCode"), conf).setInitialSize(400));

        columns.add(new SubmitCancelColumn("submitCancel2", new ResourceModel("edit")));

        return columns;
    }

    private int getIndex(MenuTreeNode node) {
        MenuTreeNode parent = (MenuTreeNode) tree.getParentNode(node);
        if (parent == null) {
            return -1;
        } else {
            return parent.getChildren().indexOf(node);
        }
    }

    private MenuTreeNode getSelected() {
        if (!tree.getTreeState().getSelectedNodes().isEmpty()) {
            MenuTreeNode node = (MenuTreeNode) tree.getTreeState().getSelectedNodes().iterator().next();
            return node;
        } else {
            return (MenuTreeNode) treeModel.getRoot();
        }
    }

    private void selectionChanged(AjaxRequestTarget target) {
        if (target != null) {
            target.addComponent(links);
        }
    }
}
