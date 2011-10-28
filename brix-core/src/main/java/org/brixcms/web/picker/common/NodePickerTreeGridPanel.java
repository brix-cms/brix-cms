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

package org.brixcms.web.picker.common;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.picker.node.NodePickerTreeModel;
import org.brixcms.web.tree.AbstractTreeModel;
import org.brixcms.web.tree.FilteredJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;
import org.brixcms.web.util.AbstractModel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.tree.AbstractTreeColumn;
import com.inmethod.grid.treegrid.TreeGrid;

public abstract class NodePickerTreeGridPanel extends Panel {
    private final static NodeFilter ALLOW_ALL_FILTER = new NodeFilter() {
        public boolean isNodeAllowed(BrixNode node) {
            return true;
        }
    };

    private final NodeFilter visibilityFilter;
    private final NodeFilter enabledFilter;

    private TreeGrid<NodePickerTreeModel, JcrTreeNode> grid;

    public NodePickerTreeGridPanel(String id, NodeFilter visibilityFilter, NodeFilter enabledFilter) {
        super(id);

        this.visibilityFilter = visibilityFilter;
        this.enabledFilter = enabledFilter != null ? enabledFilter : ALLOW_ALL_FILTER;
    }

    public NodePickerTreeGridPanel(String id, IModel<?> model, NodeFilter visibilityFilter, NodeFilter enabledFilter) {
        super(id, model);
        this.visibilityFilter = visibilityFilter;
        this.enabledFilter = enabledFilter != null ? enabledFilter : ALLOW_ALL_FILTER;
    }

    public TreeGrid<NodePickerTreeModel, JcrTreeNode> getGrid() {
        return grid;
    }

    public NodeFilter getVisibilityFilter() {
        return visibilityFilter;
    }

    @Override
    protected void onBeforeRender() {
        if (!hasBeenRendered()) {
            initComponents();
        }
        expandToSelectedNodes();
        super.onBeforeRender();
    }

    protected void initComponents() {
        grid = new TreeGrid<NodePickerTreeModel, JcrTreeNode>("grid", new Model((Serializable) newTreeModel()), newGridColumns()) {
            @Override
            protected void onItemSelectionChanged(IModel<JcrTreeNode> rowModel, boolean newValue) {
                BrixNode node = getNode(rowModel);
                if (isNodeEnabled(rowModel.getObject()) && node != null) {
                    if (isItemSelected(rowModel)) {
                        onNodeSelected(node);
                    } else {
                        onNodeDeselected(node);
                    }
                    update();
                }
                super.onItemSelectionChanged(rowModel, newValue);
            }

            @Override
            protected void onRowClicked(AjaxRequestTarget target, IModel<JcrTreeNode> rowModel) {
                BrixNode node = getNode(rowModel);
                if (isNodeEnabled(rowModel.getObject()) && node != null) {
                    super.onRowClicked(target, rowModel);
                }
            }

            @Override
            protected void onRowPopulated(WebMarkupContainer rowComponent) {
                super.onRowPopulated(rowComponent);
                rowComponent.add(new AbstractBehavior() {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) {
                        BrixNode node = getNode(component.getDefaultModel());
                        if (!isNodeEnabled((JcrTreeNode) component.getDefaultModelObject()) || node == null) {
                            tag.put("class", "disabled");
                        }
                    }
                });
            }
        };

        configureGrid(grid);
        add(grid);
    }

    protected AbstractTreeModel newTreeModel() {
        return new AbstractTreeModel() {
            public JcrTreeNode getRoot() {
                return new FilteredJcrTreeNode(getRootNode(), visibilityFilter);
            }
        };
    }

    protected abstract JcrTreeNode getRootNode();

    protected List<IGridColumn<NodePickerTreeModel,JcrTreeNode>> newGridColumns() {
        List<IGridColumn<NodePickerTreeModel,JcrTreeNode>> columns = new ArrayList<IGridColumn<NodePickerTreeModel, JcrTreeNode>>();
        columns.add(new NodePickerCheckBoxColumn("checkbox"));
        columns.add(new TreeColumn("name", new ResourceModel("name")).setInitialSize(300));
        columns.add(new NodePropertyColumn(new ResourceModel("type"), "userVisibleType"));
        columns.add(new DatePropertyColumn(new ResourceModel("lastModified"), "lastModified"));
        columns.add(new NodePropertyColumn(new ResourceModel("lastModifiedBy"), "lastModifiedBy"));
        return columns;
    }

    protected void onNodeSelected(BrixNode node) {
    }

    protected void onNodeDeselected(BrixNode node) {
    }

    private BrixNode getNode(IModel<?> model) {
        Object object = model.getObject();
        if (object instanceof JcrTreeNode) {
            IModel<BrixNode> nodeModel = ((JcrTreeNode) object).getNodeModel();
            return nodeModel != null ? nodeModel.getObject() : null;
        } else {
            return null;
        }
    }

    private boolean isNodeEnabled(JcrTreeNode node) {
        BrixNode n = node.getNodeModel() != null ? node.getNodeModel().getObject() : null;
        return enabledFilter.isNodeAllowed(n);
    }

    protected void configureGrid(TreeGrid<NodePickerTreeModel, JcrTreeNode> grid) {
        grid.getTree().setRootLess(true);
        grid.setClickRowToSelect(true);
        grid.setContentHeight(18, SizeUnit.EM);
    }

    protected void expandToSelectedNodes() {
        for (IModel<?> model : getGrid().getSelectedItems()) {
            JcrTreeNode node = (JcrTreeNode) model.getObject();
            expandToNode(node);
        }
    }

    private void expandToNode(JcrTreeNode node) {
        boolean first = true;
        while (node != null && node.getNodeModel() != null && node.getNodeModel().getObject() != null) {
            BrixNode n = node.getNodeModel().getObject();
            if (!first) {
                getGrid().getTreeState().expandNode(node);
            } else {
                first = false;
            }

            if (n.getDepth() > 0) {
                node = TreeAwareNode.Util.getTreeNode((BrixNode) n.getParent(), visibilityFilter);
            } else {
                node = null;
            }
        }
    }

    private class TreeColumn extends AbstractTreeColumn {
        public TreeColumn(String columnId, IModel headerModel) {
            super(columnId, headerModel);
        }

        @Override
        protected Component newNodeComponent(String id, final IModel model) {
            IModel<String> labelModel = new AbstractModel<String>() {
                @Override
                public String getObject() {
                    BrixNode node = getNode(model);
                    if (node != null) {
                        return node.getUserVisibleName();
                    } else {
                        return model.getObject().toString();
                    }
                }
            };
            return new Label(id, labelModel);
        }

        @Override
        public int getColSpan(IModel rowModel) {
            BrixNode node = getNode(rowModel);
            return node != null ? 1 : 4;
        }
    }

    private class NodePropertyColumn extends PropertyColumn {
        public NodePropertyColumn(IModel headerModel, String propertyExpression) {
            super(headerModel, propertyExpression);
        }

        @Override
        protected Object getModelObject(IModel rowModel) {
            return getNode(rowModel);
        }
    }

    protected class NodePickerCheckBoxColumn extends CheckBoxColumn {
        public NodePickerCheckBoxColumn(String columnId) {
            super(columnId);
        }

        @Override
        protected boolean isCheckBoxEnabled(IModel model) {
            BrixNode node = getNode(model);
            return isNodeEnabled((JcrTreeNode) model.getObject()) && node != null;
        }
    }

    protected class DatePropertyColumn extends NodePropertyColumn {
        public DatePropertyColumn(IModel<?> headerModel, String propertyExpression) {
            super(headerModel, propertyExpression);
        }

        @Override
        protected CharSequence convertToString(Object object) {
            if (object instanceof Date) {
                Date date = (Date) object;
                return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
            } else {
                return null;
            }
        }
    }
}
