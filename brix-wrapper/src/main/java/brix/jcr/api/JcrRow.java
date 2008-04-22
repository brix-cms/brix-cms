package brix.jcr.api;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrRow extends Row
{

    public static class Wrapper
    {
        public static JcrRow wrap(Row delegate, JcrSession session)
        {
            return WrapperAccessor.JcrRowWrapper.wrap(delegate, session);
        }
    };

    public Row getDelegate();

    public JcrValue getValue(String propertyName) throws ItemNotFoundException, RepositoryException;

    public Value[] getValues() throws RepositoryException;

}