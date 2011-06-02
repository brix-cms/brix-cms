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

package org.brixcms.plugin.site.admin.convert;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.auth.Action;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SiteNodePlugin;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.plugin.site.auth.ConvertNodeAction;
import org.brixcms.web.util.TextLink;

import java.util.Collection;

public class ConvertNodePanel extends NodeManagerPanel {
    public ConvertNodePanel(String id, IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        RepeatingView converters = new RepeatingView("converters");
        add(converters);

        BrixNode node = getModelObject();
        Collection<SiteNodePlugin> plugins = SitePlugin.get().getNodePlugins();

        boolean found = false;

        for (SiteNodePlugin plugin : plugins) {
            if (plugin.getConverterForNode(node) != null) {
                Action action = new ConvertNodeAction(Action.Context.ADMINISTRATION, node,
                        plugin.getNodeType());

                if (node.getBrix().getAuthorizationStrategy().isActionAuthorized(action)) {
                    found = true;

                    WebMarkupContainer item = new WebMarkupContainer(converters.newChildId());
                    converters.add(item);

                    Model<String> typeName = new Model<String>(plugin.getNodeType());
                    item.add(new TextLink<String>("convert", typeName, new Model<String>(plugin.getName())) {
                        @Override
                        public void onClick() {
                            final String type = (String) getModelObject();
                            convertToType(type);
                            getSession().info(getString("nodeConverted"));
                        }
                    });
                }
            }
        }

        setVisible(found);
    }

    private void convertToType(String type) {
        final BrixNode node = getModelObject();

        node.checkout();
        SitePlugin.get().getNodePluginForType(type).getConverterForNode(getModelObject()).convert(node);
        node.save();
        node.checkin();

        getModel().detach();

        SitePlugin.get().selectNode(this, getModelObject());
    }
}
