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

package org.brixcms.web.tree;

import org.apache.wicket.markup.html.tree.AbstractTree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AbstractTreeModel that delegates the tree structure to {@link TreeNode}. It also contains convenience methods that
 * inform the tree component that the tree structure has been changed.
 *
 * @author Matej Knopp
 */
public abstract class AbstractTreeModel implements Serializable, TreeModel {
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>(0);


    /**
     * Returns the root node of this tree. This is the only mandatory method to be implemented for custom tree models.
     */
    public abstract javax.swing.tree.TreeNode getRoot();

    public Object getChild(Object parent, int index) {
        List<?> children = ((TreeNode) parent).getChildren();
        return children != null ? children.get(index) : null;
    }

    public int getChildCount(Object parent) {
        List<?> children = ((TreeNode) parent).getChildren();
        return children != null ? children.size() : 0;
    }

    public boolean isLeaf(Object node) {
        return ((TreeNode) node).isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    public int getIndexOfChild(Object parent, Object child) {
        List<?> children = ((TreeNode) parent).getChildren();
        return children != null ? children.indexOf(child) : -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    /**
     * Notifies the tree that the given node has been changed while it's children remained unchanged.
     *
     * @param tree
     * @param node
     */
    public void nodeChanged(AbstractTree tree, TreeNode node) {
        TreeNode parent = (TreeNode) getParent(tree, node);
        if (parent != null) {
            int index = parent.getChildren().indexOf(node);
            if (index != -1) {
                TreeModelEvent e = new TreeModelEvent(this, pathFromNode(tree, parent), new int[]{index},
                        new Object[]{node});
                for (TreeModelListener l : listeners) {
                    l.treeNodesChanged(e);
                }
            }
        }
    }

    private TreePath pathFromNode(AbstractTree tree, TreeNode node) {
        List<TreeNode> l = new ArrayList<TreeNode>();
        for (TreeNode n = node; n != null; n = (TreeNode) getParent(tree, n)) {
            l.add(0, n);
        }
        return new TreePath(l.toArray(new TreeNode[l.size()]));
    }

    public Object getParent(AbstractTree tree, Object node) {
        return tree.getParentNode(node);
    }

    /**
     * Notifies the tree that the children of given node have been changed.
     *
     * @param tree
     * @param node
     */
    public void nodeChildrenChanged(AbstractTree tree, TreeNode node) {
        TreeModelEvent event = new TreeModelEvent(this, pathFromNode(tree, node));
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(event);
        }
    }

    /**
     * Notifies the tree that the given node will be removed. This method must be called <b>before</b> the node is actually
     * deleted.
     *
     * @param tree
     * @param node
     */
    public void nodeDeleted(AbstractTree tree, TreeNode node) {
        TreeNode parent = (TreeNode) getParent(tree, node);
        if (parent != null) {
            int index = parent.getChildren().indexOf(node);
            if (index != -1) {
                TreeModelEvent e = new TreeModelEvent(this, pathFromNode(tree, parent), new int[]{index},
                        new Object[]{node});
                for (TreeModelListener l : listeners) {
                    l.treeNodesRemoved(e);
                }
            }
        }
    }

    /**
     * Notifies the tree that the given node has been added.
     *
     * @param tree
     * @param node
     */
    public void nodeInserted(AbstractTree tree, TreeNode parent, TreeNode node) {
        int index = parent.getChildren().indexOf(node);
        if (index != -1) {
            TreeModelEvent e = new TreeModelEvent(this, pathFromNode(tree, parent), new int[]{index},
                    new Object[]{node});
            for (TreeModelListener l : listeners) {
                l.treeNodesInserted(e);
            }
        }
    }
}
