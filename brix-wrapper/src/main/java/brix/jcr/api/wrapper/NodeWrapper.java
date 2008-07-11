package brix.jcr.api.wrapper;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;

import brix.jcr.api.JcrItem;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrVersion;
import brix.jcr.api.JcrVersionHistory;
import brix.jcr.api.JcrSession.Behavior;

/**
 * 
 * @author Matej Knopp
 */
public class NodeWrapper extends ItemWrapper implements JcrNode
{

	public NodeWrapper(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	public static JcrNode wrap(Node delegate, JcrSession session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			Behavior behavior = session.getBehavior();
			if (behavior != null)
			{
				JcrNode node = behavior.wrap(delegate, session);
				if (node != null)
				{
					return node;
				}
			}
			return new NodeWrapper(delegate, session);
		}
	}

	@Override
	public Node getDelegate()
	{
		return (Node) super.getDelegate();
	}

	public void addMixin(final String mixinName)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().addMixin(mixinName);
			}
		});
	}

	public JcrNode addNode(final String relPath)
	{
		return executeCallback(new Callback<JcrNode>()
		{
			public JcrNode execute() throws Exception
			{
				return JcrNode.Wrapper.wrap(getDelegate().addNode(relPath), getJcrSession());
			}
		});
	}

	public JcrNode addNode(final String relPath, final String primaryNodeTypeName)
	{
		return executeCallback(new Callback<JcrNode>()
		{
			public JcrNode execute() throws Exception
			{
				return JcrNode.Wrapper.wrap(getDelegate().addNode(relPath, primaryNodeTypeName), getJcrSession());
			}
		});
	}

	public boolean canAddMixin(final String mixinName)
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().canAddMixin(mixinName);
			}
		});
	}

	public void cancelMerge(final Version version)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().cancelMerge(unwrap(version));
			}
		});
	}

	public JcrVersion checkin()
	{
		return executeCallback(new Callback<JcrVersion>()
		{
			public JcrVersion execute() throws Exception
			{
				final Node delegate = getDelegate();
				if (delegate.isNodeType("mix:versionable"))
				{
					return JcrVersion.Wrapper.wrap(delegate.checkin(), getJcrSession());
				}
				else
				{
					return null;
				}
			}
		});
	}

	@Override
	public void save()
	{
		Behavior behavior = getJcrSession().getBehavior();
		if (behavior != null)
		{
			behavior.nodeSaved(this);
		}
		super.save();
	}

	public void checkout()
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				if (getDelegate().isNodeType("mix:versionable"))
				{
					getDelegate().checkout();
				}
			}
		});
	}

	public void doneMerge(final Version version)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().doneMerge(unwrap(version));
			}
		});
	}

	public JcrVersion getBaseVersion()
	{
		return executeCallback(new Callback<JcrVersion>()
		{
			public JcrVersion execute() throws Exception
			{
				return JcrVersion.Wrapper.wrap(getDelegate().getBaseVersion(), getJcrSession());
			}
		});
	}

	public String getCorrespondingNodePath(final String workspaceName)
	{
		return executeCallback(new Callback<String>()
		{
			public String execute() throws Exception
			{
				return getDelegate().getCorrespondingNodePath(workspaceName);
			}
		});
	}

	public NodeDefinition getDefinition()
	{
		return executeCallback(new Callback<NodeDefinition>()
		{
			public NodeDefinition execute() throws Exception
			{
				return getDelegate().getDefinition();
			}
		});
	}

	public int getIndex()
	{
		return executeCallback(new Callback<Integer>()
		{
			public Integer execute() throws Exception
			{
				return getDelegate().getIndex();
			}
		});
	}

	public Lock getLock()
	{
		return executeCallback(new Callback<Lock>()
		{
			public Lock execute() throws Exception
			{
				return getDelegate().getLock();
			}
		});
	}

	public NodeType[] getMixinNodeTypes()
	{
		return executeCallback(new Callback<NodeType[]>()
		{
			public NodeType[] execute() throws Exception
			{
				return getDelegate().getMixinNodeTypes();
			}
		});
	}

	public JcrNode getNode(final String relPath)
	{
		return executeCallback(new Callback<JcrNode>()
		{
			public JcrNode execute() throws Exception
			{
				return JcrNode.Wrapper.wrap(getDelegate().getNode(relPath), getJcrSession());
			}
		});
	}

	public JcrNodeIterator getNodes()
	{
		return executeCallback(new Callback<JcrNodeIterator>()
		{
			public JcrNodeIterator execute() throws Exception
			{
				return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(), getJcrSession());
			}
		});
	}

	public JcrNodeIterator getNodes(final String namePattern)
	{
		return executeCallback(new Callback<JcrNodeIterator>()
		{
			public JcrNodeIterator execute() throws Exception
			{
				return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(namePattern), getJcrSession());
			}
		});
	}

	public JcrItem getPrimaryItem()
	{
		return executeCallback(new Callback<JcrItem>()
		{
			public JcrItem execute() throws Exception
			{
				return JcrItem.Wrapper.wrap(getDelegate().getPrimaryItem(), getJcrSession());
			}
		});
	}

	public NodeType getPrimaryNodeType()
	{
		return executeCallback(new Callback<NodeType>()
		{
			public NodeType execute() throws Exception
			{
				return getDelegate().getPrimaryNodeType();
			}
		});
	}

	public JcrPropertyIterator getProperties()
	{
		return executeCallback(new Callback<JcrPropertyIterator>()
		{
			public JcrPropertyIterator execute() throws Exception
			{
				return JcrPropertyIterator.Wrapper.wrap(getDelegate().getProperties(), getJcrSession());
			}
		});
	}

	public JcrPropertyIterator getProperties(final String namePattern)
	{
		return executeCallback(new Callback<JcrPropertyIterator>()
		{
			public JcrPropertyIterator execute() throws Exception
			{
				return JcrPropertyIterator.Wrapper.wrap(getDelegate().getProperties(namePattern), getJcrSession());
			}
		});
	}

	public JcrProperty getProperty(final String relPath)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().getProperty(relPath), getJcrSession());
			}
		});
	}

	public JcrPropertyIterator getReferences()
	{
		return executeCallback(new Callback<JcrPropertyIterator>()
		{
			public JcrPropertyIterator execute() throws Exception
			{
				return JcrPropertyIterator.Wrapper.wrap(getDelegate().getReferences(), getJcrSession());
			}
		});
	}

	public String getUUID()
	{
		return executeCallback(new Callback<String>()
		{
			public String execute() throws Exception
			{
				return getDelegate().getUUID();
			}
		});
	}

	public JcrVersionHistory getVersionHistory()
	{
		return executeCallback(new Callback<JcrVersionHistory>()
		{
			public JcrVersionHistory execute() throws Exception
			{
				return JcrVersionHistory.Wrapper.wrap(getDelegate().getVersionHistory(), getJcrSession());
			}
		});
	}

	public boolean hasNode(final String relPath)
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().hasNode(relPath);
			}
		});
	}

	public boolean hasNodes()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().hasNodes();
			}
		});
	}

	public boolean hasProperties()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().hasProperties();
			}
		});
	}

	public boolean hasProperty(final String relPath)
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().hasProperty(relPath);
			}
		});
	}

	public boolean holdsLock()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().holdsLock();
			}
		});
	}

	public boolean isCheckedOut()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().isCheckedOut();
			}
		});
	}

	public boolean isLocked()
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().isLocked();
			}
		});
	}

	public boolean isNodeType(final String nodeTypeName)
	{
		return executeCallback(new Callback<Boolean>()
		{
			public Boolean execute() throws Exception
			{
				return getDelegate().isNodeType(nodeTypeName);
			}
		});
	}

	public Lock lock(final boolean isDeep, final boolean isSessionScoped)
	{
		return executeCallback(new Callback<Lock>()
		{
			public Lock execute() throws Exception
			{
				return getDelegate().lock(isDeep, isSessionScoped);
			}
		});
	}

	public JcrNodeIterator merge(final String srcWorkspace, final boolean bestEffort)
	{
		return executeCallback(new Callback<JcrNodeIterator>()
		{
			public JcrNodeIterator execute() throws Exception
			{
				return JcrNodeIterator.Wrapper.wrap(getDelegate().merge(srcWorkspace, bestEffort), getJcrSession());
			}
		});
	}

	public void orderBefore(final String srcChildRelPath, final String destChildRelPath)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().orderBefore(srcChildRelPath, destChildRelPath);
			}
		});

	}

	public void removeMixin(final String mixinName)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().removeMixin(mixinName);
			}
		});
	}

	public void restore(final String versionName, final boolean removeExisting)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().restore(versionName, removeExisting);
			}
		});
	}

	public void restore(final Version version, final boolean removeExisting)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().restore(unwrap(version), removeExisting);
			}
		});
	}

	public void restore(final Version version, final String relPath, final boolean removeExisting)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().restore(unwrap(version), relPath, removeExisting);
			}
		});
	}

	public void restoreByLabel(final String versionLabel, final boolean removeExisting)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().restoreByLabel(versionLabel, removeExisting);
			}
		});
	}

	public JcrProperty setProperty(final String name, final Value value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final Value[] values)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				final Value[] unwrapped = unwrap(values, new Value[values.length]);
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrapped), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final String[] values)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, values), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final String value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final InputStream value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final boolean value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final double value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final long value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final Calendar value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final Node value)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final Value value, final int type)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value), type), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final Value[] values, final int type)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				final Value[] unwrapped = unwrap(values, new Value[values.length]);
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrapped, type), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final String[] values, final int type)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, values, type), getJcrSession());
			}
		});
	}

	public JcrProperty setProperty(final String name, final String value, final int type)
	{
		return executeCallback(new Callback<JcrProperty>()
		{
			public JcrProperty execute() throws Exception
			{
				return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value, type), getJcrSession());
			}
		});
	}

	public void unlock()
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().unlock();
			}
		});
	}

	public void update(final String srcWorkspaceName)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				getDelegate().update(srcWorkspaceName);
			}
		});
	}

	@Override
	public String toString()
	{
		return getPath() + " [" + getPrimaryNodeType().getName() + "]";
	}
	
	public void accept(final ItemVisitor visitor)
	{
		executeCallback(new VoidCallback()
		{
			public void execute() throws Exception
			{
				visitor.visit(NodeWrapper.this);
			}
		});
	}
}
