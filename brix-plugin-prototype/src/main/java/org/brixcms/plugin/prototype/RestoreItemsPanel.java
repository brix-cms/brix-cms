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

package org.brixcms.plugin.prototype;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.JcrUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrWorkspace;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.picker.node.SiteNodePickerPanel;

import java.util.List;
import java.util.Map;

public class RestoreItemsPanel extends SelectItemsPanel<Void> {
    private IModel<BrixNode> targetNode = new BrixNodeModel();

    public RestoreItemsPanel(String id, String prototypeWorkspaceId, final String targetWorkspaceId) {
        super(id, prototypeWorkspaceId);

        final Component message = new MultiLineLabel("message", new Model<String>(
                ""));
        message.setOutputMarkupId(true);
        add(message);


        add(new SiteNodePickerPanel("picker", targetNode, targetWorkspaceId, true, null));

        add(new AjaxLink<Void>("restore") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<JcrNode> nodes = getSelectedNodes();
                if (!nodes.isEmpty()) {
                    Brix brix = ((BrixNode) nodes.iterator().next()).getBrix();
                    JcrWorkspace targetWorkspace = brix.getCurrentSession(targetWorkspaceId).getWorkspace();
                    Map<JcrNode, List<JcrNode>> dependencies = JcrUtil.getUnsatisfiedDependencies(
                            nodes, targetWorkspace);
                    ;
                    if (!dependencies.isEmpty()) {
                        message.setDefaultModelObject(getDependenciesMessage(dependencies));
                    } else {
                        JcrNode rootNode = targetNode.getObject();
                        if (rootNode == null) {
                            rootNode = targetWorkspace.getSession().getRootNode();
                        }
                        PrototypePlugin.get().restoreNodes(nodes, rootNode);
                        findParent(ModalWindow.class).close(target);
                    }
                } else {
                    message.setDefaultModelObject("You have to select at least one node.");
                }
                target.addComponent(message);
            }
        });
    }
}
