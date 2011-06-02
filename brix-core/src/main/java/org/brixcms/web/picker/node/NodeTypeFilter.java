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

import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.tree.NodeFilter;

public class NodeTypeFilter implements NodeFilter {
    private final String nodeTypes[];

    public NodeTypeFilter(String nodeType) {
        if (nodeType == null) {
            throw new IllegalArgumentException("Argument 'nodeType' may not be null.");
        }
        this.nodeTypes = new String[]{nodeType};
    }

    public NodeTypeFilter(String... nodeTypes) {
        if (nodeTypes == null) {
            throw new IllegalArgumentException("Argument 'nodeTypes' may not be null.");
        }
        for (String s : nodeTypes) {
            if (s == null) {
                throw new IllegalArgumentException("Argument 'nodeTypes' may not contain null value.");
            }
        }
        this.nodeTypes = nodeTypes;
    }


    public boolean isNodeAllowed(BrixNode node) {
        for (String type : nodeTypes) {
            if (node != null && type.equals(node.getNodeType()))
                return true;
        }
        return false;
    }
}
