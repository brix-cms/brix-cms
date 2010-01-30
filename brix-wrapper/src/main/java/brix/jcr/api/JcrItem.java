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

package brix.jcr.api;

import javax.jcr.Item;
import javax.jcr.ItemVisitor;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrItem extends Item
{

    public static class Wrapper
    {
        public static JcrItem wrap(Item delegate, JcrSession session)
        {
            return WrapperAccessor.JcrItemWrapper.wrap(delegate, session);
        }
    };

    public Item getDelegate();

    public void accept(ItemVisitor visitor);

    public JcrItem getAncestor(int depth);

    public int getDepth();

    public String getName();

    public JcrNode getParent();

    public String getPath();

    public JcrSession getSession();

    public boolean isModified();

    public boolean isNew();

    public boolean isNode();

    public boolean isSame(Item otherItem);

    public void refresh(boolean keepChanges);

    public void remove();

    /**
     * @deprecated, see {@link Item#save()}
     */
    @Deprecated
    public void save();
}