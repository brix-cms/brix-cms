package brix.jcr.api;

import javax.jcr.Node;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrQueryManager extends QueryManager
{

    public static class Wrapper
    {
        public static JcrQueryManager wrap(QueryManager delegate, JcrSession session)
        {
            return WrapperAccessor.JcrQueryManagerWrapper.wrap(delegate, session);
        }
    }

    public QueryManager getDelegate();

    public JcrQuery createQuery(String statement, String language);

    public Query getQuery(Node node);

    public String[] getSupportedQueryLanguages();

}