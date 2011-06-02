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

import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.title.TitleTransformer;
import org.brixcms.markup.transform.HeadTransformer;
import org.brixcms.markup.variable.VariableTransformer;
import org.brixcms.markup.web.BrixMarkupNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.toolbar.ToolbarBehavior;

public class PageRenderingPage extends BrixMarkupNodeWebPage {
    public static MarkupSource transform(MarkupSource source, AbstractContainer container) {
        source = new HeadTransformer(source);
        source = new VariableTransformer(source, container);
        source = new TitleTransformer(source, container);
        return source;
    }

    public PageRenderingPage(final IModel<BrixNode> node, BrixPageParameters pageParameters) {
        super(node, pageParameters);
        //  add(new TilePageRenderPanel("view", node, this));
        add(new ToolbarBehavior() {
            @Override
            protected String getCurrentWorkspaceId() {
                return node.getObject().getSession().getWorkspace().getName();
            }
        });
    }


    public MarkupSource getMarkupSource() {
        MarkupSource source = new PageMarkupSource((AbstractContainer) getModelObject());
        return transform(source, (AbstractContainer) getModelObject());
    }
}
