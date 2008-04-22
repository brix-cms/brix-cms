package brix.jcr.api.wrapper;

import javax.jcr.Node;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import brix.jcr.api.JcrQuery;
import brix.jcr.api.JcrQueryManager;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class QueryManagerWrapper extends AbstractWrapper implements JcrQueryManager
{

    protected QueryManagerWrapper(QueryManager delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrQueryManager wrap(QueryManager delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new QueryManagerWrapper(delegate, session);
        }
    }

    @Override
    public QueryManager getDelegate()
    {
        return (QueryManager)super.getDelegate();
    }

    public JcrQuery createQuery(final String statement, final String language)
    {
        return executeCallback(new Callback<JcrQuery>()
        {
            public JcrQuery execute() throws Exception
            {
                return JcrQuery.Wrapper.wrap(getDelegate().createQuery(statement, language),
                        getJcrSession());
            }
        });
    }

    public Query getQuery(final Node node)
    {
        return executeCallback(new Callback<JcrQuery>()
        {
            public JcrQuery execute() throws Exception
            {
                return JcrQuery.Wrapper.wrap(getDelegate().getQuery(unwrap(node)), getJcrSession());
            }
        });
    }

    public String[] getSupportedQueryLanguages()
    {
        return executeCallback(new Callback<String[]>()
        {
            public String[] execute() throws Exception
            {
                return getDelegate().getSupportedQueryLanguages();
            }
        });
    }

}
