package brix.jcr.event.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

class SessionWrapper extends BaseWrapper<Session> implements Session
{

    private SessionWrapper(Session session)
    {
        super(session, null);
    }

    public static SessionWrapper wrap(Session session)
    {
        if (session == null)
        {
            return null;
        }
        else
        {
            return new SessionWrapper(session);
        }
    }

    public void addLockToken(String lt)
    {
        getDelegate().addLockToken(lt);
    }

    public void checkPermission(String absPath, String actions) throws AccessControlException,
            RepositoryException
    {
        getDelegate().checkPermission(absPath, actions);
    }

    public void exportDocumentView(String absPath, ContentHandler contentHandler,
            boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException,
            RepositoryException
    {
        getDelegate().exportDocumentView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary,
            boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException
    {
        getDelegate().exportDocumentView(absPath, out, skipBinary, noRecurse);
    }

    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary,
            boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException
    {
        getDelegate().exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary,
            boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException
    {
        getDelegate().exportSystemView(absPath, out, skipBinary, noRecurse);
    }

    public Object getAttribute(String name)
    {
        return getDelegate().getAttribute(name);
    }

    public String[] getAttributeNames()
    {
        return getDelegate().getAttributeNames();
    }

    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior)
            throws PathNotFoundException, ConstraintViolationException, VersionException,
            LockException, RepositoryException
    {
        return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
    }

    public Item getItem(String absPath) throws PathNotFoundException, RepositoryException
    {
        return ItemWrapper.wrap(getDelegate().getItem(absPath), this);
    }

    public String[] getLockTokens()
    {
        return getDelegate().getLockTokens();
    }

    public String getNamespacePrefix(String uri) throws NamespaceException, RepositoryException
    {
        return getDelegate().getNamespacePrefix(uri);
    }

    public String[] getNamespacePrefixes() throws RepositoryException
    {
        return getDelegate().getNamespacePrefixes();
    }

    public String getNamespaceURI(String prefix) throws NamespaceException, RepositoryException
    {
        return getDelegate().getNamespaceURI(prefix);
    }

    public Node getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getNodeByUUID(uuid), this);
    }

    public Repository getRepository()
    {
        return getDelegate().getRepository();
    }

    public Node getRootNode() throws RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getRootNode(), this);
    }

    public String getUserID()
    {
        return getDelegate().getUserID();
    }

    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException,
            RepositoryException
    {
        return ValueFactoryWrapper.wrap(getDelegate().getValueFactory(), this);
    }

    public Workspace getWorkspace()
    {
        return getDelegate().getWorkspace();
    }

    public boolean hasPendingChanges() throws RepositoryException
    {
        return getDelegate().hasPendingChanges();
    }

    public Session impersonate(Credentials credentials) throws LoginException, RepositoryException
    {
        return SessionWrapper.wrap(getDelegate().impersonate(credentials));
    }

    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior)
            throws IOException, PathNotFoundException, ItemExistsException,
            ConstraintViolationException, VersionException, InvalidSerializedDataException,
            LockException, RepositoryException
    {
        getDelegate().importXML(parentAbsPath, in, uuidBehavior);
    }

    public boolean isLive()
    {
        return getDelegate().isLive();
    }

    public boolean itemExists(String absPath) throws RepositoryException
    {
        return getDelegate().itemExists(absPath);
    }

    public void logout()
    {
        getDelegate().logout();
    }

    public void move(String srcAbsPath, String destAbsPath) throws ItemExistsException,
            PathNotFoundException, VersionException, ConstraintViolationException, LockException,
            RepositoryException
    {
        getDelegate().move(srcAbsPath, destAbsPath);
    }

    public void refresh(boolean keepChanges) throws RepositoryException
    {
        getDelegate().refresh(keepChanges);
    }

    public void removeLockToken(String lt)
    {
        getDelegate().removeLockToken(lt);
    }

    public void save() throws AccessDeniedException, ItemExistsException,
            ConstraintViolationException, InvalidItemStateException, VersionException,
            LockException, NoSuchNodeTypeException, RepositoryException
    {
        getDelegate().save();
    }

    public void setNamespacePrefix(String prefix, String uri) throws NamespaceException,
            RepositoryException
    {
        getDelegate().setNamespacePrefix(prefix, uri);
    }

    final Set<Node> raisedSaveEvent = new HashSet<Node>();
}
