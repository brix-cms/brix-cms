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

package brix.web.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import brix.jcr.wrapper.BrixNode;

public class FilteredJcrTreeNode implements JcrTreeNode
{
	private final JcrTreeNode delegate;
	private final NodeFilter visibilityFilter;
	
	public FilteredJcrTreeNode(final JcrTreeNode delegate, final NodeFilter visibilityFilter)
	{
		this.delegate = delegate;
		this.visibilityFilter = visibilityFilter;
		
		if (delegate == null)
		{
			throw new IllegalArgumentException("Argument 'delegate' may not be null.");
		}
	}

	private List<JcrTreeNode> children = null;
	
	private void buildChildren()
	{
		final List<? extends JcrTreeNode> original = delegate.getChildren();
		if (original == null)
		{
			children = Collections.emptyList();
		}
		else if (visibilityFilter != null)
		{
			children = new ArrayList<JcrTreeNode>(original.size());
			for (JcrTreeNode node : original)
			{
				BrixNode n = node.getNodeModel() != null ? node.getNodeModel().getObject() : null;
				if (visibilityFilter.isNodeAllowed(n))
				{
					children.add(new FilteredJcrTreeNode(node, visibilityFilter));
				}
			}
		}	
		else
		{
			children = new ArrayList<JcrTreeNode>(original);
		}
	};
	
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
		return delegate.getNodeModel();	
	}

	public boolean isLeaf()
	{
		return delegate.isLeaf();
	}

	@Override
	public int hashCode()
	{
		return delegate.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof JcrTreeNode == false)
		{
			return false;			
		}
		
		JcrTreeNode that;
		if (obj instanceof FilteredJcrTreeNode)
		{
			that = ((FilteredJcrTreeNode)obj).delegate;
		}
		else
		{
			that = (JcrTreeNode)obj;
		}
		return Objects.equal(delegate, that);
	}
	
	@Override
	public String toString()
	{
		return delegate.toString();
	}
	
	public void detach()
	{
		children = null;
		delegate.detach();
	}

}
