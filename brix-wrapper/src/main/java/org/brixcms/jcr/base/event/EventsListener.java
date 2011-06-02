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

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;

/**
 * Object that gets notifications before and after save method has been invoked on item or session.
 *
 * @author Matej Knopp
 */
public interface EventsListener {
    /**
     * Invoked right after the {@link Item#save()} method was called on the given item or after Session#save is called, in
     * which case the item will be <code>null</code>.
     *
     * @param item
     * @param events list of events concerning the item or it's children that have occurred before the save call
     * @throws RepositoryException
     */
    public void handleEventsAfterSave(Session session, Item item, List<Event> events) throws RepositoryException;

    /**
     * Invoked before the {@link Item#save()} method is called on the given item or before Session#save is called, in which
     * case the item will be <code>null</code>.
     *
     * @param item
     * @param events list of events concerning the item or it's children that have occurred before the save call
     * @throws RepositoryException
     */
    public void handleEventsBeforeSave(Session session, Item item, List<Event> events) throws RepositoryException;
}
