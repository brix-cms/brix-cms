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

import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.tree.JcrTreeNode;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public class RootTreeNode implements JcrTreeNode {
    private final String workspaceId;

    private List<JcrTreeNode> children = null;

    public RootTreeNode(String workpaceId) {
        this.workspaceId = workpaceId;
    }

    public List<? extends JcrTreeNode> getChildren() {
        if (children == null) {
            buildChildren();
        }
        return children;
    }

    private void buildChildren() {
        children = new ArrayList<JcrTreeNode>();
        JcrSession session = Brix.get().getCurrentSession(workspaceId);
        BrixNode root = (BrixNode) session.getItem(Brix.get().getRootPath());
        JcrNodeIterator iterator = root.getNodes();
        while (iterator.hasNext()) {
            BrixNode node = (BrixNode) iterator.nextNode();
            if (node instanceof TreeAwareNode) {
                JcrTreeNode treeNode = ((TreeAwareNode) node).getTreeNode(node);
                if (treeNode != null) {
                    children.add(treeNode);
                }
            }
        }
        Collections.sort(children, new Comparator<JcrTreeNode>() {
            public int compare(JcrTreeNode o1, JcrTreeNode o2) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
    }


    public IModel<BrixNode> getNodeModel() {
        return null;
    }

    public void detach() {
        children = null;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return null;
    }
}
