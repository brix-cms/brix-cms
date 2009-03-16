package brix.jcr.base.wrapper;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

class QueryManagerWrapper extends BaseWrapper<QueryManager> implements QueryManager
{

	private QueryManagerWrapper(QueryManager delegate, SessionWrapper session)
	{
		super(delegate, session);
	}
	
	public static QueryManagerWrapper wrap(QueryManager delegate, SessionWrapper session)
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

	public Query createQuery(String statement, String language) throws RepositoryException
	{
		return QueryWrapper.wrap(getDelegate().createQuery(statement, language), getSessionWrapper());
	}

	public Query getQuery(Node node) throws RepositoryException
	{
		return QueryWrapper.wrap(getDelegate().getQuery(unwrap(node)), getSessionWrapper());
	}

	public String[] getSupportedQueryLanguages() throws RepositoryException
	{
		return getDelegate().getSupportedQueryLanguages();
	}

}
