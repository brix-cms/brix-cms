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
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrValue extends Value {


    public String getString();

    /**
     * @deprecated {@link #getBinary()} should be used instead.
     */
    @Deprecated
    public InputStream getStream();

    /**
     * Returns a <code>Binary</code> representation of this value. The {@link Binary} object in turn provides methods to
     * access the binary data itself. Uses the standard conversion to binary (see JCR specification).
     *
     * @return A <code>Binary</code> representation of this value.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public Binary getBinary();

    public long getLong();

    public double getDouble();

    /**
     * Returns a <code>BigDecimal</code> representation of this value.
     *
     * @return A <code>BigDecimal</code> representation of this value.
     * @throws ValueFormatException if conversion to a <code>BigDecimal</code> is not possible.
     * @throws RepositoryException  if another error occurs.
     * @since JCR 2.0
     */
    public BigDecimal getDecimal() throws ValueFormatException, RepositoryException;

    public Calendar getDate();

    public boolean getBoolean();

    public int getType();

// -------------------------- OTHER METHODS --------------------------
    public Value getDelegate();

    public static class Wrapper {
        public static JcrValue wrap(Value delegate, JcrSession session) {
            return WrapperAccessor.JcrValueWrapper.wrap(delegate, session);
        }

        public static JcrValue[] wrap(Value[] delegate, JcrSession session) {
            return WrapperAccessor.JcrValueWrapper.wrap(delegate, session);
        }
    }
}