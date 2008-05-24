package brix.web.util;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.target.component.listener.ListenerInterfaceRequestTarget;
import org.apache.wicket.util.value.ValueMap;

import brix.Brix;
import brix.Path;

public abstract class PathLabel extends WebMarkupContainer implements ILinkListener
{

    private final Path root;

    public PathLabel(String id, IModel model, Path root)
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
            writePath(subpath, r);
            if (i <= size)
            {
                r.write("&nbsp;/&nbsp;");
            }
        }
        writePath(path, r);
    }

    private void writePath(Path path, Response r)
    {
        r.write("<a href=\"");
        r.write(createCallbackUrl(path));
        r.write("\"><span>");
        boolean root = path.toString().equals(Brix.get().getRootPath());
        r.write(root ? "root" : path.getName());
        r.write("</span></a>");
    }

    private CharSequence createCallbackUrl(Path subpath)
    {
        IRequestTarget t = new ListenerInterfaceRequestTarget(getPage(), this,
                ILinkListener.INTERFACE);

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
        path = RequestUtils.decode(path);
        onPathClicked(new Path(path));

    }

    protected abstract void onPathClicked(Path path);

}
