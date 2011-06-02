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

package org.brixcms.plugin.prototype;

import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.picker.common.NodePickerTreeGridPanel;
import org.brixcms.web.picker.common.RootTreeNode;
import org.brixcms.web.picker.node.NodePickerTreeModel;
import org.brixcms.web.tree.JcrTreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SelectItemsPanel<T> extends BrixGenericPanel<T> {
    private NodePickerTreeGridPanel treeGrid;

    public SelectItemsPanel(String id, String workspaceName) {
        super(id);
        init(workspaceName);
    }

    public SelectItemsPanel(String id, IModel<T> model, String workspaceId) {
        super(id, model);
        init(workspaceId);
    }

    private void init(final String workspaceId) {
        treeGrid = new NodePickerTreeGridPanel("grid", null, null) {
            @Override
            protected JcrTreeNode getRootNode() {
                return new RootTreeNode(workspaceId);
            }

            @Override
            protected void configureGrid(TreeGrid grid) {
                super.configureGrid(grid);
                grid.setAllowSelectMultiple(true);
                grid.getTree().setRootLess(true);
            }
        };

        add(treeGrid);
    }

    protected String getDependenciesMessage(Map<JcrNode, List<JcrNode>> dependencies) {
        StringBuilder b = new StringBuilder();

        b.append(getString("followingDependenciesAreNotSatisfied") + "\n");

        for (Entry<JcrNode, List<JcrNode>> entry : dependencies.entrySet()) {
            b.append(getNodePath((BrixNode) entry.getKey()));
            b.append(" -> ");

            if (entry.getValue().size() == 1) {
                b.append(getNodePath((BrixNode) entry.getValue().iterator().next()));
            } else {
                b.append("(");
                boolean first = true;

                for (JcrNode node : entry.getValue()) {
                    if (first) {
                        first = false;
                    } else {
                        b.append(", ");
                    }
                    b.append(getNodePath((BrixNode) node));
                }

                b.append(")");
            }

            b.append("\n");
        }
        return b.toString();
    }

    private String getNodePath(BrixNode node) {
        List<BrixNode> path = new ArrayList<BrixNode>();
        while (node.getDepth() > 1) {
            path.add(0, node);
            node = (BrixNode) node.getParent();
        }
        StringBuilder res = new StringBuilder();
        for (BrixNode n : path) {
            res.append("/");
            res.append(n.getUserVisibleName());
            if (!n.isFolder()) {
                break;
            }
        }
        return res.toString();
    }

    @SuppressWarnings("unchecked")
    protected List<JcrNode> getSelectedNodes() {
        List<JcrNode> nodes = new ArrayList<JcrNode>();
        for (IModel<JcrTreeNode> model : getTreeGrid().getSelectedItems()) {
            JcrTreeNode treeNode = model.getObject();
            JcrNode node = treeNode.getNodeModel() != null ? treeNode.getNodeModel().getObject() : null;
            if (node != null)
                nodes.add(node);
        }

        return nodes;
    }

    public TreeGrid<NodePickerTreeModel, JcrTreeNode> getTreeGrid() {
        return treeGrid.getGrid();
    }
}
