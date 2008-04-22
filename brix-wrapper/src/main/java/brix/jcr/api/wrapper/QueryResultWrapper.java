package brix.jcr.api.wrapper;

import javax.jcr.query.QueryResult;

import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrQueryResult;
import brix.jcr.api.JcrRowIterator;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class QueryResultWrapper extends AbstractWrapper implements JcrQueryResult
{

    protected QueryResultWrapper(QueryResult delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrQueryResult wrap(QueryResult delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new QueryResultWrapper(delegate, session);
        }
    }

    @Override
    public QueryResult getDelegate()
    {
        return (QueryResult)super.getDelegate();
    }

    public String[] getColumnNames()
    {
        return executeCallback(new Callback<String[]>()
        {
            public String[] execute() throws Exception
            {
                return getDelegate().getColumnNames();
            }
        });
    }

    public JcrNodeIterator getNodes()
    {
        return executeCallback(new Callback<JcrNodeIterator>()
        {
            public JcrNodeIterator execute() throws Exception
            {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(), getJcrSession());
            }
        });
    }

    public JcrRowIterator getRows()
    {
        return executeCallback(new Callback<JcrRowIterator>()
        {
            public JcrRowIterator execute() throws Exception
            {
                return JcrRowIterator.Wrapper.wrap(getDelegate().getRows(), getJcrSession());
            }
        });
    }

}
