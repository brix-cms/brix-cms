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

package org.brixcms.web.tile.pagetile;

import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.PageSiteNodePlugin;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;
import org.brixcms.plugin.site.picker.node.SiteNodePickerPanel;
import org.brixcms.web.picker.node.NodePickerPanel;
import org.brixcms.web.picker.node.NodeTypeFilter;
import org.brixcms.web.tree.NodeFilter;

public class PageTileEditorPanel extends GenericTileEditorPanel<BrixNode> {
    private IModel<BrixNode> targetNodeModel = new BrixNodeModel();

    public PageTileEditorPanel(String id, IModel<BrixNode> tileContainerNode) {
        this(id, tileContainerNode, new NodeTypeFilter(PageSiteNodePlugin.TYPE));
    }

    public PageTileEditorPanel(String id, IModel<BrixNode> tileContainerNode,
                               NodeFilter filter) {
        super(id, tileContainerNode);
        NodePickerPanel picker = new SiteNodePickerPanel("nodePicker",
                targetNodeModel, tileContainerNode.getObject().getSession()
                .getWorkspace().getName(), filter);
        picker.setRequired(true);
        add(picker);
    }

    @Override
    protected void detachModel() {
        if (targetNodeModel != null) {
            targetNodeModel.detach();
        }
        super.detachModel();
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
}
