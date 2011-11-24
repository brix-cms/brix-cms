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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.web.nodepage.BrixPageParameters;

/**
 * Plugin that handles node of certain type. This is not a global plugin, the scope if this plugin is {@link
 * SitePlugin}. Main purpose of {@link SiteNodePlugin} is to respond when an URL for site node is requested.
 *
 * @author Matej Knopp
 */
public interface SiteNodePlugin {
    public static ExtensionPoint<SiteNodePlugin> POINT = new ExtensionPoint<SiteNodePlugin>() {
        public org.brixcms.registry.ExtensionPoint.Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return SiteNodePlugin.class.getName();
        }
    };

    /**
     * This method returns a converter that is capable of converting the given node to a node this plugin can handle, or
     * <code>null</code> if such converter does not exist.
     *
     * @param node
     * @return
     */
    NodeConverter getConverterForNode(BrixNode node);

    /**
     * Returns the user readable name of this plugin.
     *
     * @return
     */
    String getName();

    /**
     * Returns the node type of nodes that this plugin can handle.
     *
     * @return
     * @see BrixNode#setNodeType(String)
     */
    String getNodeType();

    /**
     * Returns model caption of Create link for this plugin.
     *
     * @param parentNode
     * @return
     */
    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode);

    /**
     * Returns an instance of panel that should create node of type this plugin can handle.
     *
     * @param id         panel component id
     * @param parentNode parent node of the new node
     * @param goBack     simple callback that should be invoked after node creation or on cancel
     * @return panel instance
     */
    Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack);

    /**
     * Returns the request target if this plugin is capable of creating a response for the node. Otherwise returns
     * <code>null</code>
     *
     * @param nodeModel
     * @param requestParameters
     * @return
     */
    IRequestHandler respond(IModel<BrixNode> nodeModel, BrixPageParameters brixPageParameters);
}
