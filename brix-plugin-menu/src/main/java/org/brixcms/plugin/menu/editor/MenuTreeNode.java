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

import org.apache.wicket.util.lang.Objects;
import org.brixcms.plugin.menu.Menu.Entry;
import org.brixcms.web.tree.TreeNode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MenuTreeNode implements TreeNode {
    private final Entry entry;

    public MenuTreeNode(Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Argument 'entry' may not be null.");
        }
        this.entry = entry;
    }

    public List<? extends TreeNode> getChildren() {
        List<MenuTreeNode> children = new ArrayList<MenuTreeNode>();
        for (Entry e : entry.getChildren()) {
            children.add(new MenuTreeNode(e));
        }
        return children;
    }

    public Entry getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MenuTreeNode == false) {
            return false;
        }
        return Objects.equal(entry, ((MenuTreeNode) obj).entry);
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }

    @Override
    public String toString() {
        return entry.toString();
    }


    public void detach() {
    }


    @Override
    public javax.swing.tree.TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public javax.swing.tree.TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(javax.swing.tree.TreeNode node) {
        return 0;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    public boolean isLeaf() {
        return entry.getChildren().isEmpty();
    }

    @Override
    public Enumeration children() {
        return null;
    }
}
