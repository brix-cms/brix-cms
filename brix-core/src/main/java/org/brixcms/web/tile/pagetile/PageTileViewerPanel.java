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

package org.brixcms.web.tile.pagetile;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.PageRenderingPanel;
import org.brixcms.web.generic.BrixGenericPanel;

public class PageTileViewerPanel extends BrixGenericPanel<BrixNode> {
    public PageTileViewerPanel(String id, IModel<BrixNode> tileNode) {
        super(id, tileNode);
    }

    @Override
    protected void onBeforeRender() {
        if (!hasBeenRendered()) {
            init();
        }

        super.onBeforeRender();
    }

    private void init() {
        JcrNode tileNode = (JcrNode) getModelObject();

        if (checkLoop(getModel()) == true) {
            add(new Label("view", "Loop detected."));
        } else {
            BrixNode pageNode = (BrixNode) (tileNode.hasProperty("pageNode") ? tileNode.getProperty("pageNode")
                    .getNode() : null);

            if (pageNode != null) {
                add(new PageRenderingPanel("view", new BrixNodeModel(pageNode)));
            } else {
                add(new Label("view", "Page not found."));
            }
        }
    }

    private boolean checkLoop(final IModel<BrixNode> model) {
        final boolean loop[] = {false};

        visitParents(PageTileViewerPanel.class, new IVisitor<Component, BrixNode>() {
            public void component(Component component, IVisit iVisit) {
                // found parent with same model, this indicates a loop
                if (component != PageTileViewerPanel.this && component.getDefaultModel().equals(model)) {
                    loop[0] = true;
                }
            }
        });

        return loop[0];
    }
}
