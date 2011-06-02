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

package org.brixcms.plugin.site.page;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;

import javax.jcr.Node;
import javax.jcr.Session;

public class TemplateNode extends AbstractContainer {
    public static final String CONTENT_TAG = Brix.NS_PREFIX + "content";
    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new TemplateNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session) {
            RepositoryUtil.registerNodeType(session.getWorkspace(), TemplateSiteNodePlugin.TYPE, false, false, true);
        }
    };

    public static boolean canHandle(JcrNode node) {
        return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static TemplateNode initialize(JcrNode node) {
        BrixNode brixNode = (BrixNode) node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TemplateSiteNodePlugin.TYPE);

        return new TemplateNode(node.getDelegate(), node.getSession());
    }

    public TemplateNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public String getUserVisibleType() {
        return "Template";
    }
}
