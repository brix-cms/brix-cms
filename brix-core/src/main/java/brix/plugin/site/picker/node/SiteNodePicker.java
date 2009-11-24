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

package brix.plugin.site.picker.node;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.tree.SiteNodeFilter;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.NodePicker;
import brix.web.tree.NodeFilter;

public class SiteNodePicker extends NodePicker
{

	public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceId, boolean foldersOnly,
			NodeFilter enabledFilter)
	{
		super(id, model, TreeAwareNode.Util.getTreeNode(SitePlugin.get().getSiteRootNode(workspaceId)),
				new SiteNodeFilter(foldersOnly, null), enabledFilter);
	}

	public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceName, NodeFilter enabledFilter)
	{
		this(id, model, workspaceName, false, enabledFilter);
	}

}
