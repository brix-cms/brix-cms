package brix.jcr.api;

import javax.jcr.query.Query;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrQuery extends Query
{

    public static class Wrapper
    {
        public static JcrQuery wrap(Query delegate, JcrSession session)
        {
            return WrapperAccessor.JcrQueryWrapper.wrap(delegate, session);
        }
    };

    public Query getDelegate();

    public JcrQueryResult execute();

    public String getLanguage();

    public String getStatement();

    public String getStoredQueryPath();

    public JcrNode storeAsNode(String absPath);

}