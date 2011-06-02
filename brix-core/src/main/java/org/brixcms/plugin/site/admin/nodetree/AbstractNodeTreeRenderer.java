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

package org.brixcms.plugin.site.admin.nodetree;

import org.apache.wicket.Component;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.NodeTreeRenderer;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.util.AbstractModel;

import java.io.Serializable;

/**
 * A base to build other renderers from.
 *
 * @author Jeremy Thomerson
 */
public abstract class AbstractNodeTreeRenderer implements NodeTreeRenderer, Serializable {
    private static final long serialVersionUID = 1L;


    public Component newNodeComponent(String id, LinkTree tree, IModel<Object> treeNodeModel) {
        JcrTreeNode node = (JcrTreeNode) treeNodeModel.getObject();
        BrixNode bn = node.getNodeModel().getObject();

        if (isForThisNode(bn)) {
            return new NodeTreeRenderingComponent(id, treeNodeModel, tree);
        }

        return null;
    }

    /**
     * @param tree the tree being rendered
     * @param node the JcrTreeNode being rendered
     * @return the resource reference to use as an icon for this node
     */
    protected abstract ResourceReference getImageResourceReference(BaseTree tree, Object node);

    /**
     * @param bn the brix node being rendered
     * @return whether you want to render it or not
     */
    protected boolean isForThisNode(BrixNode bn) {
        return bn.getClass().isAssignableFrom(getNodeClass());
    }

    /**
     * @return the class of node that you want to render
     */
    protected abstract Class<? extends BrixNode> getNodeClass();

    private class NodeTreeRenderingComponent extends LinkIconPanel {
        private static final long serialVersionUID = 1L;

        public NodeTreeRenderingComponent(String id, IModel<Object> model, BaseTree tree) {
            super(id, model, tree);
            BrixNode bn = ((JcrTreeNode) model.getObject()).getNodeModel().getObject();
            add(new SimpleAttributeModifier("class", bn.getNodeType()));
        }

        @Override
        protected ResourceReference getImageResourceReference(BaseTree tree, Object node) {
            return AbstractNodeTreeRenderer.this.getImageResourceReference(tree, node);
        }

        @Override
        protected Component newContentComponent(String componentId, BaseTree tree, final IModel<?> model) {
            return new Label(componentId, new AbstractModel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getObject() {
                    JcrTreeNode node = (JcrTreeNode) model.getObject();
                    BrixNode n = node.getNodeModel().getObject();
                    return n.getUserVisibleName();
                }
            }).add(new SimpleAttributeModifier("style", "padding-left: 4px;"));
        }
    }
}
