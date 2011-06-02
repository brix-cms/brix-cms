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

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrProperty;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrValue;

import javax.jcr.Binary;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 */
class PropertyWrapper extends ItemWrapper implements JcrProperty {
    public static JcrProperty wrap(Property delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new PropertyWrapper(delegate, session);
        }
    }

    protected PropertyWrapper(Property delegate, JcrSession session) {
        super(delegate, session);
    }



    public void accept(final ItemVisitor visitor) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                visitor.visit(PropertyWrapper.this);
            }
        });
    }

    @Override
    public Property getDelegate() {
        return (Property) super.getDelegate();
    }


    public void setValue(final Value value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Value[] values) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(values);
            }
        });
    }

    public void setValue(final String value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final String[] values) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(values);
            }
        });
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setValue(final InputStream value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Binary value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final long value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final double value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final BigDecimal value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Calendar value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final boolean value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Node value) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setValue(unwrap(value));
            }
        });
    }

    public JcrValue getValue() {
        return executeCallback(new Callback<JcrValue>() {
            public JcrValue execute() throws Exception {
                return JcrValue.Wrapper.wrap(getDelegate().getValue(), getJcrSession());
            }
        });
    }

    public JcrValue[] getValues() {
        return executeCallback(new Callback<JcrValue[]>() {
            public JcrValue[] execute() throws Exception {
                return JcrValue.Wrapper.wrap(getDelegate().getValues(), getJcrSession());
            }
        });
    }

    public String getString() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getString();
            }
        });
    }

    /**
     * @deprecated, see Property#getStream()
     */
    @Deprecated
    public InputStream getStream() {
        return executeCallback(new Callback<InputStream>() {
            public InputStream execute() throws Exception {
                return getDelegate().getStream();
            }
        });
    }

    public Binary getBinary() {
        return executeCallback(new Callback<Binary>() {
            public Binary execute() throws Exception {
                return getDelegate().getBinary();
            }
        });
    }

    public long getLong() {
        return executeCallback(new Callback<Long>() {
            public Long execute() throws Exception {
                return getDelegate().getLong();
            }
        });
    }

    public double getDouble() {
        return executeCallback(new Callback<Double>() {
            public Double execute() throws Exception {
                return getDelegate().getDouble();
            }
        });
    }

    public BigDecimal getDecimal() {
        return executeCallback(new Callback<BigDecimal>() {
            public BigDecimal execute() throws Exception {
                return getDelegate().getDecimal();
            }
        });
    }

    public Calendar getDate() {
        return executeCallback(new Callback<Calendar>() {
            public Calendar execute() throws Exception {
                return getDelegate().getDate();
            }
        });
    }

    public boolean getBoolean() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().getBoolean();
            }
        });
    }

    public JcrNode getNode() {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getNode(), getJcrSession());
            }
        });
    }

    public JcrProperty getProperty() {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().getProperty(), getJcrSession());
            }
        });
    }

    public long getLength() {
        return executeCallback(new Callback<Long>() {
            public Long execute() throws Exception {
                return getDelegate().getLength();
            }
        });
    }

    public long[] getLengths() {
        return executeCallback(new Callback<long[]>() {
            public long[] execute() throws Exception {
                return getDelegate().getLengths();
            }
        });
    }

    public PropertyDefinition getDefinition() {
        return executeCallback(new Callback<PropertyDefinition>() {
            public PropertyDefinition execute() throws Exception {
                return getDelegate().getDefinition();
            }
        });
    }

    public int getType() {
        return executeCallback(new Callback<Integer>() {
            public Integer execute() throws Exception {
                return getDelegate().getType();
            }
        });
    }

    public boolean isMultiple() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isMultiple();
            }
        });
    }
}
