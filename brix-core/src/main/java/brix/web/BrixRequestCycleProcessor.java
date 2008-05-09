package brix.web;

import javax.servlet.http.Cookie;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Path;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;

public abstract class BrixRequestCycleProcessor extends WebRequestCycleProcessor
{
    private final Brix brix;
    private final BrixUrlCodingStrategy urlCodingStrategy;
    private boolean handleHomePage = true;

    public BrixRequestCycleProcessor(Brix brix)
    {
        urlCodingStrategy = new BrixUrlCodingStrategy();
        this.brix = brix;
    }

    public abstract JcrNode getNodeForUriPath(Path path);

    public abstract Path getUriPathForNode(JcrNode node);

    public abstract int getHttpPort();

    public abstract int getHttpsPort();

    public BrixRequestCycleProcessor setHandleHomePage(boolean handleHomePage)
    {
        this.handleHomePage = handleHomePage;
        return this;
    }

    private static final String COOKIE_NAME = "brix-revision";

    private static final MetaDataKey<String> WORKSPACE_METADATA = new MetaDataKey<String>(String.class)
    {
		private static final long serialVersionUID = 1L;
    };

    public String getWorkspace()
    {
        RequestCycle rc = RequestCycle.get();
        String workspace = rc.getMetaData(WORKSPACE_METADATA);
        if (workspace == null)
        {
            WebRequest req = (WebRequest)RequestCycle.get().getRequest();
            WebResponse resp = (WebResponse)RequestCycle.get().getResponse();
            Cookie cookie = req.getCookie(COOKIE_NAME);
            workspace = getDefaultWorkspaceName();
            if (cookie != null)
            {
                if (cookie.getValue() != null)
                    workspace = cookie.getValue();
            }
            if (workspace.toString().equals(getDefaultWorkspaceName()) == false)
                resp.addCookie(new Cookie(COOKIE_NAME, workspace));
            else if (cookie != null)
                resp.clearCookie(cookie);
            rc.setMetaData(WORKSPACE_METADATA, workspace);
        }
        return workspace;
    }

    protected abstract String getDefaultWorkspaceName();

    @Override
    protected IRequestCodingStrategy newRequestCodingStrategy()
    {
        return new BrixRequestCodingStrategy();
    }

    @Override
    protected IRequestTarget resolveHomePageTarget(RequestCycle requestCycle,
            RequestParameters requestParameters)
    {
        if (handleHomePage)
        {
            return urlCodingStrategy.decode(requestParameters);
        }
        else
        {
            return super.resolveHomePageTarget(requestCycle, requestParameters);
        }
    }

    private class BrixRequestCodingStrategy extends WebRequestCodingStrategy
    {

        @Override
        public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath(String path)
        {

            IRequestTargetUrlCodingStrategy target = super.urlCodingStrategyForPath(path);
            if (target == null)
            {
                target = urlCodingStrategy;
            }
            return target;
        }
    }

    private class BrixUrlCodingStrategy implements IRequestTargetUrlCodingStrategy
    {

        private Path decode(Path path)
        {
            StringBuilder builder = new StringBuilder(path.toString().length());
            boolean first = true;
            for (String s : path)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    builder.append("/");
                }
                builder.append(BrixNodePageUrlCodingStrategy.urlDecode(s));
            }
            if (builder.length() == 0)
            {
                builder.append("/");
            }
            return new Path(builder.toString(), false);
        }
        
        private IRequestTarget getSwitchTarget(JcrNode node) 
        {
        	if (node instanceof BrixNode) 
        	{
        		return SwitchProtocolRequestTarget.requireProtocol(((BrixNode) node).getRequiredProtocol());
        	} 
        	else
        	{
        		return null;
        	}
        }

        public IRequestTarget targetForPath(String pathStr, RequestParameters requestParameters)
        {
            if (!pathStr.startsWith("/"))
            {
                pathStr = "/" + pathStr;
            }

            // TODO: This is just a quick fix
            if (pathStr.startsWith("/webdav") || pathStr.startsWith("/jcrwebdav"))
            {
                return null;
            }

            Path path = decode(new Path(pathStr, false));

            IRequestTarget target = null;

            while (target == null)
            {
                final JcrNode node = getNodeForUriPath(path);
                if (node != null)
                {
                	target = getSwitchTarget(node);
                	if (target == null)
                	{
                		target = SitePlugin.get().getNodePluginForNode(node).respond(new BrixNodeModel(node), requestParameters);
                	}
                }
                if (path.isRoot() || path.toString().equals("."))
                {
                    break;
                }
                path = path.parent();
            }
            return target;
        }

        public IRequestTarget decode(RequestParameters requestParameters)
        {
            String pathStr = requestParameters.getPath();

            IRequestTarget target = targetForPath(pathStr, requestParameters);

            if (target == null)
            {
                // 404 if node not found
                // return new WebErrorCodeResponseTarget(HttpServletResponse.SC_NOT_FOUND, "Resource
                // " + pathStr
                // + " not found");
                return null;
                // return new PageRequestTarget(new ResourceNotFoundPage(pathStr));
            }
            else
            {
                return target;
            }

        }

        public CharSequence encode(IRequestTarget requestTarget)
        {
            throw new UnsupportedOperationException();
        }

        public String getMountPath()
        {
            throw new UnsupportedOperationException();
        }

        public boolean matches(IRequestTarget requestTarget)
        {
            throw new UnsupportedOperationException();
        }

        public boolean matches(String path)
        {
            throw new UnsupportedOperationException();
        }
    }

}
