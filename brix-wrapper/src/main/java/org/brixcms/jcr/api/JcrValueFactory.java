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

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrValueFactory extends ValueFactory {

    public JcrValue createValue(String value);

    public JcrValue createValue(String value, int type);

    public JcrValue createValue(long value);

    public JcrValue createValue(double value);

    /**
     * Returns a <code>Value</code> object of {@link PropertyType#DECIMAL} with the specified <code>value</code>.
     *
     * @param value a <code>double</code>
     * @return a <code>Value</code> of {@link PropertyType#DECIMAL}
     * @since JCR 2.0
     */
    public Value createValue(BigDecimal value);

    public JcrValue createValue(boolean value);

    public JcrValue createValue(Calendar value);

    /**
     * @deprecated As of JCR 2.0, {@link #createValue(Binary)} should be used instead.
     */
    @Deprecated
    public JcrValue createValue(InputStream value);

    /**
     * Returns a <code>Value</code> object of <code>PropertyType.BINARY</code> with a value consisting of the content of
     * the specified <code>Binary</code>.
     *
     * @param value a <code>Binary</code>
     * @return a <code>Value</code> of {@link PropertyType#BINARY}
     * @since JCR 2.0
     */
    public Value createValue(Binary binary);

    public JcrValue createValue(Node value);

    /**
     * Returns a <code>Binary</code> object with a value consisting of the content of the specified
     * <code>InputStream</code>.
     * <p/>
     * The passed <code>InputStream</code> is closed before this method returns either normally or because of an
     * exception.
     *
     * @param stream an <code>InputStream</code>
     * @return a <code>Binary</code>
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public Binary createBinary(InputStream stream);

// -------------------------- OTHER METHODS --------------------------
    public ValueFactory getDelegate();

    public static class Wrapper {
        public static JcrValueFactory wrap(ValueFactory delegate, JcrSession session) {
            return WrapperAccessor.JcrValueFactoryWrapper.wrap(delegate, session);
        }
    }
}