/**
 * 
 */
package brix.jcr.api.wrapper;

import javax.jcr.Item;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;

import brix.jcr.api.JcrItem;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class ItemWrapper extends AbstractWrapper implements JcrItem
{

    public Item getDelegate()
    {
        return (Item)super.getDelegate();
    }

    public static JcrItem wrap(Item delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else if (delegate instanceof Node)
        {
            return NodeWrapper.wrap((Node)delegate, session);
        }
        else if (delegate instanceof Property)
        {
            return PropertyWrapper.wrap((Property)delegate, session);
        }
        else
        {
            return new ItemWrapper(delegate, session);
        }
    }

    protected ItemWrapper(Item delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public void accept(final ItemVisitor visitor)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().accept(visitor);
            }
        });
    }

    public JcrItem getAncestor(final int depth)
    {
        return executeCallback(new Callback<JcrItem>()
        {
            public JcrItem execute() throws Exception
            {
                return JcrItem.Wrapper.wrap(getDelegate().getAncestor(depth), getJcrSession());
            }
        });
    }

    public int getDepth()
    {
        return executeCallback(new Callback<Integer>()
        {
            public Integer execute() throws Exception
            {
                return getDelegate().getDepth();
            }
        });
    }

    public String getName()
    {
        return executeCallback(new Callback<String>()
        {
            public String execute() throws Exception
            {
                return getDelegate().getName();
            }
        });
    }

    public JcrNode getParent()
    {
        return executeCallback(new Callback<JcrNode>()
        {
            public JcrNode execute() throws Exception
            {
                return JcrNode.Wrapper.wrap(getDelegate().getParent(), getJcrSession());
            }
        });
    }

    public String getPath()
    {
        return executeCallback(new Callback<String>()
        {
            public String execute() throws Exception
            {
                return getDelegate().getPath();
            }
        });
    }

    public JcrSession getSession()
    {
        return getJcrSession();
    }

    public boolean isModified()
    {
        return executeCallback(new Callback<Boolean>()
        {
            public Boolean execute() throws Exception
            {
                return getDelegate().isModified();
            }
        });
    }

    public boolean isNew()
    {
        return getDelegate().isNew();
    }

    public boolean isNode()
    {
        return getDelegate().isNode();
    }

    public boolean isSame(final Item otherItem)
    {
        return executeCallback(new Callback<Boolean>()
        {
            public Boolean execute() throws Exception
            {
                return getDelegate().isSame(unwrap(otherItem));
            }
        });
    }

    public void refresh(final boolean keepChanges)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().refresh(keepChanges);
            }
        });
    }

    public void remove()
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().remove();
            }
        });
    }

    public void save()
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().save();
            }
        });
    }

    @Override
    public String toString()
    {
        return getPath();
    }
}
