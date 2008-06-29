package brix.web.util;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WicketURLDecoder;
import org.apache.wicket.util.string.PrependingStringBuffer;
import org.apache.wicket.util.value.ValueMap;

import brix.Path;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericWebMarkupContainer;

public abstract class PathLabel extends BrixGenericWebMarkupContainer<BrixNode> implements ILinkListener
{

    private final String rootPath;

    public PathLabel(String id, IModel<BrixNode> model, String rootPath)
    {
        super(id, model);
        this.rootPath = rootPath;
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
    	PrependingStringBuffer b = new PrependingStringBuffer();
    	BrixNode current = getModelObject();
    	
    	while(true)
    	{
	    	StringBuilder builder = new StringBuilder();
	    	writePath(current, builder, current.equals(getModelObject()));
	    	if (b.length() > 0)
	    	{
	    		b.prepend("&nbsp;/&nbsp;");	    		
	    	}
	    	b.prepend(builder.toString());
	    	if (current.getDepth() == 0 || current.getPath().equals(rootPath))
	    	{
	    		break;
	    	}
	    	current = (BrixNode) current.getParent();
    	}
    	
        final Response r = getResponse();
        r.write(b.toString());
    }

    private void writePath(BrixNode node, StringBuilder builder, boolean last)
    {
        builder.append("<a href=\"");
        builder.append(createCallbackUrl(node.getPath()));
        builder.append("\"><span");
        if (last)
        {
        	builder.append(" class=\"brix-node-path-last\"");
        }
        builder.append(">");        
        builder.append(node.getUserVisibleName());
        builder.append("</span></a>");
    }

    private CharSequence createCallbackUrl(String subpath)
    {
        ValueMap params = new ValueMap();
        params.add("path", subpath);
        return getRequestCycle().urlFor(this, ILinkListener.INTERFACE, params);
    }

    public final void onLinkClicked()
    {
        String path = getRequest().getParameter("path");
        if (path == null)
        {
            path = getRequestCycle().getPageParameters().getString("path");
        }
        path = WicketURLDecoder.QUERY_INSTANCE.decode(path);
        onPathClicked(new Path(path));

    }    

    protected abstract void onPathClicked(Path path);    
}
