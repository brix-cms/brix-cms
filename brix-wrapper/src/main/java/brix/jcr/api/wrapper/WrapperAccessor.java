package brix.jcr.api.wrapper;

import javax.jcr.Item;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import brix.jcr.api.JcrItem;
import brix.jcr.api.JcrNamespaceRegistry;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrQuery;
import brix.jcr.api.JcrQueryManager;
import brix.jcr.api.JcrQueryResult;
import brix.jcr.api.JcrRow;
import brix.jcr.api.JcrRowIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;
import brix.jcr.api.JcrValueFactory;
import brix.jcr.api.JcrVersion;
import brix.jcr.api.JcrVersionHistory;
import brix.jcr.api.JcrVersionIterator;
import brix.jcr.api.JcrWorkspace;

/**
 * 
 * @author Matej Knopp
 */
public class WrapperAccessor
{

	public static class JcrItemWrapper
	{
		public static JcrItem wrap(Item delegate, JcrSession session)
		{
			return ItemWrapper.wrap(delegate, session);
		}
	};

	public static class JcrNodeWrapper
	{
		public static JcrNode wrap(Node delegate, JcrSession session)
		{
			return NodeWrapper.wrap(delegate, session);
		}
	};

	public static class JcrNodeIteratorWrapper
	{
		public static JcrNodeIterator wrap(NodeIterator delegate, JcrSession session)
		{
			return NodeIteratorWrapper.wrap(delegate, session);
		}
	};

	public static class JcrPropertyWrapper
	{
		public static JcrProperty wrap(Property delegate, JcrSession session)
		{
			return PropertyWrapper.wrap(delegate, session);
		}
	};

	public static class JcrPropertyIteratorWrapper
	{
		public static JcrPropertyIterator wrap(PropertyIterator delegate, JcrSession session)
		{
			return PropertyIteratorWrapper.wrap(delegate, session);
		}
	};

	public static class JcrQueryWrapper
	{
		public static JcrQuery wrap(Query delegate, JcrSession session)
		{
			return QueryWrapper.wrap(delegate, session);
		}
	};

	public static class JcrQueryManagerWrapper
	{
		public static JcrQueryManager wrap(QueryManager delegate, JcrSession session)
		{
			return QueryManagerWrapper.wrap(delegate, session);
		}
	}

	public static class JcrQueryResultWrapper
	{
		public static JcrQueryResult wrap(QueryResult delegate, JcrSession session)
		{
			return QueryResultWrapper.wrap(delegate, session);
		}
	};

	public static class JcrRowWrapper
	{
		public static JcrRow wrap(Row delegate, JcrSession session)
		{
			return RowWrapper.wrap(delegate, session);
		}
	};

	public static class JcrRowIteratorWrapper
	{
		public static JcrRowIterator wrap(RowIterator delegate, JcrSession session)
		{
			return RowIteratorWrapper.wrap(delegate, session);
		};
	};

	public static class JcrSessionWrapper
	{
		public static JcrSession wrap(Session delegate, JcrSession.Behavior behavior)
		{
			return SessionWrapper.wrap(delegate, behavior);
		}
	};

	public static class JcrValueWrapper
	{
		public static JcrValue wrap(Value delegate, JcrSession session)
		{
			return ValueWrapper.wrap(delegate, session);
		}

		public static JcrValue[] wrap(Value[] delegate, JcrSession session)
		{
			return ValueWrapper.wrap(delegate, session);
		}
	};

	public static class JcrValueFactoryWrapper
	{
		public static JcrValueFactory wrap(ValueFactory delegate, JcrSession session)
		{
			return ValueFactoryWrapper.wrap(delegate, session);
		}
	};

	public static class JcrVersionWrapper
	{
		public static JcrVersion wrap(Version delegate, JcrSession session)
		{
			return VersionWrapper.wrap(delegate, session);
		}

		public static JcrVersion[] wrap(Version delegate[], JcrSession session)
		{
			return VersionWrapper.wrap(delegate, session);
		}
	};

	public static class JcrVersionHistoryWrapper
	{
		public static JcrVersionHistory wrap(VersionHistory delegate, JcrSession session)
		{
			return VersionHistoryWrapper.wrap(delegate, session);
		}
	};

	public static class JcrVersionIteratorWrapper
	{
		public static JcrVersionIterator wrap(VersionIterator delegate, JcrSession session)
		{
			return VersionIteratorWrapper.wrap(delegate, session);
		}
	};

	public static class JcrWorkspaceWrapper
	{
		public static JcrWorkspace wrap(Workspace delegate, JcrSession session)
		{
			return WorkspaceWrapper.wrap(delegate, session);
		}
	};

	public static class JcrNamespaceRegistryWrapper
	{
		public static JcrNamespaceRegistry wrap(NamespaceRegistry delegate, JcrSession session)
		{
			return NamespaceRegistryWrapper.wrap(delegate, session);
		}
	}		
}
