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

package org.brixcms.plugin.site;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.brixcms.registry.ExtensionPoint;

/**
 * Factory for rendering nodes in the site plugin tree view.
 * <p/>
 * Allows plugins to register new renderers for different types of nodes within the site tree
 *
 * @author Jeremy Thomerson
 */
public interface NodeTreeRenderer {
    public static final ExtensionPoint<NodeTreeRenderer> POINT = new ExtensionPoint<NodeTreeRenderer>() {
        public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return NodeTreeRenderer.class.getName();
        }
    };

    /**
     * Returns a component that renders this node in the site node browsing tree
     *
     * @param id            the id to use for your component
     * @param tree          the tree being rendered
     * @param treeNodeModel the model that contains the tree node you are rendering (JcrTreeNode)
     * @return a component if you want to render this node, or null if not
     */
    public Component newNodeComponent(String id, LinkTree tree, IModel<Object> treeNodeModel);
}
