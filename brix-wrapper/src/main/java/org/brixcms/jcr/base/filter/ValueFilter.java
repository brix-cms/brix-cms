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

package org.brixcms.jcr.base.filter;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public class ValueFilter {
    private final ValueFilter previous;

    public ValueFilter() {
        previous = null;
    }

    public ValueFilter(ValueFilter previous) {
        this.previous = previous;
    }

    public ValueFilter getPrevious() {
        return previous;
    }

    public Binary getBinary(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getBinary(property);
        } else {
            return property.getBinary();
        }
    }

    public boolean getBoolean(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getBoolean(property);
        } else {
            return property.getBoolean();
        }
    }

    public Calendar getDate(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getDate(property);
        } else {
            return property.getDate();
        }
    }

    public BigDecimal getDecimal(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getDecimal(property);
        } else {
            return property.getDecimal();
        }
    }

    public double getDouble(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getDouble(property);
        } else {
            return property.getDouble();
        }
    }

    public long getLength(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getLength(property);
        } else {
            return property.getLength();
        }
    }

    public long[] getLengths(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getLengths(property);
        } else {
            return property.getLengths();
        }
    }

    public long getLong(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getLong(property);
        } else {
            return property.getLong();
        }
    }

    public Node getNode(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getNode(property);
        } else {
            return property.getNode();
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public InputStream getStream(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getStream(property);
        } else {
            return property.getStream();
        }
    }

    public String getString(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getString(property);
        } else {
            return property.getString();
        }
    }

    public int getType(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getType(property);
        } else {
            return property.getType();
        }
    }

    public Value getValue(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getValue(property);
        } else {
            return property.getValue();
        }
    }

    public Value[] getValues(Property property) throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().getValues(property);
        } else {
            return property.getValues();
        }
    }

    public Property setValue(Node node, String name, Value value, Integer type)
            throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().setValue(node, name, value, type);
        } else {
            if (type != null) {
                return node.setProperty(name, value, type);
            } else {
                return node.setProperty(name, value);
            }
        }
    }

    public Property setValue(Node node, String name, Value[] values, Integer type)
            throws RepositoryException {
        if (getPrevious() != null) {
            return getPrevious().setValue(node, name, values, type);
        } else {
            if (type != null) {
                return node.setProperty(name, values, type);
            } else {
                return node.setProperty(name, values);
            }
        }
    }
}
