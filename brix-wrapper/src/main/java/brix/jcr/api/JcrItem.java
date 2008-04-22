package brix.jcr.api;

import javax.jcr.Item;
import javax.jcr.ItemVisitor;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrItem extends Item
{

    public static class Wrapper
    {
        public static JcrItem wrap(Item delegate, JcrSession session)
        {
            return WrapperAccessor.JcrItemWrapper.wrap(delegate, session);
        }
    };

    public Item getDelegate();

    public void accept(ItemVisitor visitor);

    public JcrItem getAncestor(int depth);

    public int getDepth();

    public String getName();

    public JcrNode getParent();

    public String getPath();

    public JcrSession getSession();

    public boolean isModified();

    public boolean isNew();

    public boolean isNode();

    public boolean isSame(Item otherItem);

    public void refresh(boolean keepChanges);

    public void remove();

    public void save();
}