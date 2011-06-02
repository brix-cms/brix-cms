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

package org.brixcms.plugin.site.admin;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * Allows nested or related components to affect changes to the node tree contained and managed by the component that
 * implements this interface.
 * <p/>
 * This interface must be implemented by a subclass of {@link WebMarkupContainer}
 *
 * @author igor.vaynberg
 */
public interface NodeTreeContainer {
    /**
     * Called when the tree selection needs to be changed to the specified node
     *
     * @param node
     */
    public abstract void selectNode(BrixNode node);

    /**
     * Called when the tree needs to be updated - eg a new node has been inserted
     */
    public abstract void updateTree();
}