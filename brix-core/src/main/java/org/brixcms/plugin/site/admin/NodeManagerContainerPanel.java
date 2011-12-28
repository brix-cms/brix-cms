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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.jcr.ItemNotFoundException;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.markup.html.tree.LinkType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.JcrUtil;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.NodeTreeRenderer;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SiteNodePlugin;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.folder.FolderNode;
import org.brixcms.plugin.site.tree.SiteNodeFilter;
import org.brixcms.web.picker.common.TreeAwareNode;
import org.brixcms.web.tree.AbstractTreeModel;
import org.brixcms.web.tree.JcrTreeNode;
import org.brixcms.web.util.AbstractModel;
import org.brixcms.workspace.Workspace;

public class NodeManagerContainerPanel extends NodeManagerPanel implements NodeTreeContainer {
    private static MetaDataKey<String> EDITOR_NODE_TYPE = new MetaDataKey<String>() {
    };

    private static final SiteNodeFilter NODE_FILTER = new SiteNodeFilter(false, null);

    private static final String EDITOR_ID = "editor";

    private Component editor;

    private final BaseTree tree;
    private IModel<Workspace> workspaceModel;

    // used to detect whether workspace was changed between the requests (node
    // needs to be updated)
    // or node has been changed (workspace needs to be updated)
    private String oldWorkspaceId;

    private Component lastEditor;

    private IModel<List<SiteNodePluginEntry>> createNodesModel = new LoadableDetachableModel<List<SiteNodePluginEntry>>() {
        @Override
        protected List<SiteNodePluginEntry> load() {
            List<SiteNodePluginEntry> result = new ArrayList<SiteNodePluginEntry>();
            for (SiteNodePlugin plugin : SitePlugin.get().getNodePlugins()) {
                IModel<BrixNode> parent = getNewNodeParent();
                if (plugin.newCreateNodeCaptionModel(parent) != null) {
                    result.add(new SiteNodePluginEntry(plugin));
                }
            }
            return result;
        }
    };

    public NodeManagerContainerPanel(String id, IModel<Workspace> workspaceModel) {
        super(id, new BrixNodeModel(getRootNode(workspaceModel)));
        this.workspaceModel = workspaceModel;

        editor = new WebMarkupContainer(EDITOR_ID);
        add(editor);

        setupDefaultEditor();

        add(tree = new Tree("tree", new TreeModel()));

        WebMarkupContainer createNodesContainer = new WebMarkupContainer("createNodesContainer") {
            @Override
            public boolean isVisible() {
                BrixNode folderNode = getNewNodeParent().getObject();
                return SitePlugin.get().canAddNodeChild(folderNode, Context.ADMINISTRATION);
            }
        };
        add(createNodesContainer);

        createNodesContainer.add(new ListView<SiteNodePluginEntry>("createNodes", createNodesModel) {
            @Override
            protected void populateItem(final ListItem<SiteNodePluginEntry> item) {
                Link<Void> link;
                item.add(link = new Link<Void>("link") {
                    @Override
                    public void onClick() {
                        SiteNodePlugin plugin = item.getModelObject().getPlugin();
                        final Component currentEditor = getEditor();

                        // remember the last editor that is not a create node
                        // panel
                        if (lastEditor == null ||
                                currentEditor.getMetaData(EDITOR_NODE_TYPE) == null) {
                            lastEditor = currentEditor;
                        }
                        SimpleCallback goBack = new SimpleCallback() {
                            public void execute() {
                                setupEditor(lastEditor);
                            }
                        };
                        Panel panel = plugin.newCreateNodePanel(EDITOR_ID, getNewNodeParent(),
                                goBack);
                        panel.setMetaData(EDITOR_NODE_TYPE, plugin.getNodeType());
                        setupEditor(panel);
                    }

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        SiteNodePlugin plugin = item.getModelObject().getPlugin();
                        String editorNodeType = getEditor().getMetaData(EDITOR_NODE_TYPE);
                        if (plugin.getNodeType().equals(editorNodeType)) {
                            CharSequence klass = tag.getString("class");
                            if (klass == null) {
                                klass = "selected";
                            } else {
                                klass = klass + " selected";
                            }
                            tag.put("class", klass);
                        }
                    }
                });
                item.add(new WebMarkupContainer("separator") {
                    @Override
                    public boolean isVisible() {
                        return item.getIndex() != createNodesModel.getObject().size() - 1;
                    }
                });
                IModel<BrixNode> parent = getNewNodeParent();
                SiteNodePlugin plugin = item.getModelObject().getPlugin();
                link.add(new Label("label", plugin.newCreateNodeCaptionModel(parent)));
            }

        }.setReuseItems(false));
    }


    public void updateTree() {
        tree.invalidateAll();
        // tree.updateTree();
    }

    private Component getEditor() {
        return get(EDITOR_ID);
    }

    private IModel<BrixNode> getNewNodeParent() {
        BrixNode current = getModelObject();
        if (current instanceof FolderNode) {
            return getModel();
        } else {
            return new BrixNodeModel((BrixNode) current.getParent());
        }
    }

    private JcrTreeNode getTreeNode(BrixNode node) {
        return TreeAwareNode.Util.getTreeNode(node, NODE_FILTER);
    }

    @Override
    protected void onBeforeRender() {
        Workspace workspace = workspaceModel.getObject();

        BrixNode node;
        try {
            node = getModelObject();
        }
        catch (JcrException e) {
            if (e.getCause() instanceof ItemNotFoundException) {
                node = SitePlugin.get().getSiteRootNode(workspace.getId());
                getModel().setObject(null);
                selectNode(node);
                setupDefaultEditor();
                tree.invalidateAll();
            } else {
                throw (e);
            }
        }

        String nodeWorkspaceName = node.getSession().getWorkspace().getName();
        if (!nodeWorkspaceName.equals(workspace.getId())) {
            // we have to either update node or workspace
            if (oldWorkspaceId != null && workspace.getId().equals(oldWorkspaceId)) {
                // the node changed, need to update the workspace
                Workspace newWorkspace = node.getBrix().getWorkspaceManager().getWorkspace(
                        nodeWorkspaceName);
                workspaceModel.setObject(newWorkspace);
            } else {
                // the workspace has changed, update the node
                // 1 try to get node with same UUID, 2 try to get node with same
                // path, 3 get root node
                JcrSession newSession = node.getBrix().getCurrentSession(workspace.getId());
                String uuid = node.getIdentifier();
                BrixNode newNode = JcrUtil.getNodeByUUID(newSession, uuid);
                if (newNode == null) {
                    String path = node.getPath();
                    if (newSession.getRootNode().hasNode(path.substring(1))) {
                        newNode = (BrixNode) newSession.getItem(path);
                    }
                }
                if (newNode == null) {
                    newNode = getRootNode(workspaceModel);
                }
                selectNode(newNode);
                tree.invalidateAll();
                tree.getTreeState().expandNode(((TreeModel) tree.getDefaultModelObject()).getRoot());
            }
        }

        super.onBeforeRender();

        oldWorkspaceId = workspace.getId();
    }

    private void setupDefaultEditor() {
        setupEditor(new NodeManagerEditorPanel(EDITOR_ID, getModel()));
    }

    private void setupEditor(Component newEditor) {
        editor.replaceWith(newEditor);
        editor = newEditor;
    }

    private static BrixNode getRootNode(IModel<Workspace> workspaceModel) {
        Workspace workspace = workspaceModel.getObject();
        if (workspace != null) {
            BrixNode root = (BrixNode) Brix.get().getCurrentSession(workspace.getId()).getItem(
                    SitePlugin.get().getSiteRootPath());
            return root;
        } else {
            return null;
        }
    }

    public void selectNode(BrixNode node) {
        tree.getTreeState().selectNode(getTreeNode(node), true);
    }

    private static class SiteNodePluginEntry implements Serializable {
        private final String nodeType;

        public SiteNodePluginEntry(SiteNodePlugin plugin) {
            this.nodeType = plugin.getNodeType();
        }

        public SiteNodePlugin getPlugin() {
            return SitePlugin.get().getNodePluginForType(nodeType);
        }
    }

    private class Tree extends LinkTree {
        public Tree(String id, TreeModel model) {
            super(id, model);
            setLinkType(LinkType.REGULAR);
            getTreeState().expandNode(model.getRoot());
        }

        @Override
        protected Component newNodeComponent(String id, IModel<Object> model) {
            JcrTreeNode node = (JcrTreeNode) model.getObject();
            BrixNode n = node.getNodeModel().getObject();
            Collection<NodeTreeRenderer> renderers = n.getBrix().getConfig().getRegistry().lookupCollection(NodeTreeRenderer.POINT);
            for (NodeTreeRenderer renderer : renderers) {
                Component component = renderer.newNodeComponent(id, Tree.this, model);
                if (component != null) {
                    return component;
                }
            }
            return super.newNodeComponent(id, model);
        }

        @Override
        protected Component newJunctionLink(MarkupContainer parent, String id, Object node) {
            LinkType old = getLinkType();
            setLinkType(LinkType.AJAX);
            Component c = super.newJunctionLink(parent, id, node);
            setLinkType(old);
            return c;
        }

        @Override
        protected IModel getNodeTextModel(final IModel nodeModel) {
            return new AbstractModel<String>() {
                @Override
                public String getObject() {
                    JcrTreeNode node = (JcrTreeNode) nodeModel.getObject();
                    BrixNode n = node.getNodeModel().getObject();
                    return n.getUserVisibleName();
                }
            };
        }

        @Override
        protected ITreeState newTreeState() {
            return new TreeState();
        }
    }

    private class TreeState extends DefaultTreeState {
        @Override
        public void selectNode(Object node, boolean selected) {
            if (selected) {
                JcrTreeNode n = (JcrTreeNode) node;
                NodeManagerContainerPanel.this.setModel(n.getNodeModel());
                setupDefaultEditor();
                expandParents(n.getNodeModel().getObject());
            }
        }

        private void expandParents(BrixNode node) {
            BrixNode parent = (BrixNode) node.getParent();
            while (parent.getDepth() > 0) {
                expandNode(getTreeNode(parent));
                parent = (BrixNode) parent.getParent();
            }
        }

        @Override
        public boolean isNodeSelected(Object node) {
            JcrTreeNode n = (JcrTreeNode) node;
            IModel<BrixNode> model = n.getNodeModel();
            return model != null && model.equals(NodeManagerContainerPanel.this.getModel());
        }

        @Override
        public Collection<Object> getSelectedNodes() {
            JcrTreeNode node = getTreeNode(getModelObject());
            return Arrays.asList(new Object[]{node});
        }
    }

    private class TreeModel extends AbstractTreeModel {
        @Override
        public javax.swing.tree.TreeNode getRoot() {
            Workspace workspace = workspaceModel.getObject();
            return getTreeNode(SitePlugin.get().getSiteRootNode(workspace.getId()));
        }
    }
}
