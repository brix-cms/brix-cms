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
import org.apache.wicket.model.Model;
import org.brixcms.jcr.JcrUtil;
import org.brixcms.jcr.api.JcrNode;

import java.util.List;
import java.util.Map;


public class CreatePrototypePanel extends SelectItemsPanel<Void> {
    public CreatePrototypePanel(String id, String workspaceId, final String targetPrototypeName) {
        super(id, workspaceId);

        final Component message = new MultiLineLabel("message", new Model<String>(
                ""));
        message.setOutputMarkupId(true);
        add(message);

        add(new AjaxLink<Void>("create") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<JcrNode> nodes = getSelectedNodes();
                if (!nodes.isEmpty()) {
                    Map<JcrNode, List<JcrNode>> dependencies = JcrUtil.getUnsatisfiedDependencies(
                            nodes, null);
                    if (!dependencies.isEmpty()) {
                        message.setDefaultModelObject(getDependenciesMessage(dependencies));
                    } else {
                        PrototypePlugin.get().createPrototype(nodes, targetPrototypeName);
                        findParent(ModalWindow.class).close(target);
                    }
                } else {
                    message.setDefaultModelObject(getString("youHaveToSelectAtLeastOneNode"));
                }
                target.addComponent(message);
            }
        });
    }
}
