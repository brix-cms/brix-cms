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

public class MenusNode extends BrixNode implements TreeAwareNode
{

	public MenusNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}
	
	public JcrTreeNode getTreeNode(BrixNode node)
	{
		return new MenusTreeNode(node);
	}

	private static class MenusTreeNode extends AbstractJcrTreeNode
	{

		public MenusTreeNode(BrixNode node)
		{
			super(node);
		}		
	};
	
	@Override
	public boolean isFolder()
	{
		return true;
	}
	
	@Override
	public String getUserVisibleName()
	{
		return "Menus";
	}
	
	@Override
	public String getUserVisibleType()
	{
		return "";
	}
	
	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.getPath().equals(MenuPlugin.get(brix).getRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new MenusNode(node, session);
        }
    };

}
