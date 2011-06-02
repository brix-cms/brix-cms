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

package org.brixcms.plugin.site.tree;

import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.tree.NodeFilter;

public class SiteNodeFilter implements NodeFilter {
    private final boolean foldersOnly;
    private final NodeFilter customFilter;

    public SiteNodeFilter(boolean foldersOnly, NodeFilter customFilter) {
        this.foldersOnly = foldersOnly;
        this.customFilter = customFilter;
    }


    public boolean isNodeAllowed(BrixNode node) {
        if (node == null) {
            return false;
        } else if (foldersOnly && !node.isFolder()) {
            return false;
        } else if (customFilter != null && !customFilter.isNodeAllowed(node)) {
            return false;
        }
        return true;
    }
}
