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

package org.brixcms.markup.tag;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * HTML tag that can have a wicket component attached to it.
 *
 * @author Matej Knopp
 */
public interface ComponentTag extends Tag {
    /**
     * Creates wicket component for this tag. If there is no component returns null.
     *
     * @param id            component id (will be generated and based on {@link #getUniqueTagId()} result)
     * @param pageNodeModel model to JcrNode that represents the target page
     * @return
     */
    public Component getComponent(String id, IModel<BrixNode> pageNodeModel);

    /**
     * The unique identifier is required to keep track of components on page. It is used to remove components that no
     * longer have their items present in markup stream and add components which items are new in the markup stream.
     * <p/>
     * The identifier must be unique for every tag instance and must not change during the life of that instance.
     *
     * @return
     */
    public String getUniqueTagId();
}
