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

import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tree.AbstractJcrTreeNode;
import org.brixcms.web.tree.AbstractTreeModel;

public abstract class NodePickerTreeModel extends AbstractTreeModel {
    private NodePickerTreeNode root;

    public NodePickerTreeModel(String workspaceName) {
        JcrSession session = Brix.get().getCurrentSession(workspaceName);

        root = new NodePickerTreeNode((BrixNode) session.getItem(SitePlugin.get().getSiteRootPath()));
    }

    public javax.swing.tree.TreeNode getRoot() {
        return root;
    }

    protected abstract boolean displayFoldersOnly();

    public NodePickerTreeNode treeNodeFor(BrixNode node) {
        return new NodePickerTreeNode(node);
    }

    public class NodePickerTreeNode extends AbstractJcrTreeNode {
        public NodePickerTreeNode(BrixNode node) {
            super(node);
        }

        @Override
        protected AbstractJcrTreeNode newTreeNode(BrixNode node) {
            return new NodePickerTreeNode(node);
        }

        @Override
        protected boolean displayFoldersOnly() {
            return NodePickerTreeModel.this.displayFoldersOnly();
        }

        public BrixNode getNode() {
            return getNodeModel().getObject();
        }
    }
}
