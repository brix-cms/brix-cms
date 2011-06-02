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

package org.brixcms.plugin.site;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.folder.FolderNode;

import javax.jcr.Node;

/**
 * Node that can wrap the brix:root/brix:site node
 *
 * @author Matej Knopp
 */
public class SiteRootNode extends FolderNode {
    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return node.getPath().equals(SitePlugin.get(brix).getSiteRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new SiteRootNode(node, session);
        }
    };

    public SiteRootNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public String getUserVisibleName() {
        return "Site";
    }
}
