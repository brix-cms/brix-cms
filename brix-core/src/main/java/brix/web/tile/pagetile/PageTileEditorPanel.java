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

package brix.web.tile.pagetile;

import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.tile.admin.GenericTileEditorPanel;
import brix.plugin.site.picker.node.SiteNodePickerPanel;
import brix.web.picker.node.NodePickerPanel;
import brix.web.picker.node.NodeTypeFilter;
import brix.web.tree.NodeFilter;

public class PageTileEditorPanel extends GenericTileEditorPanel<BrixNode> {

	private IModel<BrixNode> targetNodeModel = new BrixNodeModel();

	public PageTileEditorPanel(String id, IModel<BrixNode> tileContainerNode,
			NodeFilter filter) {
		super(id, tileContainerNode);
		NodePickerPanel picker = new SiteNodePickerPanel("nodePicker",
				targetNodeModel, tileContainerNode.getObject().getSession()
						.getWorkspace().getName(), filter);
		picker.setRequired(true);
		add(picker);
	}

	public PageTileEditorPanel(String id, IModel<BrixNode> tileContainerNode) {
		this(id, tileContainerNode, new NodeTypeFilter(PageSiteNodePlugin.TYPE));
	}

	@Override
	public void load(BrixNode node) {
		if (node.hasProperty("pageNode")) {
			BrixNode pageNode = (BrixNode) node.getProperty("pageNode")
					.getNode();
			targetNodeModel.setObject(pageNode);
		}
	}

	@Override
	public void save(BrixNode node) {
		node.setProperty("pageNode", targetNodeModel.getObject());
	}

	@Override
	protected void detachModel() {
		if (targetNodeModel != null) {
			targetNodeModel.detach();
		}
		super.detachModel();
	}

}
