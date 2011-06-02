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
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrProperty extends JcrItem, Property {

// --------------------- Interface JcrItem ---------------------
    public Property getDelegate();


    public void setValue(Value value);

    public void setValue(Value[] values);

    public void setValue(String value);

    public void setValue(String[] values);

    /**
     * @deprecated As of JCR 2.0, {@link #setValue(Binary)} should be used instead.
     */
    @Deprecated
    public void setValue(InputStream value);

    // SINCE 2.0


    /**
     * Sets the value of this property to <code>value</code>. Same as <code>{@link #setValue(Value value)}</code> except
     * that the value is specified as a <code>Binary</code>.
     *
     * @param value The new value to set the property to.
     * @throws ValueFormatException         if the type or format of the specified value is incompatible with the type
     *                                      of this property.
     * @throws VersionException             if this property belongs to a node that is read-only due to a checked-in
     *                                      node and this implementation performs this validation immediately.
     * @throws LockException                if a lock prevents the setting of the value and this implementation performs
     *                                      this validation immediately.
     * @throws ConstraintViolationException if the change would violate a node-type or other constraint and this
     *                                      implementation performs this validation immediately.
     * @throws RepositoryException          if another error occurs.
     * @since JCR 2.0
     */
    public void setValue(Binary value);

    public void setValue(long value);

    public void setValue(double value);

    /**
     * Sets the value of this property to <code>value</code>. Same as <code>{@link #setValue(Value value)}</code> except
     * that the value is specified as a <code>BigDecimal</code>.
     *
     * @param value The new value to set the property to.
     * @throws ValueFormatException         if the type or format of the specified value is incompatible with the type
     *                                      of this property.
     * @throws VersionException             if this property belongs to a node that is read-only due to a checked-in
     *                                      node and this implementation performs this validation immediately.
     * @throws LockException                if a lock prevents the setting of the value and this implementation performs
     *                                      this validation immediately.
     * @throws ConstraintViolationException if the change would violate a node-type or other constraint and this
     *                                      implementation performs this validation immediately.
     * @throws RepositoryException          if another error occurs.
     * @since JCR 2.0
     */
    public void setValue(BigDecimal value);

    public void setValue(Calendar value);

    public void setValue(boolean value);

    public void setValue(Node value);

    public JcrValue getValue();

    public JcrValue[] getValues();

    public String getString();

    /**
     * @deprecated As of JCR 2.0, {@link #getBinary()} should be used instead.
     */
    @Deprecated
    public InputStream getStream();


    /**
     * Returns a <code>Binary</code> representation of the value of this property. A shortcut for
     * <code>Property.getValue().getBinary()</code>.
     *
     * @return A <code>Binary</code> representation of the value of this property.
     * @throws ValueFormatException if the property is multi-valued.
     * @throws RepositoryException  if another error occurs.
     * @see Value
     * @see Binary
     * @since JCR 2.0
     */
    // TODO wrap Binary in JcrBinary
    public Binary getBinary();

    public long getLong();

    public double getDouble();


    /**
     * Returns a <code>BigDecimal</code> representation of the value of this property. A shortcut for
     * <code>Property.getValue().getDecimal()</code>.
     *
     * @return A <code>BigDecimal</code> representation of the value of this property.
     * @throws ValueFormatException if conversion to a <code>BigDecimal</code> is not possible or if the property is
     *                              multi-valued.
     * @throws RepositoryException  if another error occurs
     * @see Value
     * @since JCR 2.0
     */
    public BigDecimal getDecimal();

    public Calendar getDate();

    public boolean getBoolean();

    public JcrNode getNode();


    /**
     * If this property is of type <code>PATH</code> (or convertible to this type) this method returns the
     * <code>Property</code> to which <i>this</i> property refers.
     * <p/>
     * If this property contains a relative path, it is interpreted relative to the parent node of this property.
     * Therefore, when resolving such a relative path, the segment "<code>.</code>" refers to the parent node itself,
     * "<code>..</code>" to the parent of the parent node and " <code>foo</code>" to a sibling property of this property
     * or this property itself.
     * <p/>
     * For example, if this property is located at <code>/a/b/c</code> and it has a value of " <code>../d</code>" then
     * this method will return the property at <code>/a/d</code> if such exists.
     * <p/>
     * If this property is multi-valued, this method throws a <code>ValueFormatException</code>.
     * <p/>
     * If this property cannot be converted to a <code>PATH</code> then a <code>ValueFormatException</code> is thrown.
     * <p/>
     * If this property is currently part of the frozen state of a version in version storage, this method will throw a
     * <code>ValueFormatException</code>.
     *
     * @return the referenced property
     * @throws ValueFormatException  if this property cannot be converted to a <code>PATH</code>, if the property is
     *                               multi-valued or if this property is a referring type but is currently part of the
     *                               frozen state of a version in version storage.
     * @throws ItemNotFoundException If no property accessible by the current <code>Session</code> exists in this
     *                               workspace at the specified path. Note that this applies even if a <i>node</i>
     *                               exists at the specified location. To dereference to a target node, the method
     *                               <code>Property.getNode</code> is used.
     * @throws RepositoryException   if another error occurs.
     * @since JCR 2.0
     */
    public JcrProperty getProperty();

    public long getLength();

    public long[] getLengths();

    public PropertyDefinition getDefinition();

    public int getType();

    public static class Wrapper {
        public static JcrProperty wrap(Property delegate, JcrSession session) {
            return WrapperAccessor.JcrPropertyWrapper.wrap(delegate, session);
        }
    }
}