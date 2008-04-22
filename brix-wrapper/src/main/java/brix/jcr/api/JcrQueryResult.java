package brix.jcr.api;

import javax.jcr.query.QueryResult;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrQueryResult extends QueryResult
{

    public static class Wrapper
    {
        public static JcrQueryResult wrap(QueryResult delegate, JcrSession session)
        {
            return WrapperAccessor.JcrQueryResultWrapper.wrap(delegate, session);
        }
    };

    public QueryResult getDelegate();

    public String[] getColumnNames();

    public JcrNodeIterator getNodes();

    public JcrRowIterator getRows();

}