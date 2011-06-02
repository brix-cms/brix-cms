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

package org.brixcms.workspace;

import java.util.Iterator;

/**
 * Represents a single workspace. Allows to set and query workspace attributes. To obtain a workspace object use {@link
 * WorkspaceManager}.
 *
 * @author Matej Knopp
 */
public interface Workspace {
    /**
     * Deletes the workspace.
     */
    public void delete();

    /**
     * Returns attribute with the specified key.
     *
     * @param attributeKey
     * @return attribute value or <code>null</code> if such attribute is not present.
     */
    public String getAttribute(String attributeKey);

    /**
     * Returns all available attribute keys in this workspace.
     *
     * @return
     */
    public Iterator<String> getAttributeKeys();

    /**
     * Returns workspace id. Workspace id is a JCR workspace name.
     *
     * @return
     */
    public String getId();

    /**
     * Sets the attribute with specified key to specified value. To remove attribute use <code>null</code> value.
     *
     * @param attributeKey
     * @param attributeValue
     */
    public void setAttribute(String attributeKey, String attributeValue);
}
