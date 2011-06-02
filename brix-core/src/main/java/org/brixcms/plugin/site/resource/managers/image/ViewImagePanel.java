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

package org.brixcms.plugin.site.resource.managers.image;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.resource.ResourceNodeHandler;
import org.brixcms.web.generic.BrixGenericPanel;

public class ViewImagePanel extends BrixGenericPanel<BrixNode> {
    public ViewImagePanel(String id, IModel<BrixNode> model) {
        super(id, model);

        final ResourceBehavior behavior = new ResourceBehavior() {
            @Override
            IModel<BrixNode> getNodeModel() {
                return ViewImagePanel.this.getModel();
            }
        };

        add(new WebMarkupContainer("image") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                CharSequence url = urlFor(behavior, IBehaviorListener.INTERFACE);
                tag.put("src", url);
                super.onComponentTag(tag);
            }
        }.add(behavior));
    }

    private abstract class ResourceBehavior extends AbstractBehavior
            implements
            IBehaviorListener {
        public void onRequest() {
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceNodeHandler(getNodeModel()));
        }

        abstract IModel<BrixNode> getNodeModel();
    }

    ;
}
