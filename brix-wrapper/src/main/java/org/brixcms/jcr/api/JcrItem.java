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

package org.brixcms.jcr.api;

import org.brixcms.jcr.api.wrapper.WrapperAccessor;

import javax.jcr.Item;
import javax.jcr.ItemVisitor;

/**
 * @author Matej Knopp
 */
public interface JcrItem extends Item {


    public String getPath();

    public String getName();

    public JcrItem getAncestor(int depth);

    public JcrNode getParent();

    public int getDepth();

    public JcrSession getSession();

    public boolean isNode();

    public boolean isNew();

    public boolean isModified();

    public boolean isSame(Item otherItem);

    public void accept(ItemVisitor visitor);

    /**
     * @deprecated, see {@link Item#save()}
     */
    @Deprecated
    public void save();

    public void refresh(boolean keepChanges);

    public void remove();

// -------------------------- OTHER METHODS --------------------------
    public Item getDelegate();

    public static class Wrapper {
        public static JcrItem wrap(Item delegate, JcrSession session) {
            return WrapperAccessor.JcrItemWrapper.wrap(delegate, session);
        }
    }
}