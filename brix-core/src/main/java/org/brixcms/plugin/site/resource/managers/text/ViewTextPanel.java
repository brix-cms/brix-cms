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

package org.brixcms.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.BrixNode.Protocol;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.resource.ResourceNodeHandler;
import org.brixcms.web.generic.BrixGenericPanel;

public class ViewTextPanel extends BrixGenericPanel<BrixNode> {
    public ViewTextPanel(String id, final IModel<BrixNode> model) {
        super(id, model);

        IModel<String> labelModel = new Model<String>() {
            @Override
            public String getObject() {
                BrixFileNode node = (BrixFileNode) getModel().getObject();
                return node.getDataAsString();
            }
        };
        add(new Label("label", labelModel));

        add(new Label("mimeType", new Model<String>() {
            @Override
            public String getObject() {
                BrixFileNode node = (BrixFileNode) model.getObject();
                return node.getMimeType();
            }
        }));

        add(new Label("size", new Model<String>() {
            @Override
            public String getObject() {
                BrixFileNode node = (BrixFileNode) model.getObject();
                return node.getContentLength() + " bytes";
            }
        }));

        add(new Label("requiredProtocol", new Model<String>() {
            @Override
            public String getObject() {
                Protocol protocol = model.getObject().getRequiredProtocol();
                return getString(protocol.toString());
            }
        }));

        add(new Link<Void>("download") {
            @Override
            public void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceNodeHandler(model, true));
            }
        });

        add(new Link<Void>("edit") {
            @Override
            public void onClick() {
                EditTextResourcePanel panel = new EditTextResourcePanel(ViewTextPanel.this.getId(),
                        ViewTextPanel.this.getModel()) {
                    @Override
                    protected void done() {
                        replaceWith(ViewTextPanel.this);
                    }
                };
                ViewTextPanel.this.replaceWith(panel);
            }

            @Override
            public boolean isVisible() {
                return hasEditPermission(ViewTextPanel.this.getModel());
            }
        });
    }

    private static boolean hasEditPermission(IModel<BrixNode> model) {
        return SitePlugin.get().canEditNode(model.getObject(), Context.ADMINISTRATION);
    }
}
