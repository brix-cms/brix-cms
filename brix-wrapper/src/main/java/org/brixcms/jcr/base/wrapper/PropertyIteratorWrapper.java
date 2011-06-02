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

package org.brixcms.jcr.base.wrapper;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;

class PropertyIteratorWrapper extends BaseWrapper<PropertyIterator> implements PropertyIterator {
    public static PropertyIteratorWrapper wrap(PropertyIterator delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new PropertyIteratorWrapper(delegate, session);
        }
    }

    private PropertyIteratorWrapper(PropertyIterator delegate, SessionWrapper session) {
        super(delegate, session);
    }



    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    public Object next() {
        return PropertyWrapper.wrap((Property) getDelegate().next(), getSessionWrapper());
    }

    public void remove() {
        getDelegate().remove();
    }

    public Property nextProperty() {
        return PropertyWrapper.wrap(getDelegate().nextProperty(), getSessionWrapper());
    }


    public void skip(long skipNum) {
        getDelegate().skip(skipNum);
    }

    public long getSize() {
        return getDelegate().getSize();
    }

    public long getPosition() {
        return getDelegate().getPosition();
    }
}
