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

package org.brixcms.plugin.prototype;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Objects;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.Plugin;
import org.brixcms.jcr.JcrUtil;
import org.brixcms.jcr.JcrUtil.ParentLimiter;
import org.brixcms.jcr.JcrUtil.TargetRootNodeProvider;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.global.GlobalContainerNode;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrototypePlugin implements Plugin {
    private static final String ID = PrototypePlugin.class.getName();

    private static final String WORKSPACE_TYPE = "brix:prototype";

    private static final String WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME = "brix:prototype-name";

    private final Brix brix;

    public static PrototypePlugin get() {
        return get(Brix.get());
    }

    public static PrototypePlugin get(Brix brix) {
        return (PrototypePlugin) brix.getPlugin(ID);
    }

    public PrototypePlugin(Brix brix) {
        this.brix = brix;
    }


    public String getId() {
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return "Prototype " + getPrototypeName(workspace);
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        if (isFrontend) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
            return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        } else {
            return null;
        }
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {

    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return isPrototypeWorkspace(workspace);
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new Tab(new ResourceModel("prototypes", "Prototypes"),
                workspaceModel)};
        return Arrays.asList(tabs);
    }

    public void createPrototype(Workspace originalWorkspace, String prototypeName) {
        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setPrototypeName(workspace, prototypeName);

        JcrSession originalSession = brix.getCurrentSession(originalWorkspace.getId());
        JcrSession destSession = brix.getCurrentSession(workspace.getId());
        brix.clone(originalSession, destSession);
    }

    public void setPrototypeName(Workspace workspace, String name) {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME, name);
    }

    public void createPrototype(List<JcrNode> nodes, String prototypeName) {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("Node list can not be empty.");
        }
        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setPrototypeName(workspace, prototypeName);

        JcrSession destSession = brix.getCurrentSession(workspace.getId());

        JcrUtil.cloneNodes(nodes, destSession.getRootNode());
        destSession.save();
    }

    public String getPrototypeName(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME);
    }

    public List<Workspace> getPrototypes() {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
    }

    public boolean isPrototypeWorkspace(Workspace workspace) {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public boolean prototypeExists(String protypeName) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        attributes.put(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME, protypeName);
        return !brix.getWorkspaceManager().getWorkspacesFiltered(attributes).isEmpty();
    }

    public void restoreNodes(List<JcrNode> nodes, final JcrNode targetRootNode) {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("List 'nodes' must contain at least one node.");
        }

        ParentLimiter limiter = null;

        // targetRootNode is only applicable for regular Site nodes (not even
        // global container)

        final String siteRoot = SitePlugin.get().getSiteRootPath();

        if (targetRootNode.getDepth() > 0) {
            final String commonParent = getCommonParentPath(nodes);
            limiter = new ParentLimiter() {
                public boolean isFinalParent(JcrNode node, JcrNode parent) {
                    if (node.getPath().startsWith(siteRoot) && node instanceof GlobalContainerNode == false) {
                        return parent.getPath().equals(commonParent);
                    } else {
                        return parent.getDepth() == 0;
                    }
                }
            };
        }

        TargetRootNodeProvider provider = new TargetRootNodeProvider() {
            public JcrNode getTargetRootNode(JcrNode node) {
                if (node.getPath().startsWith(siteRoot) && node instanceof GlobalContainerNode == false) {
                    return targetRootNode;
                } else {
                    return targetRootNode.getSession().getRootNode();
                }
            }
        };

        JcrUtil.cloneNodes(nodes, provider, limiter);
        targetRootNode.getSession().save();
    }

    private String getCommonParentPath(List<JcrNode> nodes) {
        Path current = null;
        String sitePath = SitePlugin.get().getSiteRootPath();
        for (JcrNode node : nodes) {
            if (node.getPath().startsWith(sitePath) && node instanceof GlobalContainerNode == false) {
                if (current == null) {
                    current = new Path(node.getPath()).parent();
                } else {
                    Path another = new Path(node.getPath()).parent();

                    Path common = Path.ROOT;

                    Iterator<String> i1 = current.iterator();
                    Iterator<String> i2 = another.iterator();
                    while (i1.hasNext() && i2.hasNext()) {
                        String s1 = i1.next();
                        String s2 = i2.next();
                        if (Objects.equal(s1, s2)) {
                            common = common.append(new Path(s1));
                        } else {
                            break;
                        }
                    }

                    current = common;
                }
            }
        }

        if (current == null) {
            current = Path.ROOT;
        }

        return current.toString();
    }

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 50);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new ManagePrototypesPanel(panelId, workspaceModel);
        }
    }
}
