package brix.jcr.event.wrapper;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

class ItemWrapper extends BaseWrapper<Item> implements Item
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
            return new ItemWrapper(item, session);
        }
    }

    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        getDelegate().accept(visitor);
    }

    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException,
            RepositoryException
    {
        return getDelegate().getAncestor(depth);
    }

    public int getDepth() throws RepositoryException
    {
        return getDelegate().getDepth();
    }

    public String getName() throws RepositoryException
    {
        return getDelegate().getName();
    }

    public Node getParent() throws ItemNotFoundException, AccessDeniedException,
            RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getParent(), getSessionWrapper());
    }

    public String getPath() throws RepositoryException
    {
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

    public boolean isNode()
    {
        return getDelegate().isNode();
    }

    public boolean isSame(Item otherItem) throws RepositoryException
    {
        if (otherItem instanceof ItemWrapper)
            otherItem = ((ItemWrapper)otherItem).getDelegate();
        return getDelegate().isSame(otherItem);
    }

    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException
    {
        getDelegate().refresh(keepChanges);
    }

    public void remove() throws VersionException, LockException, ConstraintViolationException,
            RepositoryException
    {
        getDelegate().remove();
    }

    public void save() throws AccessDeniedException, ItemExistsException,
            ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException,
            VersionException, LockException, NoSuchNodeTypeException, RepositoryException
    {
        getDelegate().save();
    }

}
