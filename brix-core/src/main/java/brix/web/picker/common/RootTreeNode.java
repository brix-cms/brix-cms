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
