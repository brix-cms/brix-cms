package brix.plugin.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.swing.tree.TreeNode;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.Strings;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.Plugin;
import brix.jcr.JcrUtil;
import brix.jcr.JcrUtil.ParentLimiter;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.nodepage.toolbar.WorkspaceListProvider;

public class TemplatePlugin implements Plugin, WorkspaceListProvider
{

    private static final String ID = TemplatePlugin.class.getName();

    public String getId()
    {
        return ID;
    }

    public static TemplatePlugin get(Brix brix)
    {
        return (TemplatePlugin)brix.getPlugin(ID);
    }

    public static TemplatePlugin get()
    {
        return get(BrixRequestCycle.Locator.getBrix());
    }

    public static final String PREFIX = "template";

    public List<String> getTemplates()
    {
        List<String> workspaces = BrixRequestCycle.Locator.getBrix()
            .getAvailableWorkspacesFiltered(PREFIX, null, null);
        List<String> res = new ArrayList<String>(workspaces.size());
        for (String s : workspaces)
        {
            res.add(BrixRequestCycle.Locator.getBrix().getWorkspaceResolver().getWorkspaceId(s));
        }
        return res;
    }

    public boolean isValidTemplateName(String templateName)
    {
        if (Strings.isEmpty(templateName))
            return false;
        for (int i = 0; i < templateName.length(); ++i)
        {
            char c = templateName.charAt(i);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '_')
            {
                return false;
            }
        }
        return true;
    }

    public String getTemplateWorkspaceName(String templateName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        String templateSuffix = "";
        String id = templateName;
        String templateWorkspaceName = brix.getWorkspaceResolver().getWorkspaceName(PREFIX, id,
            templateSuffix);
        return templateWorkspaceName;
    }

    public void createTemplate(String workspaceName, String templateName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        String templateWorkspaceName = getTemplateWorkspaceName(templateName);

        JcrSession originalSession = BrixRequestCycle.Locator.getSession(workspaceName);
        brix.createWorkspace(originalSession, templateWorkspaceName);

        JcrSession destSession = BrixRequestCycle.Locator.getSession(templateWorkspaceName);
        brix.clone(originalSession, destSession);
    }

    public void createTemplate(List<JcrNode> nodes, String templateName)
    {
        if (nodes.isEmpty())
        {
            throw new IllegalStateException("Node list can not be empty.");
        }
        Brix brix = BrixRequestCycle.Locator.getBrix();
        String templateWorkspaceName = getTemplateWorkspaceName(templateName);

        JcrSession originalSession = nodes.iterator().next().getSession();
        brix.createWorkspace(originalSession, templateWorkspaceName);

        JcrSession destSession = BrixRequestCycle.Locator.getSession(templateWorkspaceName);

        JcrUtil.cloneNodes(nodes, destSession.getRootNode(),
            ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
        destSession.save();
    }

    public void restoreTemplateSnapshot(String templateWorkspaceName, String targetWorkspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();

        JcrSession sourceSession = BrixRequestCycle.Locator.getSession(templateWorkspaceName);
        if (brix.getAvailableWorkspaces(sourceSession).contains(targetWorkspaceName) == false)
        {
            brix.createWorkspace(sourceSession, targetWorkspaceName);
        }
        JcrSession targetSession = BrixRequestCycle.Locator.getSession(targetWorkspaceName);
        brix.clone(sourceSession, targetSession);
    }

    public NavigationTreeNode newNavigationTreeNode(String workspaceName)
    {
        return new Node(workspaceName);
    }

    private static class Node implements NavigationTreeNode
    {
        private final String workspaceName;

        public Node(String workspaceName)
        {
            this.workspaceName = workspaceName;
        }

        public boolean getAllowsChildren()
        {
            return false;
        }

        public TreeNode getChildAt(int childIndex)
        {
            return null;
        }

        public int getChildCount()
        {
            return 0;
        }

        public int getIndex(TreeNode node)
        {
            return -1;
        }

        public TreeNode getParent()
        {
            return null;
        }

        public boolean isLeaf()
        {
            return true;
        }

        public Enumeration< ? > children()
        {
            return Collections.enumeration(Collections.emptyList());
        }

        @Override
        public String toString()
        {
            return "Templates";
        }

        public Panel< ? > newLinkPanel(String id, BaseTree tree)
        {
            return new LinkIconPanel(id, new Model<Node>(this), tree)
            {
                @Override
                protected ResourceReference getImageResourceReference(BaseTree tree, TreeNode node)
                {
                    return ICON;
                }
            };
        }

        public NavigationAwarePanel< ? > newManagePanel(String id)
        {
            return new ManageTemplatesPanel(id, workspaceName);
        }
    };

    public List<Entry> getVisibleWorkspaces(String currentWorkspaceName)
    {
        List<String> workspaces = BrixRequestCycle.Locator.getBrix()
            .getAvailableWorkspacesFiltered(PREFIX, null, null);
        List<Entry> res = new ArrayList<Entry>();

        for (String w : workspaces)
        {
            Entry e = new Entry();
            e.workspaceName = w;
            e.userVisibleName = "Template " +
                BrixRequestCycle.Locator.getBrix().getWorkspaceResolver().getWorkspaceId(w);
            res.add(e);
        }

        return res;
    }

    private String getCommonParentPath(List<JcrNode> nodes)
    {
        Path current = null;
        for (JcrNode node : nodes)
        {
            if (current == null)
            {
                current = new Path(node.getPath()).parent();
            }
            else
            {
                Path another = new Path(node.getPath()).parent();

                Path common = Path.ROOT;

                Iterator<String> i1 = current.iterator();
                Iterator<String> i2 = another.iterator();
                while (i1.hasNext() && i2.hasNext())
                {
                    String s1 = i1.next();
                    String s2 = i2.next();
                    if (Objects.equal(s1, s2))
                    {
                        common = common.append(new Path(s1));
                    }
                    else
                    {
                        break;
                    }
                }

                current = common;
            }
        }

        return current.toString();
    }

    public void restoreNodes(List<JcrNode> nodes, JcrNode targetRootNode)
    {
        if (nodes.isEmpty())
        {
            throw new IllegalStateException("List 'nodes' must contain at least one node.");
        }

        ParentLimiter limiter = null;

        if (targetRootNode.getDepth() > 0)
        {
            final String commonParent = getCommonParentPath(nodes);
            limiter = new ParentLimiter()
            {
                public boolean isFinalParent(JcrNode node, JcrNode parent)
                {
                    return parent.getPath().equals(commonParent);
                }
            };
        }

        JcrUtil.cloneNodes(nodes, targetRootNode, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, limiter);
        targetRootNode.save();
    }

    public void initWorkspace(JcrSession workspaceSession)
    {

    }

    private static final ResourceReference ICON = new ResourceReference(TemplatePlugin.class,
        "layers.png");
}
