package brix.jcr.api;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrNode extends Node, JcrItem
{

    public static class Wrapper
    {
        public static JcrNode wrap(Node delegate, JcrSession session)
        {
            return WrapperAccessor.JcrNodeWrapper.wrap(delegate, session);
        }
    };

    public Node getDelegate();

    public void addMixin(String mixinName);

    public JcrNode addNode(String relPath);

    public JcrNode addNode(String relPath, String primaryNodeTypeName);

    public boolean canAddMixin(String mixinName);

    public void cancelMerge(Version version);

    public JcrVersion checkin();

    public void checkout();

    public void doneMerge(Version version);

    public JcrVersion getBaseVersion();

    public String getCorrespondingNodePath(String workspaceName);

    public NodeDefinition getDefinition();

    public int getIndex();

    public Lock getLock();

    public NodeType[] getMixinNodeTypes();

    public JcrNode getNode(String relPath);

    public JcrNodeIterator getNodes();

    public JcrNodeIterator getNodes(String namePattern);

    public JcrItem getPrimaryItem();

    public NodeType getPrimaryNodeType();

    public JcrPropertyIterator getProperties();

    public JcrPropertyIterator getProperties(String namePattern);

    public JcrProperty getProperty(String relPath);

    public JcrPropertyIterator getReferences();

    public String getUUID();

    public JcrVersionHistory getVersionHistory();

    public boolean hasNode(String relPath);

    public boolean hasNodes();

    public boolean hasProperties();

    public boolean hasProperty(String relPath);

    public boolean holdsLock();

    public boolean isCheckedOut();

    public boolean isLocked();

    public boolean isNodeType(String nodeTypeName);

    public Lock lock(boolean isDeep, boolean isSessionScoped);

    public JcrNodeIterator merge(String srcWorkspace, boolean bestEffort);

    public void orderBefore(String srcChildRelPath, String destChildRelPath);

    public void removeMixin(String mixinName);

    public void restore(String versionName, boolean removeExisting);

    public void restore(Version version, boolean removeExisting);

    public void restore(Version version, String relPath, boolean removeExisting);

    public void restoreByLabel(String versionLabel, boolean removeExisting);

    public JcrProperty setProperty(String name, Value value);

    public JcrProperty setProperty(String name, Value[] values);

    public JcrProperty setProperty(String name, String[] values);

    public JcrProperty setProperty(String name, String value);

    public JcrProperty setProperty(String name, InputStream value);

    public JcrProperty setProperty(String name, boolean value);

    public JcrProperty setProperty(String name, double value);

    public JcrProperty setProperty(String name, long value);

    public JcrProperty setProperty(String name, Calendar value);

    public JcrProperty setProperty(String name, Node value);

    public JcrProperty setProperty(String name, Value value, int type);

    public JcrProperty setProperty(String name, Value[] values, int type);

    public JcrProperty setProperty(String name, String[] values, int type);

    public JcrProperty setProperty(String name, String value, int type);

    public void unlock();

    public void update(String srcWorkspaceName);

}