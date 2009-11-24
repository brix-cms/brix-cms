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

package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupSource;
import brix.markup.title.TitleTransformer;
import brix.markup.transform.PanelTransformer;
import brix.markup.variable.VariableTransformer;
import brix.markup.web.BrixMarkupNodePanel;
import brix.plugin.site.SitePlugin;

public class PageRenderingPanel extends BrixMarkupNodePanel
{
    public PageRenderingPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }

    @Override
    public boolean isVisible()
    {
        BrixNode node = getModelObject();
        return SitePlugin.get().canViewNode(node, Context.PRESENTATION);
    }

    public MarkupSource getMarkupSource()
    {
        MarkupSource source = new PageMarkupSource((AbstractContainer)getModelObject());
        source = new PanelTransformer(source);
        source = new VariableTransformer(source, getModelObject());
        source = new TitleTransformer(source, (AbstractContainer) getModelObject());
        return source;
    }

}
