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

import java.util.Collection;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.NodeConverter;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SiteNodePlugin;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.admin.ManageTileNodeTabFactory;
import org.brixcms.plugin.site.resource.ResourceNodePlugin;
import org.brixcms.web.nodepage.BrixNodePageUrlMapper;
import org.brixcms.web.nodepage.BrixNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;

public abstract class AbstractSitePagePlugin implements SiteNodePlugin {
    private final BrixNodePageUrlMapper urlMapper = new BrixNodePageUrlMapper() {
        @Override
        protected BrixNodeWebPage newPageInstance(IModel<BrixNode> nodeModel,
                                                  BrixPageParameters pageParameters) {
            return new PageRenderingPage(nodeModel, pageParameters);
        }
    };

    public AbstractSitePagePlugin(SitePlugin sitePlugin) {
        registerManageNodeTabFactory(sitePlugin);
    }

    private void registerManageNodeTabFactory(SitePlugin sitePlugin) {
        Collection<ManageNodeTabFactory> factories = sitePlugin.getBrix().getConfig().getRegistry()
                .lookupCollection(ManageNodeTabFactory.POINT);
        boolean found = false;
        for (ManageNodeTabFactory f : factories) {
            if (f instanceof ManageTileNodeTabFactory) {
                found = true;
                break;
            }
        }
        if (!found) {
            sitePlugin.registerManageNodeTabFactory(new ManageTileNodeTabFactory());
        }
    }


    public abstract String getNodeType();


    public IRequestHandler respond(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters) {
        return urlMapper.decode(pageParameters, nodeModel);
    }

    public abstract Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode,
                                             SimpleCallback goBack);

    public NodeConverter getConverterForNode(BrixNode node) {
        if (node instanceof BrixFileNode) {
            BrixFileNode fileNode = (BrixFileNode) node;
            if (ResourceNodePlugin.TYPE.equals(fileNode.getNodeType())) {
                String mimeType = fileNode.getMimeType();
                if (mimeType != null &&
                        (mimeType.startsWith("text/") || mimeType.equals("application/xml"))) {
                    return new FromResourceConverter(getNodeType());
                }
            }
        }

        return null;
    }

    private static class FromResourceConverter extends SetTypeConverter {
        public FromResourceConverter(String type) {
            super(type);
        }
    }

    protected static class SetTypeConverter implements NodeConverter {
        private final String type;

        public SetTypeConverter(String type) {
            this.type = type;
        }

        public void convert(BrixNode node) {
            ((BrixNode) node).setNodeType(type);
        }
    }
}
