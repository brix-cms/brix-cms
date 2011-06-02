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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;

public class NodePickerWithButtons extends BrixGenericPanel<BrixNode> {
    private IModel<BrixNode> nodeModel;

    public NodePickerWithButtons(String id, JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter) {
        super(id);
        init(rootNode, visibleFilter, enabledFilter);
    }

    private void init(JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter) {
        nodeModel = new BrixNodeModel(getModel().getObject());

        add(new NodePicker("picker", this.nodeModel, rootNode, visibleFilter, enabledFilter));

        add(new AjaxLink<Void>("ok") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onOk(target);
            }
        });

        add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        });
    }

    protected void onOk(AjaxRequestTarget target) {
        setModelObject(getNodeModel().getObject());
    }

    protected void onCancel(AjaxRequestTarget target) {

    }

    public NodePickerWithButtons(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter) {
        super(id, model);
        init(rootNode, visibleFilter, enabledFilter);
    }

    protected IModel<BrixNode> getNodeModel() {
        return nodeModel;
    }

    public boolean isDisplayFiles() {
        return true;
    }
}
