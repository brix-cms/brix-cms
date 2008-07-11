package brix.jcr.base.wrapper;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

class QueryResultWrapper extends BaseWrapper<QueryResult> implements QueryResult
{

	private QueryResultWrapper(QueryResult delegate, SessionWrapper session)
	{
		super(delegate, session);
	}

	public static QueryResultWrapper wrap(QueryResult delegate, SessionWrapper session)
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

	public String[] getColumnNames() throws RepositoryException
	{
		return getDelegate().getColumnNames();
	}

	public NodeIterator getNodes() throws RepositoryException
	{
		return NodeIteratorWrapper.wrap(getDelegate().getNodes(), getSessionWrapper());
	}

	public RowIterator getRows() throws RepositoryException
	{
		return getDelegate().getRows();
	}

}
