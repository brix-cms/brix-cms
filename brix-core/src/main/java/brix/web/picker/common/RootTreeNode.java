package brix.web.picker.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.tree.JcrTreeNode;

public class RootTreeNode implements JcrTreeNode
{

	private final String workspaceId;

	public RootTreeNode(String workpaceId)
	{
		this.workspaceId = workpaceId;
	}

	private List<JcrTreeNode> children = null;

	private void buildChildren()
	{
		children = new ArrayList<JcrTreeNode>();
		JcrSession session = Brix.get().getCurrentSession(workspaceId);
		BrixNode root = (BrixNode) session.getItem(Brix.get().getRootPath());
		JcrNodeIterator iterator = root.getNodes();
		while (iterator.hasNext())
		{
			BrixNode node = (BrixNode) iterator.nextNode();
			if (node instanceof TreeAwareNode)
			{
				JcrTreeNode treeNode = ((TreeAwareNode) node).getTreeNode(node);
				if (treeNode != null)
				{
					children.add(treeNode);
				}
			}
		}
		Collections.sort(children, new Comparator<JcrTreeNode>()
		{
			public int compare(JcrTreeNode o1, JcrTreeNode o2)
			{
				return o1.getClass().getName().compareTo(o2.getClass().getName());
			}
		});
	}

	public List<? extends JcrTreeNode> getChildren()
	{
		if (children == null)
		{
			buildChildren();
		}
		return children;
	}

	public IModel<BrixNode> getNodeModel()
	{
		return null;
	}

	public boolean isLeaf()
	{
		return false;
	}

	public void detach()
	{
		children = null;
	}

}
