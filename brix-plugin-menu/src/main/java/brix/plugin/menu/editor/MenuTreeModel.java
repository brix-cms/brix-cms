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

package brix.plugin.menu.editor;

import org.apache.wicket.model.IDetachable;

import brix.plugin.menu.Menu.Entry;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.TreeNode;

public class MenuTreeModel extends AbstractTreeModel implements IDetachable
{
    public MenuTreeModel(Entry root)
    {
        if (root == null)
        {
            throw new IllegalArgumentException("Argument 'root' may not be null.");
        }
        this.root = new MenuTreeNode(root);
    }

    private final MenuTreeNode root;

    public TreeNode getRoot()
    {
        return root;
    }


    public void detach()
    {
        root.detach();
    }

}
