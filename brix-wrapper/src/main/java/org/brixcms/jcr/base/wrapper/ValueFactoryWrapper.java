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

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

class ValueFactoryWrapper extends BaseWrapper<ValueFactory> implements ValueFactory {
    public static ValueFactory wrap(ValueFactory delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new ValueFactoryWrapper(delegate, session);
        }
    }

    private ValueFactoryWrapper(ValueFactory delegate, SessionWrapper session) {
        super(delegate, session);
    }


    public Value createValue(String value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(String value, int type) throws ValueFormatException {
        return getDelegate().createValue(value);
    }

    public Value createValue(long value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(double value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(BigDecimal value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(boolean value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(Calendar value) {
        return getDelegate().createValue(value);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Value createValue(InputStream value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(Binary value) {
        return getDelegate().createValue(value);
    }

    public Value createValue(Node value) throws RepositoryException {
        return getDelegate().createValue(unwrap(value));
    }

    public Value createValue(Node value, boolean weak) throws RepositoryException {
        return getDelegate().createValue(value, weak);
    }

    public Binary createBinary(InputStream stream) throws RepositoryException {
        return getDelegate().createBinary(stream);
    }
}
