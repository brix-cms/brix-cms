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

package org.brixcms.plugin.site.admin.nodetree;

import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.PageNode;

/**
 * Renderer for template nodes
 *
 * @author Jeremy Thomerson
 */
public class TemplateNodeTreeRenderer extends AbstractNodeTreeRenderer {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference RESOURCE = new SharedResourceReference(
            TemplateNodeTreeRenderer.class, "resources/text-x-generic-template.png");

    @Override
    protected ResourceReference getImageResourceReference(BaseTree tree, Object node) {
        return RESOURCE;
    }

    @Override
    protected Class<? extends BrixNode> getNodeClass() {
        return PageNode.class;
    }
}
