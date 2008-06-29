package brix.plugin.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.Plugin;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.tab.AbstractWorkspaceTab;
import brix.workspace.Workspace;

public class MenuPlugin implements Plugin
{
    private final Brix brix;

    public MenuPlugin(Brix brix)
    {
        this.brix = brix;
        
        brix.getConfig().getRegistry().register(JcrNodeWrapperFactory.POINT, MenusNode.FACTORY);
        brix.getConfig().getRegistry().register(JcrNodeWrapperFactory.POINT, MenuNode.FACTORY);
    }

    private static final String ID = MenuPlugin.class.getName();

    public String getId()
    {
        return ID;
    }

    public static MenuPlugin get(Brix brix)
    {
        return (MenuPlugin)brix.getPlugin(ID);
    }

    public static MenuPlugin get()
    {
        return get(Brix.get());
    }
    
    public List<ITab> newTabs(final IModel<Workspace> workspaceModel)
    {
    	ITab tabs[] = new ITab[] {
    			new Tab(new Model<String>("Menus"), workspaceModel)
    	};
    	return Arrays.asList(tabs);
    }
    
	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
		{
			super(title, workspaceModel);
		}

		@Override
		public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new ManageMenuPanel(panelId, workspaceModel);
		}
	};

    private static String ROOT_NODE_NAME = Brix.NS_PREFIX + "menu";

    public String getRootPath()
    {
        return brix.getRootPath() + "/" + ROOT_NODE_NAME;
    }

    private BrixNode getRootNode(String workspaceId, boolean createIfNotExist)
    {
        JcrSession session = brix.getCurrentSession(workspaceId);

        if (session.itemExists(getRootPath()) == false)
        {
            if (createIfNotExist)
            {
                BrixNode parent = (BrixNode)session.getItem(brix.getRootPath());
                parent.addNode(ROOT_NODE_NAME, "nt:unstructured");
            }
            else
            {
                return null;
            }
        }

        return (BrixNode)session.getItem(getRootPath());
    }

    public List<BrixNode> getMenuNodes(String workspaceId)
    {
        BrixNode root = getRootNode(workspaceId, false);
        if (root != null)
        {
            List<BrixNode> result = new ArrayList<BrixNode>();
            JcrNodeIterator i = root.getNodes("menu");
            while (i.hasNext())
            {
                result.add((BrixNode)i.nextNode());
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public BrixNode saveMenu(Menu menu, String workspaceId, BrixNode node)
    {
        if (node != null)
        {
            menu.save(node);
        }
        else
        {
            BrixNode root = getRootNode(workspaceId, true);
            node = (BrixNode)root.addNode("menu");
            menu.save(node);
        }
        node.getSession().save();
        return node;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {

    }

    public boolean isPluginWorkspace(Workspace workspace)
    {
    	return false;
    }
    
    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

}
