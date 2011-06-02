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

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.tree.FilteredJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;

/**
 * Interface from nodes that can provide {@link JcrTreeNode} for other nodes. This is to create Tree structure for
 * NodePicker.
 *
 * @author Matej Knopp
 */
public interface TreeAwareNode extends JcrNode {
    public JcrTreeNode getTreeNode(BrixNode node);

    public static class Util {
        public static JcrTreeNode getTreeNode(BrixNode node, NodeFilter filter) {
            BrixNode n = node;
            while (n.getDepth() > 0) {
                if (n instanceof TreeAwareNode) {
                    JcrTreeNode result = ((TreeAwareNode) n).getTreeNode(node);
                    if (result != null) {
                        if (filter != null) {
                            result = new FilteredJcrTreeNode(result, filter);
                        }
                        return result;
                    }
                }
                n = (BrixNode) n.getParent();
            }
            return null;
        }

        public static JcrTreeNode getTreeNode(BrixNode node) {
            return getTreeNode(node, null);
        }
    }

    ;
}
