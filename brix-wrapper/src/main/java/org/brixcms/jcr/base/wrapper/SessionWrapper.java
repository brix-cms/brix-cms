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

import org.brixcms.jcr.base.BrixSession;
import org.brixcms.jcr.base.action.AbstractActionHandler;
import org.brixcms.jcr.base.action.CompoundActionHandler;
import org.brixcms.jcr.base.event.ChangeLog;
import org.brixcms.jcr.base.event.ChangeLogActionHandler;
import org.brixcms.jcr.base.event.EventsListener;
import org.brixcms.jcr.base.filter.ValueFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class SessionWrapper extends BaseWrapper<Session> implements BrixSession {
    final Set<Node> raisedSaveEvent = new HashSet<Node>();

    private final CompoundActionHandler actionHandler = new CompoundActionHandler();

    private final ChangeLogActionHandler changeLogActionHandler;

    private final Map<String, Object> attributesMap = new HashMap<String, Object>();

    private ValueFilter valueFilter = new ValueFilter();

    public static SessionWrapper wrap(Session session) {
        if (session == null) {
            return null;
        } else {
            return new SessionWrapper(session);
        }
    }

    private SessionWrapper(Session session) {
        super(session, null);

        changeLogActionHandler = new ChangeLogActionHandler(new ChangeLog(), this);
        actionHandler.addHandler(changeLogActionHandler);
    }

    public CompoundActionHandler getActionHandler() {
        return actionHandler;
    }

    public Map<String, Object> getAttributesMap() {
        return attributesMap;
    }

    public ValueFilter getValueFilter() {
        return valueFilter;
    }

    public void setValueFilter(ValueFilter valueFilter) {
        if (valueFilter == null) {
            throw new IllegalArgumentException("Argument 'valueFilter' may not be null.");
        }
        this.valueFilter = valueFilter;
    }



    public void addActionHandler(AbstractActionHandler handler) {
        actionHandler.addHandler(handler);
    }

    public void addEventsListener(EventsListener listener) {
        changeLogActionHandler.registerEventsListener(listener);
    }


    public Repository getRepository() {
        return getDelegate().getRepository();
    }

    public String getUserID() {
        return getDelegate().getUserID();
    }

    public String[] getAttributeNames() {
        return getDelegate().getAttributeNames();
    }

    public Object getAttribute(String name) {
        return getDelegate().getAttribute(name);
    }

    public Workspace getWorkspace() {
        return WorkspaceWrapper.wrap(getDelegate().getWorkspace(), this);
    }

    public Node getRootNode() throws RepositoryException {
        return NodeWrapper.wrap(getDelegate().getRootNode(), this);
    }

    public Session impersonate(Credentials credentials) throws RepositoryException {
        return SessionWrapper.wrap(getDelegate().impersonate(credentials));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Node getNodeByUUID(String uuid) throws RepositoryException {
        return NodeWrapper.wrap(getDelegate().getNodeByUUID(uuid), this);
    }

    public Node getNodeByIdentifier(String id) throws ItemNotFoundException, RepositoryException {
        return NodeWrapper.wrap(getDelegate().getNodeByIdentifier(id), this);
    }

    public Item getItem(String absPath) throws RepositoryException {
        return ItemWrapper.wrap(getDelegate().getItem(absPath), this);
    }

    public Node getNode(String absPath) throws PathNotFoundException, RepositoryException {
        return NodeWrapper.wrap(getDelegate().getNode(absPath), this);
    }

    public Property getProperty(String absPath) throws PathNotFoundException, RepositoryException {
        return PropertyWrapper.wrap(getDelegate().getProperty(absPath), this);
    }

    public boolean itemExists(String absPath) throws RepositoryException {
        return getDelegate().itemExists(absPath);
    }

    public boolean nodeExists(String absPath) throws RepositoryException {
        return getDelegate().nodeExists(absPath);
    }

    public boolean propertyExists(String absPath) throws RepositoryException {
        return getDelegate().propertyExists(absPath);
    }

    public void move(String srcAbsPath, String destAbsPath) throws RepositoryException {
        getActionHandler().beforeSessionNodeMove(srcAbsPath, destAbsPath);
        getDelegate().move(srcAbsPath, destAbsPath);
        getActionHandler().afterSessionNodeMove(srcAbsPath, destAbsPath);
    }

    public void removeItem(String absPath) throws VersionException, LockException,
            ConstraintViolationException, AccessDeniedException, RepositoryException {
        getDelegate().removeItem(absPath);
    }

    public void save() throws RepositoryException {
        getActionHandler().beforeSessionSave();
        getDelegate().save();
        getActionHandler().afterSessionSave();
    }

    public void refresh(boolean keepChanges) throws RepositoryException {
        getActionHandler().beforeSessionRefresh(keepChanges);
        getDelegate().refresh(keepChanges);
        getActionHandler().afterSessionRefresh(keepChanges);
    }

    public boolean hasPendingChanges() throws RepositoryException {
        return getDelegate().hasPendingChanges();
    }

    public ValueFactory getValueFactory() throws RepositoryException {
        return ValueFactoryWrapper.wrap(getDelegate().getValueFactory(), this);
    }

    public boolean hasPermission(String absPath, String actions) throws RepositoryException {
        return getDelegate().hasPermission(absPath, actions);
    }

    public void checkPermission(String absPath, String actions) throws AccessControlException,
            RepositoryException {
        getDelegate().checkPermission(absPath, actions);
    }

    public boolean hasCapability(String methodName, Object target, Object[] arguments)
            throws RepositoryException {
        return getDelegate().hasCapability(methodName, target, arguments);
    }

    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior)
            throws RepositoryException {
        return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
    }

    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior)
            throws IOException, RepositoryException {
        getActionHandler().beforeSessionImportXML(parentAbsPath);
        getDelegate().importXML(parentAbsPath, in, uuidBehavior);
        getActionHandler().afterSessionImportXML(parentAbsPath);
    }

    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary,
                                 boolean noRecurse) throws SAXException, RepositoryException {
        getDelegate().exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary,
                                 boolean noRecurse) throws IOException, RepositoryException {
        getDelegate().exportSystemView(absPath, out, skipBinary, noRecurse);
    }

    public void exportDocumentView(String absPath, ContentHandler contentHandler,
                                   boolean skipBinary, boolean noRecurse) throws SAXException, RepositoryException {
        getDelegate().exportDocumentView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary,
                                   boolean noRecurse) throws IOException, RepositoryException {
        getDelegate().exportDocumentView(absPath, out, skipBinary, noRecurse);
    }

    public void setNamespacePrefix(String prefix, String uri) throws RepositoryException {
        getDelegate().setNamespacePrefix(prefix, uri);
    }

    public String[] getNamespacePrefixes() throws RepositoryException {
        return getDelegate().getNamespacePrefixes();
    }

    public String getNamespaceURI(String prefix) throws RepositoryException {
        return getDelegate().getNamespaceURI(prefix);
    }

    public String getNamespacePrefix(String uri) throws RepositoryException {
        return getDelegate().getNamespacePrefix(uri);
    }

    public void logout() {
        getDelegate().logout();
    }

    public boolean isLive() {
        return getDelegate().isLive();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void addLockToken(String lt) {
        getDelegate().addLockToken(lt);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String[] getLockTokens() {
        return getDelegate().getLockTokens();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void removeLockToken(String lt) {
        getDelegate().removeLockToken(lt);
    }

    public AccessControlManager getAccessControlManager()
            throws UnsupportedRepositoryOperationException, RepositoryException {
        return getDelegate().getAccessControlManager();
    }

    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException,
            RepositoryException {
        return getDelegate().getRetentionManager();
    }

    @Override
    public SessionWrapper getSessionWrapper() {
        return this;
    }
}
