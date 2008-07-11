package brix.jcr.base.wrapper;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

class NodeIteratorWrapper extends BaseWrapper<NodeIterator> implements NodeIterator
{

	private NodeIteratorWrapper(NodeIterator delegate, SessionWrapper session)
	{
		super(delegate, session);
	}

	public static NodeIteratorWrapper wrap(NodeIterator delegate, SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return new NodeIteratorWrapper(delegate, session);
		}
	}

	public Node nextNode()
	{
		return NodeWrapper.wrap(getDelegate().nextNode(), getSessionWrapper());
	}

	public long getPosition()
	{
		return getDelegate().getPosition();
	}

	public long getSize()
	{
		return getDelegate().getSize();
	}

	public void skip(long skipNum)
	{
		getDelegate().skip(skipNum);
	}

	public boolean hasNext()
	{
		return getDelegate().hasNext();
	}

	public Object next()
	{
		return  NodeWrapper.wrap((Node) getDelegate().next(), getSessionWrapper());
	}

	public void remove()
	{
		getDelegate().remove();
	}

}
