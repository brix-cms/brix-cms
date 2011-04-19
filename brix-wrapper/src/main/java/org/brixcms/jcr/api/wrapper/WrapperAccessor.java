/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.*;

import javax.jcr.*;
import javax.jcr.query.*;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

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
