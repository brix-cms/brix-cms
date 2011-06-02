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

package org.brixcms.plugin.site.picker.node;

import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.tree.SiteNodeFilter;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.picker.node.NodePicker;
import org.brixcms.web.tree.NodeFilter;

public class SiteNodePicker extends NodePicker {
    public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceName, NodeFilter enabledFilter) {
        this(id, model, workspaceName, false, enabledFilter);
    }

    public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceId, boolean foldersOnly,
                          NodeFilter enabledFilter) {
        super(id, model, TreeAwareNode.Util.getTreeNode(SitePlugin.get().getSiteRootNode(workspaceId)),
                new SiteNodeFilter(foldersOnly, null), enabledFilter);
    }
}
