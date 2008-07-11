package brix.jcr.base.wrapper;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

class QueryWrapper extends BaseWrapper<Query> implements Query
{

	private QueryWrapper(Query delegate, SessionWrapper session)
	{
		super(delegate, session);
	}
	
	public static QueryWrapper wrap(Query delegate, SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return QueryWrapper.wrap(delegate, session);
		}
	}

	public QueryResult execute() throws RepositoryException
	{
		return QueryResultWrapper.wrap(getDelegate().execute(), getSessionWrapper());
	}

	public String getLanguage()
	{
		return getDelegate().getLanguage();
	}

	public String getStatement()
	{
		return getDelegate().getStatement();
	}

	public String getStoredQueryPath() throws ItemNotFoundException, RepositoryException
	{
		return getDelegate().getStoredQueryPath();
	}

	public Node storeAsNode(String absPath) throws ItemExistsException, PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, UnsupportedRepositoryOperationException, RepositoryException
	{
		return NodeWrapper.wrap(getDelegate().storeAsNode(absPath), getSessionWrapper());
	}

}
