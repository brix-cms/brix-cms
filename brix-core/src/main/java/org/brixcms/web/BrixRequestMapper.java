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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.info.PageInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.nodepage.BrixNodePageRequestHandler;
import org.brixcms.web.nodepage.BrixNodeRequestHandler;
import org.brixcms.web.nodepage.BrixNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;
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
    // ------------------------------ FIELDS ------------------------------

    public static final String WORKSPACE_PARAM = Brix.NS_PREFIX + "workspace";

    private static final Logger logger = LoggerFactory.getLogger(BrixRequestMapper.class);

    private static final String COOKIE_NAME = "brix-revision";

    private static final MetaDataKey<String> WORKSPACE_METADATA = new MetaDataKey<String>() {
        private static final long serialVersionUID = 1L;
    };
    private static final Logger log = LoggerFactory.getLogger(BrixRequestMapper.class);
    final Brix brix;
    private boolean handleHomePage = true;

    // --------------------------- CONSTRUCTORS ---------------------------

    public BrixRequestMapper(Brix brix) {
        this.brix = brix;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

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
                throw new IllegalStateException("Could not resolve jcr workspace to use for this request");
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
                }
                catch (UnsupportedEncodingException e) {
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

    // ------------------------ INTERFACE METHODS ------------------------


    @Override
    public IRequestHandler mapRequest(Request request) {
        final Url url = request.getClientUrl();

        // TODO: This is just a quick fix
        if (url.getSegments().size() > 0) {
            if (url.getSegments().get(0).equals("webdav") || url.getSegments().get(0).equals("jcrwebdav")) {
                return null;
            }
        }

        Path path = new Path("/" + url.toString());

        // root path handling
        if (path.isRoot()) {
            if (handleHomePage) {
                final BrixNode node = getNodeForUriPath(path);
                return SitePlugin.get().getNodePluginForNode(node).respond(new BrixNodeModel(node), request.getRequestParameters());
            } else {
                return null;
            }
        }

        IRequestHandler handler = null;
        try {
            while (handler == null) {
                final BrixNode node = getNodeForUriPath(path);
                if (node != null) {
                    handler = SitePlugin.get().getNodePluginForNode(node).respond(new BrixNodeModel(node), request.getRequestParameters());
                }
                if (handler != null || path.toString().equals(".")) {
                    break;
                }
                path = path.parent();
                if (path.isRoot()) {
                    break;
                }
            }
        }
        catch (JcrException e) {
            logger.warn("JcrException caught due to incorrect url", e);
        }

        return handler;
    }

    @Override
    public int getCompatibilityScore(Request request) {
        Url url = request.getUrl();
        if (url.getSegments().size() > 0) {
            if (url.getSegments().get(0).equals((Application.get().getMapperContext().getNamespace()))) {
                // starts with wicket namespace - is an internal wicket url
                return 0;
            }
        }
        // bluff we can parse all segments - makes sure we run first
        return request.getUrl().getSegments().size();
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        // BT 20110602 - It's unclear why this garbage is necessary, why is this Mapper being called with an exception in the first place?
        Url url = null;
        if (requestHandler instanceof BrixNodePageRequestHandler || requestHandler instanceof BrixNodeRequestHandler) {
            url = encode(requestHandler);
        }
        return url;
    }

    // -------------------------- OTHER METHODS --------------------------

    public Url encode(IRequestHandler requestHandler) {
        if (requestHandler instanceof BrixNodeRequestHandler) {
            BrixNodeRequestHandler handler = (BrixNodeRequestHandler) requestHandler;
            PageInfo info = null;
            Page page = handler.getPage();
            if (page != null && !page.isPageStateless()) {
                info = new PageInfo(page.getPageId());
            }
            String nodeURL = handler.getNodeURL();
            return encode(nodeURL, handler.getPageParameters(), info);
        } else if (requestHandler instanceof ListenerInterfaceRequestHandler) {
            ListenerInterfaceRequestHandler target = (ListenerInterfaceRequestHandler) requestHandler;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            return encode(page);
        } else if (requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
            BookmarkableListenerInterfaceRequestHandler target = (BookmarkableListenerInterfaceRequestHandler) requestHandler;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            BrixNode node = page.getModelObject();
            PageInfo info = new PageInfo(page.getPageId());
            String componentPath = target.getComponent().getPageRelativePath();

            // remove the page id from component path, we don't really need it
            componentPath = componentPath.substring(componentPath.indexOf(':') + 1);
            String iface = componentPath; // + ":" + target.getInterfaceName();
            return encode(node, page.getBrixPageParameters(), info);
        } else if (requestHandler instanceof BookmarkablePageRequestHandler
                && ((BookmarkablePageRequestHandler) requestHandler).getPageClass().equals(HomePage.class)) {
            BrixNode node = ((BrixRequestCycleProcessor) RequestCycle.get().getActiveRequestHandler()).getNodeForUriPath(Path.ROOT);
            return encode(new BrixNodeRequestHandler(new BrixNodeModel(node)));
        } else {
            return null;
        }
    }

    private Url encode(BrixNodeWebPage page) {
        BrixNode node = page.getModelObject();
        PageInfo info = new PageInfo(page.getPageId());

        // This is a URL for redirect. Allow components to contribute state to
        // URL if they want to
        final BrixPageParameters parameters = page.getBrixPageParameters();
        page.visitChildren(PageParametersAware.class, new IVisitor<Component, PageParametersAware>() {
            @Override
            public void component(Component component, IVisit iVisit) {
                ((PageParametersAware) component).contributeToPageParameters(parameters);
            }
        });

        return encode(node, parameters, info);
    }

    private Url encode(BrixNode node, PageParameters parameters, PageInfo info) {
        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get().getActiveRequestHandler();
        return encode(processor.getUriPathForNode(node).toString(), parameters, info);
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
        }
        catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return string;
        }
    }

    /**
     * Resolves uri path to a {@link BrixNode}. By default this method uses {@link BrixConfig#getMapper()} to map the
     * uri to a node path.
     *
     * @param uriPath uri path
     * @return node that maps to the <code>uriPath</code> or <code>null</code> if none
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
     * Creates a uri path for the specified <code>node</code> By default this method uses {@link BrixConfig#getMapper()}
     * to map node path to a uri path.
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

    // -------------------------- INNER CLASSES --------------------------

    public static final class HomePage extends WebPage {
    }
}
