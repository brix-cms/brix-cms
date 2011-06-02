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

import org.brixcms.jcr.api.JcrRow;
import org.brixcms.jcr.api.JcrRowIterator;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

/**
 * @author Matej Knopp
 */
class RowIteratorWrapper extends RangeIteratorWrapper implements JcrRowIterator {
    public static JcrRowIterator wrap(RowIterator delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new RowIteratorWrapper(delegate, session);
        }
    }

    protected RowIteratorWrapper(RowIterator delegate, JcrSession session) {
        super(delegate, session);
    }



    @Override
    public Object next() {
        return JcrRow.Wrapper.wrap((Row) getDelegate().next(), getJcrSession());
    }

// --------------------- Interface JcrRowIterator ---------------------
    @Override
    public RowIterator getDelegate() {
        return (RowIterator) super.getDelegate();
    }


    public JcrRow nextRow() {
        return JcrRow.Wrapper.wrap(getDelegate().nextRow(), getJcrSession());
    }
}
