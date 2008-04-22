package brix.jcr.api;

import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.xml.sax.ContentHandler;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrSession extends Session
{

    public static class Wrapper
    {
        public static JcrSession wrap(Session delegate, Behavior behavior)
        {
            return WrapperAccessor.JcrSessionWrapper.wrap(delegate, behavior);
        }

        public static JcrSession wrap(Session delegate)
        {
            return WrapperAccessor.JcrSessionWrapper.wrap(delegate, null);
        }
    };

    /**
     * Allows to customize behavior for nodes within the session to which it was passed.
     * 
     * @author Matej Knopp
     * 
     */
    public static interface Behavior
    {
        /**
         * Allows to use custom wrapper for given node.
         * 
         * @param node
         * @return
         */
        public JcrNode wrap(Node node, JcrSession session);

        /**
         * Invoked when the {@link JcrNode#save()} method was called on the given node.
         * 
         * @param node
         */
        public void nodeSaved(JcrNode node);

        /**
         * Invoked when the underlying JCR method throws an exception.
         * 
         * @param e
         */
        public void handleException(Exception e);
    };

    public Behavior getBehavior();

    public Session getDelegate();

    public void addLockToken(final String lt);

    public void checkPermission(final String absPath, final String actions);

    public void exportDocumentView(final String absPath, final ContentHandler contentHandler,
            final boolean skipBinary, final boolean noRecurse);

    public void exportDocumentView(final String absPath, final OutputStream out,
            final boolean skipBinary, final boolean noRecurse);

    public void exportSystemView(final String absPath, final ContentHandler contentHandler,
            final boolean skipBinary, final boolean noRecurse);

    public void exportSystemView(final String absPath, final OutputStream out,
            final boolean skipBinary, final boolean noRecurse);

    public Object getAttribute(final String name);

    public String[] getAttributeNames();

    public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior);

    public JcrItem getItem(final String absPath);

    public String[] getLockTokens();

    public String getNamespacePrefix(final String uri);

    public String[] getNamespacePrefixes();

    public String getNamespaceURI(final String prefix);

    public JcrNode getNodeByUUID(final String uuid);

    public Repository getRepository();

    public JcrNode getRootNode();

    public String getUserID();

    public JcrValueFactory getValueFactory();

    public JcrWorkspace getWorkspace();

    public boolean hasPendingChanges();

    public JcrSession impersonate(final Credentials credentials);

    public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior);

    public boolean isLive();

    public boolean itemExists(final String absPath);

    public void logout();

    public void move(final String srcAbsPath, final String destAbsPath);

    public void refresh(final boolean keepChanges);

    public void removeLockToken(final String lt);

    public void save();

    public void setNamespacePrefix(final String prefix, final String uri);

}