package brix.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
import org.apache.wicket.util.string.Strings;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Path;
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

    public abstract BrixNode getNodeForUriPath(Path path);

    public abstract Path getUriPathForNode(BrixNode node);

    public abstract int getHttpPort();

    public abstract int getHttpsPort();

    public BrixRequestCycleProcessor setHandleHomePage(boolean handleHomePage)
    {
        this.handleHomePage = handleHomePage;
        return this;
    }

    private static final String COOKIE_NAME = "brix-revision";

    private static final MetaDataKey<String> WORKSPACE_METADATA = new MetaDataKey<String>()
    {
        private static final long serialVersionUID = 1L;
    };

    private boolean checkSession(String workspaceId)
    {
        return brix.getWorkspaceManager().workspaceExists(workspaceId);
    }

    public static final String WORKSPACE_PARAM = Brix.NS_PREFIX + "workspace";

    private static String extractWorkspaceFromReferer(String refererURL)
    {
        int i = refererURL.indexOf('?');
        if (i != -1 && i != refererURL.length() - 1)
        {
            String param = refererURL.substring(i + 1);
            String params[] = Strings.split(param, '&');
            for (String s : params)
            {
                try
                {
                    s = URLDecoder.decode(s, "utf-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    // rrright
                    throw new RuntimeException(e);
                }
                if (s.startsWith(WORKSPACE_PARAM + "="))
                {
                    String value = s.substring(WORKSPACE_PARAM.length() + 1);
                    if (value.length() > 0)
                    {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private String getWorkspaceFromUrl()
    {
        HttpServletRequest request = ((WebRequest)RequestCycle.get().getRequest())
            .getHttpServletRequest();

        if (request.getParameter(WORKSPACE_PARAM) != null)
        {
            return request.getParameter(WORKSPACE_PARAM);
        }

        String referer = request.getHeader("referer");

        if (!Strings.isEmpty(referer))
        {
            return extractWorkspaceFromReferer(referer);
        }
        else
        {
            return null;
        }
    }

    public String getWorkspace()
    {
        String workspace = getWorkspaceFromUrl();

        if (workspace != null)
        {
            return workspace;
        }

        RequestCycle rc = RequestCycle.get();
        workspace = rc.getMetaData(WORKSPACE_METADATA);
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
            if (!checkSession(workspace))
            {
                workspace = getDefaultWorkspaceName();
            }
            if (workspace == null)
            {
                throw new IllegalStateException(
                    "Could not resolve jcr workspace to use for this request");
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

        private IRequestTarget getSwitchTarget(BrixNode node)
        {
            if (node instanceof BrixNode)
            {
                return SwitchProtocolRequestTarget.requireProtocol(((BrixNode)node)
                    .getRequiredProtocol());
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
                final BrixNode node = getNodeForUriPath(path);
                if (node != null)
                {
                    target = getSwitchTarget(node);
                    if (target == null)
                    {
                        target = SitePlugin.get().getNodePluginForNode(node).respond(
                            new BrixNodeModel(node), requestParameters);
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
                // return new
                // WebErrorCodeResponseTarget(HttpServletResponse.SC_NOT_FOUND,
                // "Resource
                // " + pathStr
                // + " not found");
                return null;
                // return new PageRequestTarget(new
                // ResourceNotFoundPage(pathStr));
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
