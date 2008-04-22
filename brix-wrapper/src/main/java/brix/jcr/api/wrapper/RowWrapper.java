package brix.jcr.api.wrapper;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import brix.jcr.api.JcrRow;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;

/**
 * 
 * @author Matej Knopp
 */
class RowWrapper extends AbstractWrapper implements JcrRow
{

    protected RowWrapper(Row delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrRow wrap(Row delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new RowWrapper(delegate, session);
        }
    }

    @Override
    public Row getDelegate()
    {
        return (Row)super.getDelegate();
    }

    public JcrValue getValue(final String propertyName) throws ItemNotFoundException,
            RepositoryException
    {
        return executeCallback(new Callback<JcrValue>()
        {
            public JcrValue execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValue(propertyName), getJcrSession());
            }
        });
    }

    public Value[] getValues() throws RepositoryException
    {
        return executeCallback(new Callback<JcrValue[]>()
        {
            public JcrValue[] execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValues(), getJcrSession());
            }
        });
    }

}
