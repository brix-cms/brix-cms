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

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.picker.node.SiteNodePicker;
import org.brixcms.plugin.site.tree.SiteNodeFilter;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.picker.node.NodePicker;
import org.brixcms.web.reference.Reference;
import org.brixcms.web.reference.Reference.Type;
import org.brixcms.web.tree.JcrTreeNode;

public abstract class NodeUrlTab extends BrixGenericPanel<Reference> {
    public NodeUrlTab(String id, IModel<Reference> model) {
        super(id, model);

        setOutputMarkupId(true);

        List<Reference.Type> choices = Arrays.asList(Reference.Type.values());
        DropDownChoice<Reference.Type> choice;

        IChoiceRenderer<Reference.Type> renderer = new ChoiceRenderer<Reference.Type>() {
            public Object getDisplayValue(Type object) {
                return getString(object.toString());
            }

            public String getIdValue(Type object, int index) {
                return object.toString();
            }
        };

        final ReferenceEditorConfiguration configuration = getConfiguration();
        add(choice = new DropDownChoice<Reference.Type>("type", new PropertyModel<Reference.Type>(this.getModel(), "type"), choices,
                renderer) {
            @Override
            public boolean isVisible() {
                return configuration.isAllowNodePicker() && configuration.isAllowURLEdit();
            }
        });

        choice.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(NodeUrlTab.this);
            }
        });

        NodePicker picker = null;
        IModel<BrixNode> rootNodeModel = configuration.getRootNode();
        if (rootNodeModel != null) {
            JcrTreeNode rootNode = TreeAwareNode.Util.getTreeNode(rootNodeModel.getObject());
            picker = new NodePicker("nodePicker", getReference().getNodeModel(), rootNode, new SiteNodeFilter(false, null),
                    configuration.getNodeFilter());
        } else {
            picker = new SiteNodePicker("nodePicker", getReference().getNodeModel(), configuration.getWorkspaceName(),
                    configuration.getNodeFilter()) {
                @Override
                public boolean isVisible() {
                    return configuration.isAllowNodePicker() && getReference().getType() == Reference.Type.NODE;
                }
            };
        }
        add(picker);

        add(new UrlPanel("urlPanel", new PropertyModel<String>(getModel(), "url")) {
            @Override
            public boolean isVisible() {
                return configuration.isAllowURLEdit() && getReference().getType() == Reference.Type.URL;
            }
        });
    }

    protected abstract ReferenceEditorConfiguration getConfiguration();

    private Reference getReference() {
        return (Reference) getModelObject();
    }
}
