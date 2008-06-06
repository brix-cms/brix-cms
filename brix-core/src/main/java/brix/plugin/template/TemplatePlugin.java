package brix.plugin.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.jcr.JcrUtil;
import brix.jcr.JcrUtil.ParentLimiter;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.AbstractNavigationTreeNode;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class TemplatePlugin implements Plugin
{

    private static final String ID = TemplatePlugin.class.getName();

    private final Brix brix;
    
    public TemplatePlugin(Brix brix)
    {
        this.brix = brix;
    }

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
        return get(Brix.get());
    }

    private static final String WORKSPACE_TYPE = "brix:template";

    private static final String WORKSPACE_ATTRIBUTE_TEMPLATE_NAME = "brix:template-name";

    public boolean isTemplateWorkspace(Workspace workspace)
    {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public void setTemplateName(Workspace workspace, String name)
    {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_TEMPLATE_NAME, name);
    }

    public String getTemplateName(Workspace workspace)
    {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_TEMPLATE_NAME);
    }


    public List<Workspace> getTemplates()
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        return brix.getWorkspaceManager().getWorkspacesFiltered(
            attributes);
    }

    public boolean templateExists(String templateName)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        attributes.put(WORKSPACE_ATTRIBUTE_TEMPLATE_NAME, templateName);
        return !Brix.get().getWorkspaceManager().getWorkspacesFiltered(
            attributes).isEmpty();
    }

    public void createTemplate(Workspace originalWorkspace, String templateName)
    {
        Brix brix = Brix.get();

        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setTemplateName(workspace, templateName);

        JcrSession originalSession = brix.getCurrentSession(originalWorkspace.getId());
        JcrSession destSession = brix.getCurrentSession(workspace.getId());
        brix.clone(originalSession, destSession);
    }

    public void createTemplate(List<JcrNode> nodes, String templateName)
    {
        if (nodes.isEmpty())
        {
            throw new IllegalStateException("Node list can not be empty.");
        }
        Brix brix = Brix.get();

        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setTemplateName(workspace, templateName);

        JcrSession destSession = brix.getCurrentSession(workspace.getId());

        JcrUtil.cloneNodes(nodes, destSession.getRootNode(),
            ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
        destSession.save();
    }

    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {
        return new Node(workspace.getId());
    }

    private static class Node extends AbstractNavigationTreeNode
    {
        public Node(String workspaceId)
        {
            super(workspaceId);
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
                protected ResourceReference getImageResourceReference(BaseTree tree, Object node)
                {
                    return ICON;
                }
            };
        }

        public NavigationAwarePanel< ? > newManagePanel(String id)
        {
            return new ManageTemplatesPanel(id, new WorkspaceModel(getWorkspaceId()));
        }
    };

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

        JcrUtil.cloneNodes(nodes, targetRootNode, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW,
            limiter);
        targetRootNode.save();
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {

    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return "Template " + getTemplateName(workspace);
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        if (isFrontend)
        {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
            return Brix.get().getWorkspaceManager().getWorkspacesFiltered(
                attributes);
        }
        else
        {
            return null;
        }
    }

    private static final ResourceReference ICON = new ResourceReference(TemplatePlugin.class,
        "layers.png");
}
