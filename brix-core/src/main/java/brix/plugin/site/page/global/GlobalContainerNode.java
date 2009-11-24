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

package brix.plugin.site.page.global;

import javax.jcr.Node;
import javax.jcr.Session;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.AbstractContainer;

public class GlobalContainerNode extends AbstractContainer
{
	public static final String TYPE = Brix.NS_PREFIX + "globalContainer";
	
    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {
        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new GlobalContainerNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session)
        {
            RepositoryUtil.registerNodeType(session.getWorkspace(), TYPE,
                false, false, true);
        }		
    };

    public GlobalContainerNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static GlobalContainerNode initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TYPE);

        return new GlobalContainerNode(node.getDelegate(), node.getSession());
    }
    
    @Override
    public String getUserVisibleName()
    {
    	return "Global Tiles and Variables";
    }
}
