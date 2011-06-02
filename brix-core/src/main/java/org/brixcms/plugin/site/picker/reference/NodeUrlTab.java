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

package org.brixcms.plugin.site.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.plugin.site.picker.node.SiteNodePicker;
import org.brixcms.plugin.site.tree.SiteNodeFilter;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.picker.node.NodePicker;
import org.brixcms.web.reference.Reference;
import org.brixcms.web.reference.Reference.Type;
import org.brixcms.web.tree.JcrTreeNode;

import java.util.Arrays;
import java.util.List;

public abstract class NodeUrlTab extends BrixGenericPanel<Reference> {
    public NodeUrlTab(String id, IModel<Reference> model) {
        super(id, model);

        setOutputMarkupId(true);

        List<Reference.Type> choices = Arrays.asList(Reference.Type.values());
        DropDownChoice<Reference.Type> choice;

        IChoiceRenderer<Reference.Type> renderer = new IChoiceRenderer<Reference.Type>() {
            public Object getDisplayValue(Type object) {
                return getString(object.toString());
            }

            public String getIdValue(Type object, int index) {
                return object.toString();
            }
        };

        add(choice = new DropDownChoice<Reference.Type>("type", new PropertyModel<Reference.Type>(this.getModel(),
                "type"), choices, renderer) {
            @Override
            public boolean isVisible() {
                return getConfiguration().isAllowNodePicker() && getConfiguration().isAllowURLEdit();
            }
        });

        choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(NodeUrlTab.this);
            }
        });

        NodePicker picker = null;
        if (getConfiguration().getRootNode() != null) {
            JcrTreeNode rootNode = TreeAwareNode.Util.getTreeNode(getConfiguration().getRootNode().getObject());
            picker = new NodePicker("nodePicker", getReference().getNodeModel(), rootNode,
                    new SiteNodeFilter(false, null), getConfiguration().getNodeFilter());
        } else {
            picker = new SiteNodePicker("nodePicker", getReference().getNodeModel(),
                    getConfiguration().getWorkspaceName(), getConfiguration().getNodeFilter()) {
                @Override
                public boolean isVisible() {
                    return getConfiguration().isAllowNodePicker()
                            && getReference().getType() == Reference.Type.NODE;
                }
            };
        }
        add(picker);

        add(new UrlPanel("urlPanel", new PropertyModel<String>(getModel(), "url")) {
            @Override
            public boolean isVisible() {
                return getConfiguration().isAllowURLEdit() && getReference().getType() == Reference.Type.URL;
            }
        });
    }

    protected abstract ReferenceEditorConfiguration getConfiguration();

    private Reference getReference() {
        return (Reference) getModelObject();
    }
}
