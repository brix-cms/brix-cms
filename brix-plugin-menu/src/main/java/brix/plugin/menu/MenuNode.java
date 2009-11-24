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

package brix.plugin.menu;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.picker.common.TreeAwareNode;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.JcrTreeNode;

public class MenuNode extends BrixNode implements TreeAwareNode
{

	public MenuNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	@Override
	public String getUserVisibleName()
	{
		if (hasProperty("name"))
		{
			return getProperty("name").getString();
		}
		else
		{
			return super.getUserVisibleName();
		}
	}
	
	@Override
	public String getUserVisibleType()
	{
		return "Menu";
	}
	
	public JcrTreeNode getTreeNode(BrixNode node)
	{
		return new MenuTreeNode(node);
	}

	private static class MenuTreeNode extends AbstractJcrTreeNode
	{

		public MenuTreeNode(BrixNode node)
		{
			super(node);
		}		
	};
	
	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.getDepth() > 1 && node.getParent().getPath().equals(MenuPlugin.get(brix).getRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new MenuNode(node, session);
        }
    };

}
