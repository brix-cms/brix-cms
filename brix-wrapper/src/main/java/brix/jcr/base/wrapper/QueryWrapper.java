package brix.jcr.base.wrapper;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

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
			return new QueryWrapper(delegate, session);
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

	public String getStoredQueryPath() throws RepositoryException
	{
		return getDelegate().getStoredQueryPath();
	}

	public Node storeAsNode(String absPath) throws RepositoryException
	{
		return NodeWrapper.wrap(getDelegate().storeAsNode(absPath), getSessionWrapper());
	}

}
