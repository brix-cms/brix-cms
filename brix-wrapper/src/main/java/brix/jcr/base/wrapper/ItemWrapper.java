package brix.jcr.base.wrapper;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

abstract class ItemWrapper extends BaseWrapper<Item> implements Item
{

    protected ItemWrapper(Item delegate, SessionWrapper session)
    {
        super(delegate, session);
    }

    public static ItemWrapper wrap(Item item, SessionWrapper session)
    {
        if (item == null)
        {
            return null;
        }
        else if (item instanceof Version)
        {
        	return VersionWrapper.wrap((Version)item, session);
        }
        else if (item instanceof VersionHistory)
        {
        	return VersionHistoryWrapper.wrap((VersionHistory)item, session);
        }
        else if (item instanceof Node)
        {
            return NodeWrapper.wrap((Node)item, session);
        }
        else if (item instanceof Property)
        {
            return PropertyWrapper.wrap((Property)item, session);
        }
        else
        {
            throw new IllegalStateException("Unknown item subclass");
        }
    }

    public Item getAncestor(int depth) throws RepositoryException
    {
        return getDelegate().getAncestor(depth);
    }

    public int getDepth() throws RepositoryException
    {
        return getDelegate().getDepth();
    }

    public String getName() throws RepositoryException
    {
    	// TODO: Cache
        return getDelegate().getName();
    }

    public Node getParent() throws RepositoryException
    {    	
        return NodeWrapper.wrap(getDelegate().getParent(), getSessionWrapper());
    }

    public String getPath() throws RepositoryException
    {
    	// TODO: Cache
        return getDelegate().getPath();
    }

    public Session getSession() throws RepositoryException
    {
        return getSessionWrapper();
    }

    public boolean isModified()
    {
        return getDelegate().isModified();
    }

    public boolean isNew()
    {
        return getDelegate().isNew();
    }    

    public boolean isSame(Item otherItem) throws RepositoryException
    {    	
        return getDelegate().isSame(unwrap(otherItem));
    }

    public void refresh(boolean keepChanges) throws RepositoryException
    {
    	getActionHandler().beforeItemRefresh(this, keepChanges);
        getDelegate().refresh(keepChanges);
        getActionHandler().afterItemRefresh(this, keepChanges);
    }

    public void remove() throws RepositoryException
    {
    	// handler is notified from subclasses
        getDelegate().remove();
    }

    public void save() throws RepositoryException
    {
    	getActionHandler().beforeItemSave(this);
        getDelegate().save();
        getActionHandler().afterItemSave(this);
    }

}
