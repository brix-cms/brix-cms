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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.NodeConverter;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.admin.CreatePageOrTemplatePanel;

public class PageSiteNodePlugin extends AbstractSitePagePlugin {
    public static final String TYPE = Brix.NS_PREFIX + "tilePage";

    public PageSiteNodePlugin(SitePlugin plugin) {
        super(plugin);
    }



    @Override
    public NodeConverter getConverterForNode(BrixNode node) {
        if (TemplateSiteNodePlugin.TYPE.equals(((BrixNode) node).getNodeType())) {
            return new FromTemplateConverter(getNodeType());
        } else {
            return super.getConverterForNode(node);
        }
    }

    public String getName() {
        return (new ResourceModel("page", "Page")).getObject();
    }

    @Override
    public String getNodeType() {
        return TYPE;
    }

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
        return new ResourceModel("newPage", "Create New Page");
    }

    @Override
    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
        return new CreatePageOrTemplatePanel(id, parentNode, getNodeType(), goBack);
    }

    private static class FromTemplateConverter extends SetTypeConverter {
        public FromTemplateConverter(String type) {
            super(type);
        }
    }
}
