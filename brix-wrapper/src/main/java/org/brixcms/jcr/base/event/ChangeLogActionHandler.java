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

import org.brixcms.jcr.base.action.AbstractActionHandler;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.Lock;
import javax.jcr.version.Version;
import java.util.ArrayList;
import java.util.List;

public class ChangeLogActionHandler extends AbstractActionHandler {
    private final ChangeLog changeLog;
    private final Session session;

    private List<Event> events = null;

    private List<EventsListener> eventsListeners = new ArrayList<EventsListener>();

    public ChangeLogActionHandler(ChangeLog changeLog, Session session) {
        this.changeLog = changeLog;
        this.session = session;
    }

    @Override
    public void afterItemRefresh(Item item, boolean keepChanges) throws RepositoryException {
        if (keepChanges == false) {
            discardEvents(item);
        }
    }

    private void discardEvents(Item item) throws RepositoryException {
        discardEvents(item.getPath());
    }

    @Override
    public void afterItemSave(Item item) throws RepositoryException {
        if (events != null && !events.isEmpty()) {
            for (EventsListener l : eventsListeners) {
                l.handleEventsAfterSave(session, item, events);
            }
        }
        events = null;
    }

    @Override
    public void afterNodeAdd(Node node) throws RepositoryException {
        changeLog.addEvent(new AddNodeEvent(node));
    }

    @Override
    public void afterNodeAddMixin(Node node, String mixin) throws RepositoryException {
        changeLog.addEvent(new ChangeNodeMixinsEvent(node));
    }

    @Override
    public void afterNodeCancelMerge(Node node, Version version) throws RepositoryException {
        discardEvents(node);
    }

    @Override
    public void afterNodeCheckin(Node node, Version version) throws RepositoryException {

    }

    @Override
    public void afterNodeCheckout(Node node) throws RepositoryException {

    }

    @Override
    public void afterNodeChildNodesOrderChange(Node node) throws RepositoryException {
        changeLog.addEvent(new ChangeChildNodesOrderEvent(node));
    }

    @Override
    public void afterNodeDoneMerge(Node node, Version version) throws RepositoryException {
        discardEvents(node);
    }

    @Override
    public void afterNodeLock(Node node, boolean isDeep, boolean isSessionScoped, Lock lock) throws RepositoryException {

    }

    @Override
    public void afterNodeRemoveMixin(Node node, String mixin) throws RepositoryException {

    }

    @Override
    public void afterNodeRestoreVersion(Node node) throws RepositoryException {
        discardEvents(node);
    }

    @Override
    public void afterNodeUnlock(Node node) throws RepositoryException {

    }

    @Override
    public void afterNodeUpdate(Node node) throws RepositoryException {
        discardEvents(node);
    }

    @Override
    public void afterPropertyRemove(Node node, String propertyName) throws RepositoryException {
        changeLog.addEvent(new RemovePropertyEvent(node, propertyName));
    }

    @Override
    public void afterPropertySet(Property property) throws RepositoryException {
        changeLog.addEvent(new SetPropertyEvent(property));
    }

    @Override
    public void afterSessionImportXML(String parentAbsPath) throws RepositoryException {
        discardEvents(parentAbsPath);
    }

    private void discardEvents(String path) throws RepositoryException {
        changeLog.removeAndGetAffectedEvents(path);
    }

    @Override
    public void afterSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException {
        Node node = (Node) session.getItem(destinationPath);
        changeLog.addEvent(new MoveNodeEvent(node, sourcePath));
    }

    @Override
    public void afterSessionRefresh(boolean keepChanges) throws RepositoryException {
        if (!keepChanges) {
            discardEvents((String) null);
        }
    }

    @Override
    public void afterSessionSave() throws RepositoryException {
        if (events != null && !events.isEmpty()) {
            for (EventsListener l : eventsListeners) {
                l.handleEventsAfterSave(session, null, events);
            }
        }
        events = null;
    }

    @Override
    public void afterWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        discardEvents(destAbsPath);
    }

    @Override
    public void afterWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException {
        discardEvents(destAbsPath);
    }

    @Override
    public void afterWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        discardEvents(destAbsPath);
    }

    @Override
    public void afterWorkspaceImportXML(String parentAbsPath) throws RepositoryException {
        discardEvents(parentAbsPath);
    }

    @Override
    public void afterWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException {
        discardEvents(destAbsPath);
    }

    @Override
    public void beforeItemRefresh(Item item, boolean keepChanges) throws RepositoryException {

    }

    @Override
    public void beforeItemSave(Item item) throws RepositoryException {
        events = changeLog.removeAndGetAffectedEvents(item.getPath());
        if (events != null && !events.isEmpty()) {
            for (EventsListener l : eventsListeners) {
                l.handleEventsBeforeSave(session, item, events);
            }
        }
    }

    @Override
    public void beforeNodeAdd(Node parent, String name, String primaryType) throws RepositoryException {

    }

    @Override
    public void beforeNodeAddMixin(Node node, String mixin) throws RepositoryException {

    }

    @Override
    public void beforeNodeCancelMerge(Node node, Version version) throws RepositoryException {

    }

    @Override
    public void beforeNodeCheckin(Node node) throws RepositoryException {

    }

    @Override
    public void beforeNodeCheckout(Node node) throws RepositoryException {

    }

    @Override
    public void beforeNodeChildNodesOrderChange(Node node) throws RepositoryException {

    }

    @Override
    public void beforeNodeDoneMerge(Node node, Version version) throws RepositoryException {

    }

    @Override
    public void beforeNodeLock(Node node, boolean isDeep, boolean isSessionScoped) throws RepositoryException {

    }

    @Override
    public void beforeNodeRemove(Node node) throws RepositoryException {
        changeLog.addEvent(new BeforeRemoveNodeEvent(node));
    }

    @Override
    public void beforeNodeRemoveMixin(Node node, String mixin) throws RepositoryException {

    }

    @Override
    public void beforeNodeRestoreVersion(Node node) throws RepositoryException {

    }

    @Override
    public void beforeNodeUnlock(Node node) throws RepositoryException {

    }

    @Override
    public void beforeNodeUpdate(Node node) throws RepositoryException {

    }

    @Override
    public void beforePropertyRemove(Node node, String propertyName) throws RepositoryException {

    }

    @Override
    public void beforePropertySet(Node node, String propertyName) throws RepositoryException {

    }

    @Override
    public void beforeSessionImportXML(String parentAbsPath) throws RepositoryException {

    }

    @Override
    public void beforeSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException {

    }

    @Override
    public void beforeSessionRefresh(boolean keepChanges) throws RepositoryException {

    }

    @Override
    public void beforeSessionSave() throws RepositoryException {
        events = changeLog.removeAndGetAffectedEvents(null);
        if (events != null && !events.isEmpty()) {
            for (EventsListener l : eventsListeners) {
                l.handleEventsBeforeSave(session, null, events);
            }
        }
    }

    @Override
    public void beforeWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {

    }

    @Override
    public void beforeWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException {

    }

    @Override
    public void beforeWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {

    }

    @Override
    public void beforeWorkspaceImportXML(String parentAbsPath) throws RepositoryException {

    }

    @Override
    public void beforeWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException {

    }

    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    public void registerEventsListener(EventsListener listener) {
        eventsListeners.add(listener);
    }
}
