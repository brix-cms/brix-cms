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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrProperty;
import org.brixcms.jcr.api.JcrPropertyIterator;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RangeIterator;

/**
 * @author Matej Knopp
 */
class PropertyIteratorWrapper extends RangeIteratorWrapper implements JcrPropertyIterator {
    public static JcrPropertyIterator wrap(PropertyIterator delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new PropertyIteratorWrapper(delegate, session);
        }
    }

    protected PropertyIteratorWrapper(RangeIterator delegate, JcrSession session) {
        super(delegate, session);
    }



    @Override
    public Object next() {
        return JcrProperty.Wrapper.wrap((Property) getDelegate().next(), getJcrSession());
    }

    @Override
    public PropertyIterator getDelegate() {
        return (PropertyIterator) super.getDelegate();
    }


    public JcrProperty nextProperty() {
        return JcrProperty.Wrapper.wrap(getDelegate().nextProperty(), getJcrSession());
    }
}
