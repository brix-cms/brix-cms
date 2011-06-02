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
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.title.TitleTransformer;
import org.brixcms.markup.transform.PanelTransformer;
import org.brixcms.markup.variable.VariableTransformer;
import org.brixcms.markup.web.BrixMarkupNodePanel;
import org.brixcms.plugin.site.SitePlugin;

public class PageRenderingPanel extends BrixMarkupNodePanel {
    public PageRenderingPanel(String id, IModel<BrixNode> nodeModel) {
        super(id, nodeModel);
    }


    public MarkupSource getMarkupSource() {
        MarkupSource source = new PageMarkupSource((AbstractContainer) getModelObject());
        source = new PanelTransformer(source);
        source = new VariableTransformer(source, getModelObject());
        source = new TitleTransformer(source, (AbstractContainer) getModelObject());
        return source;
    }

    @Override
    public boolean isVisible() {
        BrixNode node = getModelObject();
        return SitePlugin.get().canViewNode(node, Context.PRESENTATION);
    }
}
