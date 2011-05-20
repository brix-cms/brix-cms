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

package org.brixcms.plugin.site.admin;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.Path;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.web.util.PathLabel;

import javax.jcr.ReferentialIntegrityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NodeManagerEditorPanel extends BrixGenericPanel<BrixNode> {
// --------------------------- CONSTRUCTORS ---------------------------

    public NodeManagerEditorPanel(String id, IModel<BrixNode> model) {
        super(id, model);

        String root = SitePlugin.get().getSiteRootPath();
        add(new PathLabel("path2", model, root) {
            @Override
            protected void onPathClicked(Path path) {
                BrixNode node = (BrixNode) getNode().getSession().getItem(path.toString());
                selectNode(node, false);
            }
        });

        add(new Link<Void>("rename") {
            @Override
            public void onClick() {
                String id = NodeManagerEditorPanel.this.getId();
                Panel renamePanel = new RenamePanel(id, NodeManagerEditorPanel.this.getModel()) {
                    @Override
                    protected void onLeave() {
                        SitePlugin.get().refreshNavigationTree(this);
                        replaceWith(NodeManagerEditorPanel.this);
                    }
                };
                NodeManagerEditorPanel.this.replaceWith(renamePanel);
            }

            @Override
            public boolean isVisible() {
                BrixNode node = NodeManagerEditorPanel.this.getModelObject();
                String path = node.getPath();
                String web = SitePlugin.get().getSiteRootPath();
                return SitePlugin.get().canRenameNode(node, Context.ADMINISTRATION) &&
                        path.length() > web.length() && path.startsWith(web);
            }
        });

        add(new Link<Void>("makeVersionable") {
            @Override
            public void onClick() {
                if (!getNode().isNodeType("mix:versionable")) {
                    getNode().addMixin("mix:versionable");
                    getNode().save();
                    getNode().checkin();
                }
            }

            @Override
            public boolean isVisible() {
                if (true) {
                    // TODO: Implement proper versioning support!
                    return false;
                }

                return getNode() != null && getNode().isNodeType("nt:file") &&
                        !getNode().isNodeType("mix:versionable") &&
                        SitePlugin.get().canEditNode(getNode(), Context.ADMINISTRATION);
            }
        });

        add(new Link<Void>("delete") {
            @Override
            public void onClick() {
                BrixNode node = getNode();
                BrixNode parent = (BrixNode) node.getParent();

                node.remove();
                try {
                    parent.save();
                    selectNode(parent, true);
                } catch (JcrException e) {
                    if (e.getCause() instanceof ReferentialIntegrityException) {
                        parent.getSession().refresh(false);
                        NodeManagerEditorPanel.this.getModel().detach();
                        // parent.refresh(false);
                        selectNode(NodeManagerEditorPanel.this.getModelObject(), true);
                        getSession().error(
                                NodeManagerEditorPanel.this.getString("referenceIntegrityError"));
                    } else {
                        throw e;
                    }
                }
            }

            @Override
            public boolean isVisible() {
                BrixNode node = NodeManagerEditorPanel.this.getModelObject();
                String path = node.getPath();
                String web = SitePlugin.get().getSiteRootPath();

                return SitePlugin.get().canDeleteNode(getNode(), Context.ADMINISTRATION) &&
                        path.length() > web.length() && path.startsWith(web);
            }
        });

        add(new SessionFeedbackPanel("sessionFeedback"));

        add(new NodeManagerTabbedPanel("tabbedPanel", getTabs(getModel())));
    }

    private void selectNode(BrixNode node, boolean refresh) {
        SitePlugin.get().selectNode(this, node, refresh);
    }

    public BrixNode getNode() {
        return getModelObject();
    }

    private List<IBrixTab> getTabs(IModel<BrixNode> nodeModel) {
        BrixNode node = nodeModel.getObject();

        final Collection<ManageNodeTabFactory> factories;
        if (node != null) {
            factories = node.getBrix().getConfig().getRegistry().lookupCollection(
                    ManageNodeTabFactory.POINT);
        } else {
            factories = Collections.emptyList();
        }

        if (factories != null && !factories.isEmpty()) {
            List<IBrixTab> result = new ArrayList<IBrixTab>();
            for (ManageNodeTabFactory f : factories) {
                List<IBrixTab> tabs = f.getManageNodeTabs(nodeModel);
                if (tabs != null)
                    result.addAll(tabs);
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

// -------------------------- INNER CLASSES --------------------------

    private static class SessionFeedbackPanel extends FeedbackPanel {
        public SessionFeedbackPanel(String id) {
            super(id, new Filter());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isVisible() {
            List messages = (List) getFeedbackMessagesModel().getObject();
            return messages != null && !messages.isEmpty();
        }

        private static class Filter implements IFeedbackMessageFilter {
            public boolean accept(FeedbackMessage message) {
                return message.getReporter() == null;
            }
        }

        ;
    }

    ;
}
