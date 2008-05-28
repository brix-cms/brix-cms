package brix.plugin.site.node.folder;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.component.PageRequestTarget;

import brix.Brix;
import brix.Path;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SiteNodePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.nodepage.ForbiddenPage;
import brix.web.reference.Reference;

public class FolderNodePlugin implements SiteNodePlugin
{

    public static final String TYPE = Brix.NS_PREFIX + "folder";

    public NavigationAwarePanel newManageNodePanel(String id, IModel<BrixNode> nodeModel)
    {
        return new FolderManagerPanel(id, nodeModel);
    }

    public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters)
    {
        BrixNode node = nodeModel.getObject();

        String path = requestParameters.getPath();
        if (!path.startsWith("/"))
            path = "/" + path;

        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor)RequestCycle.get()
                .getProcessor();
        Path nodePath = processor.getUriPathForNode(node);

        // check if the exact request path matches the node path
        if (new Path(path).equals(nodePath) == false)
        {
            return null;
        }

        FolderNode folder = (FolderNode)node;
        Reference redirect = folder.getRedirectReference();

        if (redirect != null && !redirect.isEmpty())
        {
            IRequestTarget target = redirect.getRequestTarget();
            CharSequence url = RequestCycle.get().urlFor(target);
            return new RedirectRequestTarget(url.toString());
        }
        else
        {
            return new PageRequestTarget(new ForbiddenPage(path));
        }
    }

    public NavigationAwarePanel newCreateNodePanel(String id, IModel<BrixNode> parentNode)
    {
        return new CreateFolderPanel(id, parentNode);
    }

    public NodeConverter getConverterForNode(BrixNode node)
    {
        return null;
    }

    public String getNodeType()
    {
        return TYPE;
    }

    public String getName()
    {
        return "Folder";
    }

}
