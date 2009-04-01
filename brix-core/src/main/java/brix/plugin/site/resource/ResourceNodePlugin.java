package brix.plugin.site.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.RequestParameters;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.resource.admin.ManageResourceNodeTabFactory;
import brix.plugin.site.resource.admin.UploadResourcesPanel;
import brix.plugin.site.resource.managers.image.ImageNodeTabFactory;
import brix.plugin.site.resource.managers.text.TextNodeTabFactory;

public class ResourceNodePlugin implements SiteNodePlugin
{

    public static final String TYPE = Brix.NS_PREFIX + "resource";

    public ResourceNodePlugin(SitePlugin sp)
    {
        registerDefaultMimeTypes();
        sp.registerManageNodeTabFactory(new ManageResourceNodeTabFactory());
        sp.registerManageNodeTabFactory(new ImageNodeTabFactory());
        sp.registerManageNodeTabFactory(new TextNodeTabFactory());
    }

    public String getNodeType()
    {
        return TYPE;
    }

    public String getName()
    {
        return "Resource";
    }

    public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters)
    {
//    	IRequestTarget switchTarget = SwitchProtocolRequestTarget.requireProtocol(Protocol.HTTP);
//    	if (switchTarget != null) 
//    	{
//    		return switchTarget;
//    	} 
//    	else 
//    	{
    		return new ResourceRequestTarget(nodeModel);
//    	}
    }

    public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack)
    {
        return new UploadResourcesPanel(id, parentNode, goBack);
    }

    public NodeConverter getConverterForNode(BrixNode node)
    {
        return null;
    }

    public String resolveMimeTypeFromFileName(String fileName)
    {
        int last = fileName.lastIndexOf(".");
        if (last != -1)
        {
            String ext = fileName.substring(last + 1).toLowerCase();
            return mimeTypeFromExtension(ext);
        }
        return null;
    }

    private String mimeTypeFromExtension(String ext)
    {
        return mimeTypes.get(ext);
    }

    private Map<String /* extension */, String /* mime-type */> mimeTypes = new ConcurrentHashMap<String, String>();

    public void registerMimeType(String mimeType, String... extensions)
    {
        for (String s : extensions)
        {
            mimeTypes.put(s, mimeType);
        }
    }

    private void registerDefaultMimeTypes()
    {
        registerMimeType("application/xml", "xml");
        registerMimeType("text/html", "html", "htm", "dwt");
        registerMimeType("text/plain", "txt");
        registerMimeType("text/css", "css");
        registerMimeType("text/javascript", "js");
        registerMimeType("image/jpeg", "jpg", "jpeg");
        registerMimeType("image/png", "png");
        registerMimeType("image/gif", "gif");
        registerMimeType("application/octet-stream", "exe");
        registerMimeType("application/octet-stream", "dmg");
    }

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode)
    {
    	return new Model<String>("Upload Images & Documents");
    }
}
