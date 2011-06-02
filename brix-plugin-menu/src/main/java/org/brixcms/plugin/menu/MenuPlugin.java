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

package org.brixcms.plugin.menu;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.tile.fulltree.FullTreeMenuTile;
import org.brixcms.plugin.menu.tile.subtree.SubTreeMenuTile;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenuPlugin implements Plugin {
    private static final String ID = MenuPlugin.class.getName();
    private static String ROOT_NODE_NAME = Brix.NS_PREFIX + "menu";
    private final Brix brix;

    public static MenuPlugin get() {
        return get(Brix.get());
    }

    public static MenuPlugin get(Brix brix) {
        return (MenuPlugin) brix.getPlugin(ID);
    }

    public MenuPlugin(Brix brix) {
        this.brix = brix;

        brix.getConfig().getRegistry().register(JcrNodeWrapperFactory.POINT, MenusNode.FACTORY);
        brix.getConfig().getRegistry().register(JcrNodeWrapperFactory.POINT, MenuNode.FACTORY);
        brix.getConfig().getRegistry().register(Tile.POINT, new SubTreeMenuTile());
        brix.getConfig().getRegistry().register(Tile.POINT, new FullTreeMenuTile());
    }


    // for backwards compatibility: todo decide!
//    private static final String ID = "brix.plugin.menu.MenuPlugin";


    public String getId() {
//		System.out.print(ID);
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {

    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return false;
    }

    public List<IBrixTab> newTabs(final IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new Tab(new ResourceModel("menus", "Menus"),
                workspaceModel)};
        return Arrays.asList(tabs);
    }

    public List<BrixNode> getMenuNodes(String workspaceId) {
        BrixNode root = getRootNode(workspaceId, false);
        if (root != null) {
            List<BrixNode> result = new ArrayList<BrixNode>();
            JcrNodeIterator i = root.getNodes("menu");
            while (i.hasNext()) {
                result.add((BrixNode) i.nextNode());
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    private BrixNode getRootNode(String workspaceId, boolean createIfNotExist) {
        JcrSession session = brix.getCurrentSession(workspaceId);

        if (session.itemExists(getRootPath()) == false) {
            if (createIfNotExist) {
                BrixNode parent = (BrixNode) session.getItem(brix.getRootPath());
                parent.addNode(ROOT_NODE_NAME, "nt:unstructured");
            } else {
                return null;
            }
        }

        return (BrixNode) session.getItem(getRootPath());
    }

    public String getRootPath() {
        return brix.getRootPath() + "/" + ROOT_NODE_NAME;
    }

    public BrixNode saveMenu(Menu menu, String workspaceId, BrixNode node) {
        if (node != null) {
            menu.save(node);
        } else {
            BrixNode root = getRootNode(workspaceId, true);
            node = (BrixNode) root.addNode("menu");
            menu.save(node);
        }
        node.getSession().save();
        return node;
    }

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 100);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new ManageMenuPanel(panelId, workspaceModel);
        }
    }
}
