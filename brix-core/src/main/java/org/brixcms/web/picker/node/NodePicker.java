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

package org.brixcms.web.picker.node;

import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.picker.common.NodePickerTreeGridPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;

public class NodePicker extends BrixGenericPanel<BrixNode> {
    private final JcrTreeNode rootNode;
    private final NodePickerTreeGridPanel grid;

    public NodePicker(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter) {
        super(id, model);

        this.rootNode = rootNode;

        add(grid = new NodePickerTreeGridPanel("grid", visibilityFilter, enabledFilter) {
            @Override
            protected JcrTreeNode getRootNode() {
                return NodePicker.this.rootNode;
            }

            @Override
            protected void configureGrid(TreeGrid grid) {
                super.configureGrid(grid);
                grid.setAllowSelectMultiple(false);
                updateSelection();
                NodePicker.this.configureGrid(grid);
            }

            @Override
            protected void onNodeSelected(BrixNode node) {
                NodePicker.this.setModelObject(node);
            }

            @Override
            protected void onNodeDeselected(BrixNode node) {
                NodePicker.this.setModelObject(null);
            }
        });
    }

    private void updateSelection() {
        BrixNode current = getModelObject();
        if (current == null) {
            grid.getGrid().resetSelectedItems();
        } else {
            JcrTreeNode node = TreeAwareNode.Util.getTreeNode(getModelObject(), grid.getVisibilityFilter());
            if (node == null) {
                grid.getGrid().resetSelectedItems();
            } else {
                grid.getGrid().selectItem(new Model<JcrTreeNode>(node), true);
            }
        }
    }

    protected void configureGrid(TreeGrid grid) {

    }

    @Override
    protected void onBeforeRender() {
        // First time updateSelection has been rendered from within
        // NodePickerTreePanel#configureGrid

        // In all subsequent renders it's called from here. It must be called
        // before super.onBeforeRender so that the expanded tree items get
        // chance to render
        if (hasBeenRendered()) {
            updateSelection();
        }

        super.onBeforeRender();
    }

    @Override
    protected void onDetach() {
        this.rootNode.detach();
        super.onDetach();
    }
}
