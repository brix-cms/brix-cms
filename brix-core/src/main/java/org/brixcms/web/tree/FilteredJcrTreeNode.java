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

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.brixcms.jcr.wrapper.BrixNode;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class FilteredJcrTreeNode implements JcrTreeNode {
    private final JcrTreeNode delegate;
    private final NodeFilter visibilityFilter;

    private List<JcrTreeNode> children = null;

    public FilteredJcrTreeNode(final JcrTreeNode delegate, final NodeFilter visibilityFilter) {
        this.delegate = delegate;
        this.visibilityFilter = visibilityFilter;

        if (delegate == null) {
            throw new IllegalArgumentException("Argument 'delegate' may not be null.");
        }
    }

    public List<? extends JcrTreeNode> getChildren() {
        if (children == null) {
            buildChildren();
        }
        return children;
    }

    private void buildChildren() {
        final List<? extends JcrTreeNode> original = delegate.getChildren();
        if (original == null) {
            children = Collections.emptyList();
        } else if (visibilityFilter != null) {
            children = new ArrayList<JcrTreeNode>(original.size());
            for (JcrTreeNode node : original) {
                BrixNode n = node.getNodeModel() != null ? node.getNodeModel().getObject() : null;
                if (visibilityFilter.isNodeAllowed(n)) {
                    children.add(new FilteredJcrTreeNode(node, visibilityFilter));
                }
            }
        } else {
            children = new ArrayList<JcrTreeNode>(original);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof JcrTreeNode == false) {
            return false;
        }

        JcrTreeNode that;
        if (obj instanceof FilteredJcrTreeNode) {
            that = ((FilteredJcrTreeNode) obj).delegate;
        } else {
            that = (JcrTreeNode) obj;
        }
        return Objects.equal(delegate, that);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }


    public IModel<BrixNode> getNodeModel() {
        return delegate.getNodeModel();
    }

    public void detach() {
        children = null;
        delegate.detach();
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
        return delegate.isLeaf();
    }

    @Override
    public Enumeration children() {
        return null;
    }
}
