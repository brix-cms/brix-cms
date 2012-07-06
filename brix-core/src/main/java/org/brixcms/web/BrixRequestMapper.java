/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.web;

import org.apache.wicket.*;
import org.apache.wicket.core.request.handler.*;
import org.apache.wicket.core.request.mapper.IMapperContext;
import org.apache.wicket.core.request.mapper.IPageSource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.protocol.https.Scheme;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.Url.QueryParameter;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.info.ComponentInfo;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.info.PageInfo;
import org.apache.wicket.request.mapper.parameter.INamedParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.Path;
import org.brixcms.config.BrixConfig;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SiteNodePlugin;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.AbstractSitePagePlugin;
import org.brixcms.web.nodepage.*;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

public class BrixRequestMapper implements IRequestMapper {

    public static final String WORKSPACE_PARAM = Brix.NS_PREFIX + "workspace";

    private static final Logger logger = LoggerFactory.getLogger(BrixRequestMapper.class);

    private static final String COOKIE_NAME = "brix-revision";

    private static final MetaDataKey<String> WORKSPACE_METADATA = new MetaDataKey<String>() {
        private static final long serialVersionUID = 1L;
    };
    private static final Logger log = LoggerFactory.getLogger(BrixRequestMapper.class);
    final Brix brix;
    private boolean handleHomePage = true;
    private HttpsConfig config = null;


    public BrixRequestMapper(Brix brix, HttpsConfig config) {
        this.config = config;
        this.brix = brix;
    }

    public String getWorkspace() {
        String workspace = getWorkspaceFromUrl();

        if (workspace != null) {
            return workspace;
        }

        RequestCycle rc = RequestCycle.get();
        workspace = rc.getMetaData(WORKSPACE_METADATA);
        if (workspace == null) {
            WebRequest req = (WebRequest) RequestCycle.get().getRequest();
            WebResponse resp = (WebResponse) RequestCycle.get().getResponse();
            Cookie cookie = req.getCookie(COOKIE_NAME);
            workspace = getDefaultWorkspaceName();
            if (cookie != null) {
                if (cookie.getValue() != null) {
                    workspace = cookie.getValue();
                }
            }
            if (!checkSession(workspace)) {
                workspace = getDefaultWorkspaceName();
            }
            if (workspace == null) {
                throw new IllegalStateException("Could not resolve jcr workspace to use for this request " + req.getUrl());
            }
            Cookie c = new Cookie(COOKIE_NAME, workspace);
            c.setPath("/");
            if (workspace.toString().equals(getDefaultWorkspaceName()) == false) {
                resp.addCookie(c);
            } else if (cookie != null) {
                resp.clearCookie(cookie);
            }
            rc.setMetaData(WORKSPACE_METADATA, workspace);
        }
        return workspace;
    }

    private String getWorkspaceFromUrl() {
        HttpServletRequest request = (HttpServletRequest) ((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();

        if (request.getParameter(WORKSPACE_PARAM) != null) {
            return request.getParameter(WORKSPACE_PARAM);
        }

        String referer = request.getHeader("referer");

        if (!Strings.isEmpty(referer)) {
            return extractWorkspaceFromReferer(referer);
        } else {
            return null;
        }
    }

    private static String extractWorkspaceFromReferer(String refererURL) {
        int i = refererURL.indexOf('?');
        if (i != -1 && i != refererURL.length() - 1) {
            String param = refererURL.substring(i + 1);
            String params[] = Strings.split(param, '&');
            for (String s : params) {
                try {
                    s = URLDecoder.decode(s, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // rrright
                    throw new RuntimeException(e);
                }
                if (s.startsWith(WORKSPACE_PARAM + "=")) {
                    String value = s.substring(WORKSPACE_PARAM.length() + 1);
                    if (value.length() > 0) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private boolean checkSession(String workspaceId) {
        return brix.getWorkspaceManager().workspaceExists(workspaceId);
    }

    private String getDefaultWorkspaceName() {
        final Workspace workspace = brix.getConfig().getMapper().getWorkspaceForRequest(RequestCycle.get(), brix);
        return (workspace != null) ? workspace.getId() : null;
    }

    @Override
    public IRequestHandler mapRequest(Request request) {
        final Url url = request.getClientUrl();

        if (isInternalWicket(request)) {
            return null;
        }

        // TODO: This is just a quick fix
        if (url.getSegments().size() > 0) {
            if (url.getSegments().get(0).equals("webdav") || url.getSegments().get(0).equals("jcrwebdav")) {
                return null;
            }
        }

        Path path = new Path("/" + url.getPath());

        // root path handling
        if (path.isRoot()) {
            if (handleHomePage) {
                final BrixNode node = getNodeForUriPath(path);
                return SitePlugin.get().getNodePluginForNode(node)
                        .respond(new BrixNodeModel(node), new BrixPageParameters(request.getRequestParameters()));
            } else {
                return null;
            }
        }

        IRequestHandler handler = null;
        try {
            while (handler == null) {
                final BrixNode node = getNodeForUriPath(path);
                if (node != null) {
                    SiteNodePlugin plugin = SitePlugin.get().getNodePluginForNode(node);
                    if (plugin instanceof AbstractSitePagePlugin) {
                        handler = SitePlugin.get().getNodePluginForNode(node)
                                .respond(new BrixNodeModel(node), createBrixPageParams(request.getUrl(), path));
                    } else {
                        handler = SitePlugin.get().getNodePluginForNode(node)
                                .respond(new BrixNodeModel(node), new BrixPageParameters(request.getRequestParameters()));
                    }
                }
                if (handler != null || path.toString().equals(".")) {
                    break;
                }
                path = path.parent();
                if (path.isRoot()) {
                    break;
                }
            }
        } catch (JcrException e) {
            logger.warn("JcrException caught due to incorrect url", e);
        }

        final PageComponentInfo info = getPageComponentInfo(request.getUrl());
        if (info != null && info.getPageInfo().getPageId() != null) {
            Integer renderCount = info.getComponentInfo() != null ? info.getComponentInfo().getRenderCount() : null;

            if (info.getComponentInfo() == null) {
                PageProvider provider;
                if (handler instanceof BrixNodePageRequestHandler) {
                    provider = new PageProvider(info.getPageInfo().getPageId(), BrixNodeWebPage.class, renderCount);
                    BrixNodePageRequestHandler brixNodePageRequestHandler = (BrixNodePageRequestHandler) handler;
                    final IPageProvider pageProviderAdapter = brixNodePageRequestHandler.getPageProvider();
                    provider.setPageSource(new IPageSource() {
                        @Override
                        public IRequestablePage getPageInstance(int pageId) {
                            IRequestablePage page = null;
                            Integer existingPageId = pageProviderAdapter.getPageId();
                            if (existingPageId != null && pageId == existingPageId) {
                                page = pageProviderAdapter.getPageInstance();
                            }
                            return page;
                        }

                        @Override
                        public IRequestablePage newPageInstance(Class<? extends IRequestablePage> pageClass, PageParameters pageParameters) {
                            IRequestablePage page = pageProviderAdapter.getPageInstance();
                            page.getPageParameters().set(info.toString(), "");
                            return page;
                        }
                    });
                } else {
                    provider = new PageProvider(info.getPageInfo().getPageId(), renderCount);
                    provider.setPageSource(getContext());
                }

                // render page
                return new RenderPageRequestHandler(provider);
            } else {
                ComponentInfo componentInfo = info.getComponentInfo();
                PageAndComponentProvider provider = new PageAndComponentProvider(info.getPageInfo().getPageId(), renderCount,
                        componentInfo.getComponentPath());

                provider.setPageSource(getContext());
                // listener interface
                RequestListenerInterface listenerInterface = requestListenerInterfaceFromString(componentInfo.getListenerInterface());

                return new ListenerInterfaceRequestHandler(provider, listenerInterface, componentInfo.getBehaviorId());
            }
        }


        Scheme desired = getDesiredSchemeFor(handler);
        Scheme current = getSchemeOf(request);
        if (!desired.isCompatibleWith(current)) {
            // we are currently on the wrong scheme for this handler

            // construct a url for the handler on the correct scheme
            String urlChange = createRedirectUrl(handler, request, desired);

            // replace handler with one that will redirect to the created url
            handler = createRedirectHandler(urlChange);
        }

        return handler;
    }

    private BrixPageParameters createBrixPageParams(Url url, Path path) {
        BrixPageParameters parameters = new BrixPageParameters();
        Path nodePath = path;
        Path requestPath = new Path("/" + url.getPath());

        if (nodePath.isAncestorOf(requestPath)) {
            Path remaining = new Path(requestPath.toString(), false).toRelative(nodePath);
            int i = 0;
            for (String s : remaining) {
                parameters.set(i, BrixNodePageUrlMapper.urlDecode(s));
                ++i;
            }
        }
        for (QueryParameter parameter : url.getQueryParameters()) {
            String parameterName = parameter.getName();
            if (PageComponentInfo.parse(parameterName) == null) {
                parameters.add(parameterName, parameter.getValue());
            }
        }
        return parameters;
    }

    @Override
    public int getCompatibilityScore(Request request) {
        if (isInternalWicket(request)) {
            return 0;
        }
        // bluff we can parse all segments - makes sure we run first
        return request.getUrl().getSegments().size();
    }

    private boolean isInternalWicket(Request request) {
        Url url = request.getUrl();
        if (url.getSegments().size() > 0) {
            if (url.getSegments().get(0).equals((Application.get().getMapperContext().getNamespace()))) {
                // starts with wicket namespace - is an internal wicket url
                return true;
            }
        }
        return false;
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        if (requestHandler instanceof BrixNodeRequestHandler) {
            BrixNodeRequestHandler handler = (BrixNodeRequestHandler) requestHandler;
            String nodeURL = handler.getNodeURL();
            return encode(nodeURL, handler.getPageParameters(), null);
        } else if (requestHandler instanceof ListenerInterfaceRequestHandler) {
            ListenerInterfaceRequestHandler handler = (ListenerInterfaceRequestHandler) requestHandler;
            if (handler.getPage() instanceof BrixNodeWebPage) {
                BrixNodeWebPage page = (BrixNodeWebPage) handler.getPage();
                String componentPath = handler.getComponentPath();
                RequestListenerInterface listenerInterface = handler.getListenerInterface();
                Integer renderCount = null;
                if (listenerInterface.isIncludeRenderCount()) {
                    renderCount = page.getRenderCount();
                }
                PageInfo pageInfo = new PageInfo(page.getPageId());
                ComponentInfo componentInfo = new ComponentInfo(renderCount, requestListenerInterfaceToString(listenerInterface),
                        componentPath, handler.getBehaviorIndex());
                PageComponentInfo info = new PageComponentInfo(pageInfo, componentInfo);
                Url url = encode(page);
                encodePageComponentInfo(url, info);
                return url;
            } else {
                return null;
            }
        } else if (requestHandler instanceof RenderPageRequestHandler) {
            RenderPageRequestHandler handler = (RenderPageRequestHandler) requestHandler;
            if (handler.getPage() instanceof BrixNodeWebPage) {
                BrixNodeWebPage page = (BrixNodeWebPage) handler.getPage();
                PageInfo i = new PageInfo(page.getPageId());
                PageComponentInfo info = new PageComponentInfo(i, null);
                Url url = encode(page);
                encodePageComponentInfo(url, info);
                return url;
            } else {
                return null;
            }
        } else if (requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
            BookmarkableListenerInterfaceRequestHandler target = (BookmarkableListenerInterfaceRequestHandler) requestHandler;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            BrixNode node = page.getModelObject();
            PageInfo info = new PageInfo(page.getPageId());
            return encode(node, page.getBrixPageParameters(), info);
        } else if (requestHandler instanceof BookmarkablePageRequestHandler
                && ((BookmarkablePageRequestHandler) requestHandler).getPageClass().equals(HomePage.class)) {
            BrixNode node = ((BrixRequestCycleProcessor) RequestCycle.get().getActiveRequestHandler()).getNodeForUriPath(Path.ROOT);
            return mapHandler(new BrixNodeRequestHandler(new BrixNodeModel(node)));
        } else {
            return null;
        }
    }

    /**
     * Converts the specified listener interface to String.
     *
     * @param listenerInterface
     * @return listenerInterface name as string
     */
    protected String requestListenerInterfaceToString(RequestListenerInterface listenerInterface) {
        Args.notNull(listenerInterface, "listenerInterface");

        return getContext().requestListenerInterfaceToString(listenerInterface);
    }

    /**
     * Extracts the {@link PageComponentInfo} from the URL. The
     * {@link PageComponentInfo} is encoded as the very first query parameter
     * and the parameter consists of name only (no value).
     *
     * @param url
     * @return PageComponentInfo instance if one was encoded in URL,
     *         <code>null</code> otherwise.
     */
    protected PageComponentInfo getPageComponentInfo(final Url url) {
        if (url == null) {
            throw new IllegalStateException("Argument 'url' may not be null.");
        } else {
            for (QueryParameter queryParameter : url.getQueryParameters()) {
                if (Strings.isEmpty(queryParameter.getValue())) {
                    PageComponentInfo pageComponentInfo = PageComponentInfo.parse(queryParameter.getName());
                    if (pageComponentInfo != null) {
                        return pageComponentInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates listener interface from the specified string
     *
     * @param interfaceName
     * @return listener interface
     */
    protected RequestListenerInterface requestListenerInterfaceFromString(String interfaceName) {
        Args.notEmpty(interfaceName, "interfaceName");

        return getContext().requestListenerInterfaceFromString(interfaceName);
    }

    protected IMapperContext getContext() {
        return Application.get().getMapperContext();
    }

    protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
        Args.notNull(url, "url");
        if (info != null && Session.exists() && !Session.get().isSessionInvalidated()) {
            String s = info.toString();
            if (!Strings.isEmpty(s)) {
                QueryParameter parameter = new QueryParameter(s, "");
                url.getQueryParameters().add(parameter);
            }
        }
    }

    private Url encode(BrixNodeWebPage page) {
        BrixNode node = page.getModelObject();
        // This is a URL for redirect. Allow components to contribute state to
        // URL if they want to
        final BrixPageParameters parameters = page.getBrixPageParameters();
        for (INamedParameters.NamedPair namedPair : parameters.getAllNamed()) {
            if (isNumber(namedPair.getKey())) {
                parameters.remove(namedPair.getKey());
            }
        }
        page.visitChildren(PageParametersAware.class, new IVisitor<Component, PageParametersAware>() {
            @Override
            public void component(Component component, IVisit iVisit) {
                ((PageParametersAware) component).contributeToPageParameters(parameters);
            }
        });
        return encode(node, parameters, null);
    }

    private Url encode(BrixNode node, PageParameters parameters, PageInfo info) {
        return encode(getUriPathForNode(node).toString(), parameters, info);
    }

    private Url encode(String nodeURL, PageParameters parameters, PageInfo info) {
        StringBuilder builder = new StringBuilder();

        if (nodeURL.startsWith("/")) {
            nodeURL = nodeURL.substring(1);
        }

        builder.append(urlEncodePath(new Path(nodeURL, false)));

        boolean skipFirstSlash = builder.charAt(builder.length() - 1) == '/';

        for (int i = 0; i < parameters.getIndexedCount(); ++i) {
            if (!skipFirstSlash) {
                builder.append('/');
            } else {
                skipFirstSlash = false;
            }

            final StringValue value = parameters.get(i);
            final String s = value.toString();

            if (s != null) {
                builder.append(urlEncode(s));
            }
        }

        Set<String> keys = parameters.getNamedKeys();
        if (info != null || !keys.isEmpty()) {
            builder.append("?");
        }

        if (info != null) {
            builder.append(info.toString());
        }

        boolean first = (info == null);

        for (String key : keys) {
            List<StringValue> values = parameters.getValues(key);
            for (StringValue value : values) {
                if (first) {
                    first = false;
                } else {
                    builder.append("&");
                }
                builder.append(urlEncode(key));
                builder.append("=");
                builder.append(urlEncode(value.toString()));
            }
        }

        return Url.parse(builder.toString());
    }

    private String urlEncodePath(Path path) {
        StringBuilder res = new StringBuilder(path.size());
        boolean first = true;
        for (String s : path) {
            if (first) {
                first = false;
            } else {
                res.append("/");
            }
            res.append(urlEncode(s));
        }
        return res.toString();
    }

    /**
     * Url encodes a string
     *
     * @param string string to be encoded
     * @return encoded string
     */
    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, Application.get().getRequestCycleSettings().getResponseRequestEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return string;
        }
    }

    /**
     * Resolves uri path to a {@link BrixNode}. By default this method uses
     * {@link BrixConfig#getMapper()} to map the uri to a node path.
     *
     * @param uriPath uri path
     * @return node that maps to the <code>uriPath</code> or <code>null</code>
     *         if none
     */
    public BrixNode getNodeForUriPath(final Path uriPath) {
        BrixNode node = null;

        // create desired nodepath
        final Path nodePath = brix.getConfig().getMapper().getNodePathForUriPath(uriPath.toAbsolute(), brix);

        if (nodePath != null) {
            // allow site plugin to translate the node path into an actual jcr
            // path
            final String jcrPath = SitePlugin.get().toRealWebNodePath(nodePath.toString());

            // retrieve jcr session
            final String workspace = getWorkspace();
            final JcrSession session = brix.getCurrentSession(workspace);

            if (session.itemExists(jcrPath)) {
                // node exists, return it
                node = (BrixNode) session.getItem(jcrPath);
            }
        }

        return node;
    }

    /**
     * Creates a uri path for the specified <code>node</code> By default this
     * method uses {@link BrixConfig#getMapper()} to map node path to a uri
     * path.
     *
     * @param node node to create uri path for
     * @return uri path that represents the node
     */
    public Path getUriPathForNode(final BrixNode node) {
        // allow site plugin to translate jcr path into node path
        final String jcrPath = SitePlugin.get().fromRealWebNodePath(node.getPath());
        final Path nodePath = new Path(jcrPath);

        // use urimapper to create the uri
        return brix.getConfig().getMapper().getUriPathForNode(nodePath, brix);
    }

    /**
     * Method that rigidly checks if the string consists of digits only.
     *
     * @param string
     * @return
     */
    private static boolean isNumber(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isDigit(string.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }


//    /**
//     * Creates a url for the handler. Modifies it with the correct {@link Scheme} if necessary.
//     *
//     * @param handler
//     * @param request
//     * @return url
//     */
//    final Url mapHandler(IRequestHandler handler, Request request) {
//        Url url = delegate.mapHandler(handler);
//
//        Scheme desired = getDesiredSchemeFor(handler);
//        Scheme current = getSchemeOf(request);
//        if (!desired.isCompatibleWith(current)) {
//            // the generated url does not have the correct scheme, set it (which in turn will cause
//            // the url to be rendered in its full representation)
//            url.setProtocol(desired.urlName());
//            if (url.getPort() != null || !desired.usesStandardPort(config)) {
//                url.setPort(desired.getPort(config));
//            }
//        }
//        return url;
//    }

    /**
     * Creates the {@link IRequestHandler} that will be responsible for the redirect
     *
     * @param url
     * @return request handler
     */
    protected IRequestHandler createRedirectHandler(String url) {
        return new HttpsMapper.RedirectHandler(url, config);
    }

    /**
     * Construts a redirect url that should switch the user to the specified {@code scheme}
     *
     * @param handler request handler being accessed
     * @param request current request
     * @param scheme  desired scheme for the redirect url
     * @return url
     */
    protected String createRedirectUrl(IRequestHandler handler, Request request, Scheme scheme) {
        HttpServletRequest req = (HttpServletRequest) ((WebRequest) request).getContainerRequest();
        String url = scheme.urlName() + "://";
        url += req.getServerName();
        if (!scheme.usesStandardPort(config)) {
            url += ":" + scheme.getPort(config);
        }
        url += req.getRequestURI();
        if (req.getQueryString() != null) {
            url += "?" + req.getQueryString();
        }
        return url;
    }


    /**
     * Figures out which {@link org.apache.wicket.protocol.https.Scheme} should be used to access the request handler
     *
     * @param handler request handler
     * @return {@link org.apache.wicket.protocol.https.Scheme}
     */
    protected Scheme getDesiredSchemeFor(IRequestHandler handler) {
        if (handler instanceof BrixNodePageRequestHandler) {
            BrixNode.Protocol protocol = ((BrixNodePageRequestHandler) handler).getPage().getPageNode().getRequiredProtocol();
            switch (protocol) {
                case HTTP:
                    return Scheme.HTTP;
                case HTTPS:
                    return Scheme.HTTPS;
                /**
                 * could be seen as not really correct as PRESERVE_CURRENT could also relate to "not change the scheme",
                 * however, Brix traditionally respected this as "NON SSL" like described on the SSL Page in the brix-demo:
                 *
                 * "... The URL will revert back to a non-secure one once the user navigates to a page that does not require SSL. "
                 *
                 */
                case PRESERVE_CURRENT:
                    return Scheme.HTTP;

            }
        }
        return Scheme.ANY;
    }

    /**
     * Determines the {@link Scheme} of the request
     *
     * @param request
     * @return {@link Scheme#HTTPS} or {@link Scheme#HTTP}
     */
    protected Scheme getSchemeOf(Request request) {
        HttpServletRequest req = (HttpServletRequest) request.getContainerRequest();

        if ("https".equalsIgnoreCase(req.getScheme())) {
            return Scheme.HTTPS;
        } else if ("http".equalsIgnoreCase(req.getScheme())) {
            return Scheme.HTTP;
        } else {
            throw new IllegalStateException("Could not resolve protocol for request: " + req);
        }
    }

    // -------------------------- INNER CLASSES --------------------------

    public static final class HomePage extends WebPage {
    }
}
