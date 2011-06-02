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

package org.brixcms.plugin.menu;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractJcrTreeNode;
import org.brixcms.web.tree.JcrTreeNode;

import javax.jcr.Node;

public class MenusNode extends BrixNode implements TreeAwareNode {
    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return node.getPath().equals(MenuPlugin.get(brix).getRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new MenusNode(node, session);
        }
    };

    public MenusNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }


    public JcrTreeNode getTreeNode(BrixNode node) {
        return new MenusTreeNode(node);
    }

    @Override
    public String getUserVisibleName() {
        return "Menus";
    }

    @Override
    public String getUserVisibleType() {
        return "";
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    private static class MenusTreeNode extends AbstractJcrTreeNode {
        public MenusTreeNode(BrixNode node) {
            super(node);
        }
    }
}
