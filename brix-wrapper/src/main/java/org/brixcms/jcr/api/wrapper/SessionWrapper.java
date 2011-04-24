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
import org.brixcms.jcr.base.BrixSession;
import org.brixcms.jcr.base.action.AbstractActionHandler;
import org.brixcms.jcr.base.event.EventsListener;
import org.brixcms.jcr.base.filter.ValueFilter;
import org.brixcms.jcr.base.wrapper.WrapperAccessor;
import org.xml.sax.ContentHandler;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Matej Knopp
 * @author igor.vaynberg
 */
class SessionWrapper extends AbstractWrapper implements JcrSession
{

	protected SessionWrapper(Session delegate, Behavior behavior)
	{
		super(WrapperAccessor.wrap(delegate), null);
		this.behavior = behavior;
	}

	private final Behavior behavior;

	public Behavior getBehavior()
	{
		return behavior;
	}

	@Override
	protected JcrSession getJcrSession()
	{
		return this;
	}

	public static JcrSession wrap(Session delegate, Behavior behavior)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return new SessionWrapper(delegate, behavior);
		}
	}

	@Override
	public BrixSession getDelegate()
	{
		return (BrixSession)super.getDelegate();
	}

	/** @deprecated */
	@Deprecated
	public void addLockToken(final String lt)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().addLockToken(lt);
			}
		});
	}

	public void checkPermission(final String absPath, final String actions)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().checkPermission(absPath, actions);
			}
		});
	}

	public void exportDocumentView(final String absPath, final ContentHandler contentHandler,
			final boolean skipBinary, final boolean noRecurse)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().exportDocumentView(absPath, contentHandler, skipBinary, noRecurse);
			}
		});
	}

	public void exportDocumentView(final String absPath, final OutputStream out,
			final boolean skipBinary, final boolean noRecurse)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().exportDocumentView(absPath, out, skipBinary, noRecurse);
			}
		});
	}

	public void exportSystemView(final String absPath, final ContentHandler contentHandler,
			final boolean skipBinary, final boolean noRecurse)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
			}
		});
	}

	public void exportSystemView(final String absPath, final OutputStream out,
			final boolean skipBinary, final boolean noRecurse)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().exportSystemView(absPath, out, skipBinary, noRecurse);
			}
		});
	}

	public Object getAttribute(final String name)
	{
		return executeCallback(new Callback<Object>()
		{
			public Object execute() throws Exception
			{
				return getDelegate().getAttribute(name);
			}
		});
	}

	public String[] getAttributeNames()
	{
		return executeCallback(new Callback<String[]>()
		{
			public String[] execute() throws Exception
			{
				return getDelegate().getAttributeNames();
			}
		});
	}

	public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior)
	{
		return executeCallback(new Callback<ContentHandler>()
		{
			public ContentHandler execute() throws Exception
			{
				return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
			}
		});
	}

	public JcrItem getItem(final String absPath)
	{
		return executeCallback(new Callback<JcrItem>()
		{
			public JcrItem execute() throws Exception
			{
				return JcrItem.Wrapper.wrap(getDelegate().getItem(absPath), getJcrSession());
			}
		});
	}

	/** @deprecated */
	@Deprecated
	public String[] getLockTokens()
	{
		return executeCallback(new Callback<String[]>()
		{
			public String[] execute() throws Exception
			{
				return getDelegate().getLockTokens();
			}
		});
	}

	public String getNamespacePrefix(final String uri)
	{
		return executeCallback(new Callback<String>()
		{
			public String execute() throws Exception
			{
				return getDelegate().getNamespacePrefix(uri);
			}
		});
	}

	public String[] getNamespacePrefixes()
	{
		return executeCallback(new Callback<String[]>()
		{
			public String[] execute() throws Exception
			{
				return getDelegate().getNamespacePrefixes();
			}
		});
	}

	public String getNamespaceURI(final String prefix)
	{
		return executeCallback(new Callback<String>()
		{
			public String execute() throws Exception
			{
				return getDelegate().getNamespaceURI(prefix);
			}
		});
	}

	public void nodeRemoved(JcrNode node)
	{
		// it's not enough to just remove the node from UUID map, we are
		// removing the entire subtree
		uuidMap.clear();
	}

	private Map<String, JcrNode> uuidMap = new HashMap<String, JcrNode>();

	public Repository getRepository()
	{
		return executeCallback(new Callback<Repository>()
		{
			public Repository execute() throws Exception
			{
				return getDelegate().getRepository();
			}
		});
	}

	public JcrNode getRootNode()
	{
		return executeCallback(new Callback<JcrNode>()
		{
			public JcrNode execute() throws Exception
			{
				return JcrNode.Wrapper.wrap(getDelegate().getRootNode(), getJcrSession());
			}
		});
	}

	public String getUserID()
	{
		return executeCallback(new Callback<String>()
		{
			public String execute() throws Exception
			{
				return getDelegate().getUserID();
			}
		});
	}

	public JcrValueFactory getValueFactory()
	{
		return executeCallback(new Callback<JcrValueFactory>()
		{
			public JcrValueFactory execute() throws Exception
			{
				return JcrValueFactory.Wrapper.wrap(getDelegate().getValueFactory(),
						getJcrSession());
			}
		});
	}

	public JcrWorkspace getWorkspace()
	{
		return executeCallback(new Callback<JcrWorkspace>()
		{
			public JcrWorkspace execute() throws Exception
			{
				return JcrWorkspace.Wrapper.wrap(getDelegate().getWorkspace(), getJcrSession());
			}
		});
	}

	public boolean hasPendingChanges()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().hasPendingChanges();
			}
		});
	}

	public JcrSession impersonate(final Credentials credentials)
	{
		return executeCallback(new Callback<JcrSession>()
		{
			public JcrSession execute() throws Exception
			{
				return JcrSession.Wrapper.wrap(getDelegate().impersonate(credentials),
						getBehavior());
			}
		});
	}

	public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().importXML(parentAbsPath, in, uuidBehavior);
			}
		});
	}

	public boolean isLive()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().isLive();
			}
		});
	}

	public boolean itemExists(final String absPath)
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().itemExists(absPath);
			}
		});
	}

	public void logout()
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().logout();
			}
		});
	}

	public void move(final String srcAbsPath, final String destAbsPath)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().move(srcAbsPath, destAbsPath);
			}
		});
	}

	public void refresh(final boolean keepChanges)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().refresh(keepChanges);
			}
		});
	}

	/** @deprecated */
	@Deprecated
	public void removeLockToken(final String lt)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().removeLockToken(lt);
			}
		});
	}

	public void save()
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().save();
			}
		});
	}

	public void setNamespacePrefix(final String prefix, final String uri)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().setNamespacePrefix(prefix, uri);
			}
		});
	}

	public void addActionHandler(AbstractActionHandler handler)
	{
		getDelegate().addActionHandler(handler);
	}

	public void addEventsListener(EventsListener listener)
	{
		getDelegate().addEventsListener(listener);
	}

	public Map<String, Object> getAttributesMap()
	{
		return getDelegate().getAttributesMap();
	}

	public ValueFilter getValueFilter()
	{
		return getDelegate().getValueFilter();
	}

	public void setValueFilter(ValueFilter valueFilter)
	{
		getDelegate().setValueFilter(valueFilter);
	}

	public AccessControlManager getAccessControlManager()
	{
		return executeCallback(new Callback<AccessControlManager>()
		{

			public AccessControlManager execute() throws Exception
			{
				return getDelegate().getAccessControlManager();
			}
		});
	}

	public JcrNode getNode(final String absPath)
	{
		return executeCallback(new Callback<JcrNode>()
		{

			public JcrNode execute() throws Exception
			{
				return JcrNode.Wrapper.wrap(getDelegate().getNode(absPath), getJcrSession());
			}
		});
	}

	/** @deprecated */
	@Deprecated
	public JcrNode getNodeByUUID(final String uuid)
	{
		JcrNode result = uuidMap.get(uuid);
		if (result == null)
		{
			result = executeCallback(new Callback<JcrNode>()
			{
				public JcrNode execute() throws Exception
				{
					Node node = getDelegate().getNodeByUUID(uuid);
					return JcrNode.Wrapper.wrap(node, getJcrSession());
				}
			});
			uuidMap.put(uuid, result);
		}
		return result;
	}

	public JcrNode getNodeByIdentifier(final String id)
	{
		JcrNode result = uuidMap.get(id);
		if (result == null)
		{
			result = executeCallback(new Callback<JcrNode>()
			{

				public JcrNode execute() throws Exception
				{
					Node node = getDelegate().getNodeByIdentifier(id);
					return JcrNode.Wrapper.wrap(node, getJcrSession());
				}
			});
			uuidMap.put(id, result);
		}
		return result;
	}

	public JcrProperty getProperty(final String absPath)
	{
		return executeCallback(new Callback<JcrProperty>()
		{

			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper
						.wrap(getDelegate().getProperty(absPath), getJcrSession());
			}
		});

	}

	public RetentionManager getRetentionManager()
	{
		return executeCallback(new Callback<RetentionManager>()
		{

			public RetentionManager execute() throws Exception
			{
				return getDelegate().getRetentionManager();
			}
		});

	}

	public boolean hasCapability(final String methodName, final Object target,
			final Object[] arguments)
	{
		return executeCallback(new Callback<Boolean>()
		{

			public Boolean execute() throws Exception
			{
				return getDelegate().hasCapability(methodName, target, arguments);
			}
		});
	}

	public boolean hasPermission(final String absPath, final String actions)
	{
		return executeCallback(new Callback<Boolean>()
		{

			public Boolean execute() throws Exception
			{
				return getDelegate().hasPermission(absPath, actions);
			}
		});
	}

	public boolean nodeExists(final String absPath)
	{
		return executeCallback(new Callback<Boolean>()
		{

			public Boolean execute() throws Exception
			{
				return getDelegate().nodeExists(absPath);
			}
		});
	}

	public boolean propertyExists(final String absPath)
	{
		return executeCallback(new Callback<Boolean>()
		{

			public Boolean execute() throws Exception
			{
				return getDelegate().propertyExists(absPath);
			}
		});
	}

	public void removeItem(final String absPath)
	{
		executeCallback(new VoidCallback()
		{

			public void execute() throws Exception
			{
				getDelegate().removeItem(absPath);
			}
		});

	}
}
