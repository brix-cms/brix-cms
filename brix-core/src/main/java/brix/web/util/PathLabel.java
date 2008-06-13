package brix.web.util;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WicketURLDecoder;
import org.apache.wicket.util.value.ValueMap;

import brix.Path;

public abstract class PathLabel extends WebMarkupContainer<String> implements ILinkListener
{

    private final Path root;

    public PathLabel(String id, IModel<String> model, Path root)
    {
        super(id, model);
        this.root = root;
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
        final Response r = getResponse();
        final Path path = new Path(getModelObject().toString());
        final int size = path.size();
        for (int i = root.size() + 1; i < size + 1; i++)
        {
            final Path subpath = path.subpath(i);
             
            writePath(subpath, r, false);
            if (i <= size)
            {
                r.write("&nbsp;/&nbsp;");
            }
        }
        writePath(path, r, true);
    }

    private void writePath(Path path, Response r, boolean last)
    {
        r.write("<a href=\"");
        r.write(createCallbackUrl(path));
        r.write("\"><span");
        if (last)
        {
        	r.write(" class=\"brix-node-path-last\"");
        }
        r.write(">");
        boolean isRoot = path.equals(root);
        r.write(isRoot ? getRootNodeName() : path.getName());
        r.write("</span></a>");
    }

    private CharSequence createCallbackUrl(Path subpath)
    {
        ValueMap params = new ValueMap();
        params.add("path", subpath.toString());
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
    
    protected String getRootNodeName()
    {
    	return "root";
    }

    protected abstract void onPathClicked(Path path);

}
