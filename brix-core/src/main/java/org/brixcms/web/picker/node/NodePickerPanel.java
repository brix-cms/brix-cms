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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.tree.NodeFilter;

public class NodePickerPanel extends FormComponentPanel<BrixNode> {
    protected static final String MODAL_WINDOW_ID = "modalWindow";
    private final JcrTreeNode rootNode;
    private final NodeFilter enabledFilter;
    private final NodeFilter visibilityFilter;

    public NodePickerPanel(String id, JcrTreeNode rootNode, NodeFilter visibilityFilter,
                           NodeFilter enabledFilter) {
        super(id);

        this.rootNode = rootNode;
        this.enabledFilter = enabledFilter;
        this.visibilityFilter = visibilityFilter;
    }

    public NodePickerPanel(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter) {
        super(id, model);

        this.rootNode = rootNode;
        this.enabledFilter = enabledFilter;
        this.visibilityFilter = visibilityFilter;
    }

    public NodeFilter getEnabledFilter() {
        return enabledFilter;
    }

    public JcrTreeNode getRootNode() {
        return rootNode;
    }


    @Override
    public void updateModel() {
        // don't you dare!
    }

    @Override
    public boolean checkRequired() {
        if (isRequired()) {
            JcrNode node = (JcrNode) getModelObject();
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    protected NodePickerModalWindow getModalWindow() {
        return (NodePickerModalWindow) get(MODAL_WINDOW_ID);
    }

    @Override
    public boolean isInputNullable() {
        return false;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (!hasBeenRendered()) {
            init();
        }
    }

    private void init() {
        add(newModalWindow(MODAL_WINDOW_ID));
        final Label label = new Label("label", newLabelModel()) {
            @Override
            public boolean isVisible() {
                return NodePickerPanel.this.getModelObject() != null;
            }
        };
        setOutputMarkupId(true);
        add(label);

        add(new AjaxLink<Void>("edit") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModalWindow().setModel(NodePickerPanel.this.getModel());
                getModalWindow().setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
                    public void onClose(AjaxRequestTarget target) {
                        target.addComponent(NodePickerPanel.this);
                        NodePickerPanel.this.onUpdate(target);
                    }
                });
                getModalWindow().show(target);
            }
        });

        add(new AjaxLink<Void>("clear") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                NodePickerPanel.this.setModelObject(null);
                target.addComponent(NodePickerPanel.this);
                NodePickerPanel.this.onUpdate(target);
            }

            @Override
            public boolean isEnabled() {
                return NodePickerPanel.this.getModelObject() != null;
            }
        });
    }

    protected Component newModalWindow(String id) {
        return new NodePickerModalWindow(id, getModel(), rootNode, visibilityFilter, enabledFilter);
    }

    protected IModel<String> newLabelModel() {
        return new Model<String>() {
            @Override
            public String getObject() {
                IModel<BrixNode> model = NodePickerPanel.this.getModel();
                BrixNode node = (BrixNode) model.getObject();
                // TODO: Don't use pathForNode here as it creates dependency on site plugin
                // rather than that format the path as /Site/[path], etc.
                return node != null ? SitePlugin.get().pathForNode(node) : "";
            }
        };
    }

    protected void onUpdate(AjaxRequestTarget target) {

    }

    @Override
    protected void onDetach() {
        if (rootNode != null)
            rootNode.detach();
        super.onDetach();
    }
}
