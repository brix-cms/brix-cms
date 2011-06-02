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

package org.brixcms.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

/**
 * Base event for node property.
 *
 * @author Matej Knopp
 */
abstract class PropertyEvent extends NodeEvent {
    private final String propertyName;

    PropertyEvent(Property property) throws RepositoryException {
        super(property.getParent());
        this.propertyName = property.getName();
    }

    public PropertyEvent(Node node, String propertyName) {
        super(node);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Node getNode() {
        return super.getNode();
    }

    @Override
    boolean isAffected(String path) throws RepositoryException {
        String currentPath = getNode().getPath() + "/" + getPropertyName();
        return currentPath.startsWith(path);
    }

    @Override
    Event onNewEvent(Event event, QueueCallback callback) throws RepositoryException {
        if (event instanceof PropertyEvent) {
            PropertyEvent e = (PropertyEvent) event;
            if (e.getNode().getPath().equals(getNode().getPath()) && e.getPropertyName().equals(getPropertyName())) {
                return null;
            }
        }
        return super.onNewEvent(event, callback);
    }
}