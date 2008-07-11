package brix.jcr.base.wrapper;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;

import brix.jcr.base.EventUtil;

class NodeWrapper extends ItemWrapper implements Node
{

    protected NodeWrapper(Node delegate, SessionWrapper session)
    {
        super(delegate, session);
    }

    public static NodeWrapper wrap(Node delegate, SessionWrapper session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new NodeWrapper(delegate, session);
        }
    }

    @Override
    public Node getDelegate()
    {
        return (Node)super.getDelegate();
    }

    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
            ConstraintViolationException, LockException, RepositoryException
    {
    	getActionHandler().beforeNodeAddMixin(this, mixinName);
        getDelegate().addMixin(mixinName);
        getActionHandler().afterNodeAddMixin(this, mixinName);
    }

    public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException,
            VersionException, ConstraintViolationException, LockException, RepositoryException
    {
    	getActionHandler().beforeNodeAdd(this, relPath, null);
        Node result = NodeWrapper.wrap(getDelegate().addNode(relPath), getSessionWrapper());
        getActionHandler().afterNodeAdd(result);
        return result;
    }

    public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException,
            PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
            ConstraintViolationException, RepositoryException
    {
    	getActionHandler().beforeNodeAdd(this, relPath, primaryNodeTypeName);
        Node result = NodeWrapper.wrap(getDelegate().addNode(relPath, primaryNodeTypeName),
                getSessionWrapper());
        getActionHandler().afterNodeAdd(result);
        return result;
    }

    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException,
            RepositoryException
    {    	
        return getDelegate().canAddMixin(mixinName);
    }

    public void cancelMerge(Version version) throws VersionException, InvalidItemStateException,
            UnsupportedRepositoryOperationException, RepositoryException
    {
    	getActionHandler().beforeNodeCancelMerge(this, version);
        getDelegate().cancelMerge(unwrap(version));
        getActionHandler().afterNodeCancelMerge(this, version);
    }

    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException,
            InvalidItemStateException, LockException, RepositoryException
    {
    	getActionHandler().beforeNodeCheckin(this);
        Version result = VersionWrapper.wrap(getDelegate().checkin(), getSessionWrapper());
        getActionHandler().afterNodeCheckin(this, result);
        return result;
    }

    public void checkout() throws UnsupportedRepositoryOperationException, LockException,
            RepositoryException
    {
    	getActionHandler().beforeNodeCheckout(this);
        getDelegate().checkout();
        getActionHandler().afterNodeCheckout(this);
    }

    @Override
    public void save() throws AccessDeniedException, ItemExistsException,
            ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException,
            VersionException, LockException, NoSuchNodeTypeException, RepositoryException
    {
    	// TODO: Remove this code
        SessionWrapper session = getSessionWrapper();
        Node node = getDelegate();
        if (session.raisedSaveEvent.contains(node) == false)
        {
            EventUtil.raiseSaveEvent(node);
            session.raisedSaveEvent.add(node);
        }

        super.save();
    }

    
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException,
            UnsupportedRepositoryOperationException, RepositoryException
    {
    	getActionHandler().beforeNodeDoneMerge(this, version);
        getDelegate().doneMerge(unwrap(version));
        getActionHandler().afterNodeDoneMerge(this, version);
    }

    public Version getBaseVersion() throws UnsupportedRepositoryOperationException,
            RepositoryException
    {
        return VersionWrapper.wrap(getDelegate().getBaseVersion(), getSessionWrapper());
    }

    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException,
            NoSuchWorkspaceException, AccessDeniedException, RepositoryException
    {
        return getDelegate().getCorrespondingNodePath(workspaceName);
    }

    public NodeDefinition getDefinition() throws RepositoryException
    {
        return getDelegate().getDefinition();
    }

    public int getIndex() throws RepositoryException
    {
        return getDelegate().getIndex();
    }

    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException,
            AccessDeniedException, RepositoryException
    {
        return getDelegate().getLock();
    }

    public NodeType[] getMixinNodeTypes() throws RepositoryException
    {
        return getDelegate().getMixinNodeTypes();
    }

    public Node getNode(String relPath) throws PathNotFoundException, RepositoryException
    {
        return new NodeWrapper(getDelegate().getNode(relPath), getSessionWrapper());
    }

    public NodeIterator getNodes() throws RepositoryException
    {
        return NodeIteratorWrapper.wrap(getDelegate().getNodes(), getSessionWrapper());
    }

    public NodeIterator getNodes(String namePattern) throws RepositoryException
    {
        return NodeIteratorWrapper.wrap(getDelegate().getNodes(namePattern), getSessionWrapper());
    }

    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException
    {
        return ItemWrapper.wrap(getDelegate().getPrimaryItem(), getSessionWrapper());
    }

    public NodeType getPrimaryNodeType() throws RepositoryException
    {
        return getDelegate().getPrimaryNodeType();
    }

    public PropertyIterator getProperties() throws RepositoryException
    {
        return PropertyIteratorWrapper.wrap(getDelegate().getProperties(), getSessionWrapper());
    }

    public PropertyIterator getProperties(String namePattern) throws RepositoryException
    {
        return PropertyIteratorWrapper.wrap(getDelegate().getProperties(namePattern),
                getSessionWrapper());
    }

    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException
    {
        return PropertyWrapper.wrap(getDelegate().getProperty(relPath), getSessionWrapper());
    }

    public PropertyIterator getReferences() throws RepositoryException
    {
        return PropertyIteratorWrapper.wrap(getDelegate().getReferences(), getSessionWrapper());
    }

    private static final String UNKNOWN = "unknown";
    
    private String uuid = UNKNOWN;
    
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException
    {
    	if (uuid == UNKNOWN) // not the identity equal
    	{
    		uuid = getDelegate().getUUID();
    	}
        return uuid;
    }

    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException,
            RepositoryException
    {
        return VersionHistoryWrapper.wrap(getDelegate().getVersionHistory(), getSessionWrapper());
    }

    public boolean hasNode(String relPath) throws RepositoryException
    {
        return getDelegate().hasNode(relPath);
    }

    public boolean hasNodes() throws RepositoryException
    {
        return getDelegate().hasNodes();
    }

    public boolean hasProperties() throws RepositoryException
    {
        return getDelegate().hasProperties();
    }

    public boolean hasProperty(String relPath) throws RepositoryException
    {
        return getDelegate().hasProperty(relPath);
    }

    public boolean holdsLock() throws RepositoryException
    {
        return getDelegate().holdsLock();
    }

    public boolean isCheckedOut() throws RepositoryException
    {
        return getDelegate().isCheckedOut();
    }

    public boolean isLocked() throws RepositoryException
    {
        return getDelegate().isLocked();
    }

    public boolean isNodeType(String nodeTypeName) throws RepositoryException
    {
        return getDelegate().isNodeType(nodeTypeName);
    }

    public Lock lock(boolean isDeep, boolean isSessionScoped)
            throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException,
            InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeLock(this, isDeep, isSessionScoped);
        Lock result = getDelegate().lock(isDeep, isSessionScoped);
        getActionHandler().afterNodeLock(this, isDeep, isSessionScoped, result);
        return result;
    }

    public NodeIterator merge(String srcWorkspace, boolean bestEffort)
            throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException,
            InvalidItemStateException, RepositoryException
    {
        return NodeIteratorWrapper.wrap(getDelegate().merge(srcWorkspace, bestEffort),
                getSessionWrapper());
    }

    public void orderBefore(String srcChildRelPath, String destChildRelPath)
            throws UnsupportedRepositoryOperationException, VersionException,
            ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException
    {
    	getActionHandler().beforeNodeChildNodesOrderChange(this);
        getDelegate().orderBefore(srcChildRelPath, destChildRelPath);
        getActionHandler().afterNodeChildNodesOrderChange(this);
    }

    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException,
            ConstraintViolationException, LockException, RepositoryException
    {
    	getActionHandler().beforeNodeRemoveMixin(this, mixinName);
        getDelegate().removeMixin(mixinName);
        getActionHandler().afterNodeRemoveMixin(this, mixinName);
    }

    public void restore(String versionName, boolean removeExisting) throws VersionException,
            ItemExistsException, UnsupportedRepositoryOperationException, LockException,
            InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeRestoreVersion(this);
        getDelegate().restore(versionName, removeExisting);
        getActionHandler().afterNodeRestoreVersion(this);
    }

    public void restore(Version version, boolean removeExisting) throws VersionException,
            ItemExistsException, UnsupportedRepositoryOperationException, LockException,
            RepositoryException
    {
    	getActionHandler().beforeNodeRestoreVersion(this);
        getDelegate().restore(unwrap(version), removeExisting);
        getActionHandler().afterNodeRestoreVersion(this);
    }

    public void restore(Version version, String relPath, boolean removeExisting)
            throws PathNotFoundException, ItemExistsException, VersionException,
            ConstraintViolationException, UnsupportedRepositoryOperationException, LockException,
            InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeRestoreVersion(this);
        getDelegate().restore(unwrap(version), relPath, removeExisting);
        getActionHandler().afterNodeRestoreVersion(this);
    }

    public void restoreByLabel(String versionLabel, boolean removeExisting)
            throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException,
            LockException, InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeRestoreVersion(this);
        getDelegate().restoreByLabel(versionLabel, removeExisting);
        getActionHandler().afterNodeRestoreVersion(this);
    }

    private void beforePropertySet(String name, Object value) throws RepositoryException
    {
    	if (value == null)
    	{
    		getActionHandler().beforePropertyRemove(this, name);
    	}
    	else
    	{
    		getActionHandler().beforePropertySet(this, name);
    	}
    }
    
    private void afterPropertySet(String name, Object value, Property property) throws RepositoryException
    {
    	if (value == null)
    	{
    		getActionHandler().afterPropertyRemove(this, name);
    	}
    	else
    	{
    		getActionHandler().afterPropertySet(property);
    	}
    }
    
    public Property setProperty(String name, Value value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, Value[] values) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, values);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, values), getSessionWrapper());
        afterPropertySet(name, values, result);
        return result;
    }

    public Property setProperty(String name, String[] values) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, values);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, values), getSessionWrapper());
        afterPropertySet(name, values, result);
        return result;
    }

    public Property setProperty(String name, String value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, InputStream value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, boolean value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, double value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, long value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, Calendar value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, Node value) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {        
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, unwrap(value)), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, Value value, int type) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, values);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, values), getSessionWrapper());
        afterPropertySet(name, values, result);
        return result;
    }

    public Property setProperty(String name, String[] values, int type)
            throws ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, values);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, values), getSessionWrapper());
        afterPropertySet(name, values, result);
        return result;
    }

    public Property setProperty(String name, String value, int type) throws ValueFormatException,
            VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	beforePropertySet(name, value);
        Property result = PropertyWrapper.wrap(getDelegate().setProperty(name, value), getSessionWrapper());
        afterPropertySet(name, value, result);
        return result;
    }

    public void unlock() throws UnsupportedRepositoryOperationException, LockException,
            AccessDeniedException, InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeUnlock(this);
        getDelegate().unlock();
        getActionHandler().afterNodeUnlock(this);
    }

    public void update(String srcWorkspaceName) throws NoSuchWorkspaceException,
            AccessDeniedException, LockException, InvalidItemStateException, RepositoryException
    {
    	getActionHandler().beforeNodeUpdate(this);
        getDelegate().update(srcWorkspaceName);
        getActionHandler().afterNodeUpdate(this);
    }
    
    @Override
    public String toString()
    {
    	try 
    	{
    		return getPath();    		
    	} catch (RepositoryException e) 
    	{
    		return e.toString();
    	}
    }

    public void accept(ItemVisitor visitor) throws RepositoryException
    {
    	visitor.visit(this);
    }

    public boolean isNode()
    {
    	return true;
    }
    
    @Override
    public void remove() throws VersionException, LockException, ConstraintViolationException, RepositoryException
    {
    	getActionHandler().beforeNodeRemove(this);
    	super.remove();
    }
}
