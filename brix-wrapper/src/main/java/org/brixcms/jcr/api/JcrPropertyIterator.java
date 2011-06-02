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

import javax.jcr.PropertyIterator;

/**
 * @author Matej Knopp
 */
public interface JcrPropertyIterator extends PropertyIterator {

    public JcrProperty nextProperty();

// -------------------------- OTHER METHODS --------------------------
    public PropertyIterator getDelegate();

    public static class Wrapper {
        public static JcrPropertyIterator wrap(PropertyIterator delegate, JcrSession session) {
            return WrapperAccessor.JcrPropertyIteratorWrapper.wrap(delegate, session);
        }
    }
}