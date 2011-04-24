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

package org.brixcms.plugin.site.fallback;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.EmptyRequestTarget;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.NodeConverter;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SiteNodePlugin;

public class FallbackNodePlugin implements SiteNodePlugin {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SiteNodePlugin ---------------------


    public String getNodeType() {
        return null;
    }

    public String getName() {
        return "Unknown";
    }

    public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters) {
        return EmptyRequestTarget.getInstance();
    }

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode) {
        return null;
    }

    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack) {
        return new EmptyPanel(id);
    }

    public NodeConverter getConverterForNode(BrixNode node) {
        return null;
    }

// -------------------------- INNER CLASSES --------------------------

    private class EmptyPanel extends Panel {
        public EmptyPanel(String id) {
            super(id);
        }
    }

    ;
}
