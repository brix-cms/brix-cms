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

package org.brixcms.web.nodepage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.Path;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.web.nodepage.BrixNodePageRequestHandler.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matej Knopp
 * 
 *         FIXME wicket-15: a lot of ideas from here stil need to be ported to
 *         BrixRequestMapper, or the latter should be merged into the former.
 *         two can enter, one may leave.
 */
public class BrixNodePageUrlMapper implements IRequestMapper {
    private static final Logger log = LoggerFactory.getLogger(BrixNodePageUrlMapper.class);



    public String getMountPath() {
        return "--brix-internal";
    }

    public CharSequence encode(IRequestHandler requestTarget) {
        if (requestTarget instanceof BrixNodeRequestHandler) {
            BrixNodeRequestHandler handler = (BrixNodeRequestHandler) requestTarget;
            PageInfo info = null;
            Page page = handler.getPage();
            if (page != null && !page.isPageStateless()) {
                info = new PageInfo(page.getPageId());
            }
            String nodeURL = handler.getNodeURL();
            return encode(nodeURL, handler.getPageParameters(), info, null);
        } else if (requestTarget instanceof ListenerInterfaceRequestHandler) {
            ListenerInterfaceRequestHandler target = (ListenerInterfaceRequestHandler) requestTarget;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            return encode(page);
        } else if (requestTarget instanceof BookmarkableListenerInterfaceRequestHandler) {
            BookmarkableListenerInterfaceRequestHandler target = (BookmarkableListenerInterfaceRequestHandler) requestTarget;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            BrixNode node = page.getModelObject();
            PageInfo info = new PageInfo(page.getPageId());
            String componentPath = target.getComponent().getPageRelativePath();

            // remove the page id from component path, we don't really need it
            componentPath = componentPath.substring(componentPath.indexOf(':') + 1);
            String iface = componentPath; // + ":" + target.getInterfaceName();
            return encode(node, page.getBrixPageParameters(), info, iface);
        } else if (requestTarget instanceof BookmarkablePageRequestHandler
                && ((BookmarkablePageRequestHandler) requestTarget).getPageClass().equals(
                HomePage.class)) {
            BrixNode node = ((BrixRequestCycleProcessor) RequestCycle.get().getActiveRequestHandler())
                    .getNodeForUriPath(Path.ROOT);
            return encode(new BrixNodeRequestHandler(new BrixNodeModel(node)));
        } else {
            return null;
        }
    }

    public IRequestHandler decode(IRequestParameters requestParameters) {
        throw new UnsupportedOperationException();
    }

    public boolean matches(IRequestHandler requestTarget) {
        if (requestTarget instanceof ListenerInterfaceRequestHandler) {
            ListenerInterfaceRequestHandler target = (ListenerInterfaceRequestHandler) requestTarget;
            return isBrixPage(target.getPage())
                    /*&& target.getRequestListenerInterface().equals(IRedirectListener.INTERFACE)*/;
        } else if (requestTarget instanceof BookmarkableListenerInterfaceRequestHandler) {
            BookmarkableListenerInterfaceRequestHandler target = (BookmarkableListenerInterfaceRequestHandler) requestTarget;
            return isBrixPage(target.getPage());
        } else if (requestTarget instanceof BrixNodeRequestHandler) {
            return true;
        }
        return false;
    }

    public boolean matches(String path, boolean caseSensitive) {
        return false;
    }

    public IRequestHandler decode(final BrixPageParameters pageParameters, final IModel<BrixNode> nodeModel) {

        PageFactory factory = null;

        factory = new PageFactory() {
            public BrixNodeWebPage newPage() {
                BrixNodeWebPage page = newPageInstance(nodeModel, pageParameters);
                return page;
            }

            public BrixPageParameters getPageParameters() {
                return pageParameters;
            }
        };
        return new BrixNodePageRequestHandler(nodeModel, factory);
    }

    private PageInfo extractPageInfo(String query) {
        // try to extract page info
        int i1 = query.indexOf("=");
        int i2 = query.indexOf("&");
        String part = null;

        if (i1 == -1 && i2 == -1) {
            part = query;
        } else if (i2 != -1 && (i1 == -1 || i2 < i1)) {
            part = query.substring(0, i2);
        }

        return PageInfo.parsePageInfo(part);
    }

    @SuppressWarnings("unchecked")
    private String addQueryStringParameters(BrixPageParameters pageParameters, PageInfo pageInfo,
                                            IRequestParameters requestParameters) {
        final String pageInfoString = pageInfo != null ? pageInfo.toString() : null;

        String iface = null;

        for (String name : requestParameters.getParameterNames()) {
            List<StringValue> values = requestParameters.getParameterValues(name);
            if (name.equals(getInterfaceParameter()) && values.size() > 0) {
                iface = values.get(0).toString();
            } else if (name.equals(pageInfoString) && values.size() == 1 && "".equals(values.get(0).toString())) {
                // don't add this to page parameters
            } else {
                for (StringValue value : values) {
                    pageParameters.set(name, value);
                }
            }
        }

        return iface;
    }

    private String getInterfaceParameter() {
        return Brix.NS_PREFIX + "i";
    }

    private void addIndexedParameters(String requestPathString, BrixPageParameters parameters, IModel<BrixNode> nodeModel) {
        if (!requestPathString.startsWith("/"))
            requestPathString = "/" + requestPathString;

        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get()
                .getActiveRequestHandler();
        Path nodePath = processor.getUriPathForNode(nodeModel.getObject());
        Path requestPath = new Path(requestPathString, false);

        if (nodePath.isAncestorOf(requestPath)) {
            Path remaining = new Path(requestPathString, false).toRelative(nodePath);
            int i = 0;
            for (String s : remaining) {
                parameters.set(i, urlDecode(s));
                ++i;
            }
        }
    }

    /**
     * Returns a decoded value of the given value
     *
     * @param value
     * @return Decodes the value
     */
    public static String urlDecode(String value) {
        try {
            value = URLDecoder.decode(value, Application.get().getRequestCycleSettings()
                    .getResponseRequestEncoding());
        } catch (UnsupportedEncodingException ex) {
            log.error("error decoding parameter", ex);
        }
        return value;
    }

    private IManageablePage getPage(PageInfo info) {
        IManageablePage page;

        if (/*Strings.isEmpty(info.getPageMapName()) &&*/ Application.exists()
                /*&& Application.get().getSessionSettings().isPageIdUniquePerSession()*/) {
            page = Session.get().getPageManager().getPage(info.getPageId()/*,
                    info.getVersionNumber() != null ? info.getVersionNumber() : 0*/);
        } else {
            page = Session.get().getPageManager().getPage(/*info.getPageMapName(), "" +*/ info.getPageId()/*,
                    info.getVersionNumber() != null ? info.getVersionNumber() : 0*/);
        }

        if (page != null && isBrixPage(page)) {
            return page;
        } else {
            return null;
        }
    }

    private boolean isBrixPage(IManageablePage page) {
        return page instanceof BrixNodeWebPage;
    }

    protected BrixNodeWebPage newPageInstance(IModel<BrixNode> nodeModel,
                                              BrixPageParameters pageParameters) {
        throw new UnsupportedOperationException();
    }

    private CharSequence encode(BrixNodeWebPage page) {
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

        return encode(node, parameters, info, null);
    }

    private CharSequence encode(BrixNode node, PageParameters parameters, PageInfo info,
                                String iface) {
        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get()
                .getActiveRequestHandler();
        return encode(processor.getUriPathForNode(node).toString(), parameters, info, iface);
    }

    private CharSequence encode(String nodeURL, PageParameters parameters, PageInfo info,
                                String iface) {
        StringBuilder builder = new StringBuilder();

        if (nodeURL.startsWith("/")) {
            nodeURL = nodeURL.substring(1);
        }

        builder.append(urlEncodePath(new Path(nodeURL, false)));

        boolean skipFirstSlash = builder.charAt(builder.length() - 1) == '/';

        for (int i = 0; i < parameters.getIndexedCount(); ++i) {
            if (!skipFirstSlash)
                builder.append('/');
            else
                skipFirstSlash = false;

            final StringValue value = parameters.get(i);
            final String s = value.toString();

            if (s != null)
                builder.append(urlEncode(s));
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

        if (iface != null) {
            if (!first) {
                builder.append("&");
            }
            builder.append(getInterfaceParameter());
            builder.append("=");
            builder.append(iface);
        }

        return builder.toString();
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
            return URLEncoder.encode(string, Application.get().getRequestCycleSettings()
                    .getResponseRequestEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return string;
        }
    }

    public static final class HomePage extends WebPage {
    }

    /**
     * Possible string representation of PageInfo: <ul> <li>pageId <li>pageId.version <li>pageMap (only if pageMap starts
     * with a letter) <li>.pageMap <li>pageMap.pageId.version <li>pageMap.pageId (only if pageMap name starts with a
     * letter) </ul>
     *
     * @author Matej Knopp
     */
    protected static class PageInfo {
        private final Integer pageId;
//        private final Integer versionNumber;
//        private final String pageMapName;

        /**
         * Construct.
         *
         * @param pageId
         *
         */
        public PageInfo(Integer pageId) {
            if ((pageId == null /*&& (versionNumber != null || pageMapName == null))
                    || (versionNumber == null && (pageId != null || pageMapName == null)*/)) {
                throw new IllegalArgumentException(
                        "Either both pageId and versionNumber must be null or none of them.");
            }
            this.pageId = pageId;
//            this.versionNumber = versionNumber;
//            this.pageMapName = pageMapName;
        }

        /**
         * @return
         */
        public Integer getPageId() {
            return pageId;
        }

//        /**
//         * @return
//         */
//        public Integer getVersionNumber() {
//            return versionNumber;
//        }
//
//        /**
//         * @return
//         */
//        public String getPageMapName() {
//            return pageMapName;
//        }

        private static char getPageInfoSeparator() {
            return '.';
        }

        /**
         * <ul> <li>pageId <li>pageId.version <li>pageMap (only in if pagemap starts with a letter) <li>.pageMap
         * <li>pageMap.pageId (only in if pageMap name starts with a letter) <li>pageMap.pageId.version </ul>
         */
//        public String toString() {
//            String pageMapName = this.pageMapName;
//
//            // we don't need to encode the pageMapName when the pageId is unique
//            // per session
//            if (pageMapName != null && pageId != null && Application.exists()
//                    /*&& Application.get().getSessionSettings().isPageIdUniquePerSession()*/) {
//                pageMapName = null;
//            }
//
//            AppendingStringBuffer buffer = new AppendingStringBuffer(5);
//
//            final boolean pmEmpty = Strings.isEmpty(pageMapName);
//            final boolean pmContainsLetter = !pmEmpty && !isNumber(pageMapName);
//
//            if (pageId != null && pmEmpty && versionNumber.intValue() == 0) {
//                // pageId
//                buffer.append(pageId);
//            } else if (pageId != null && pmEmpty && versionNumber.intValue() != 0) {
//                // pageId.version
//                buffer.append(pageId);
//                buffer.append(getPageInfoSeparator());
//                buffer.append(versionNumber);
//            } else if (pageId == null && pmContainsLetter) {
//                // pageMap (must start with letter)
//                buffer.append(pageMapName);
//            } else if (pageId == null && !pmEmpty && !pmContainsLetter) {
//                // .pageMap
//                buffer.append(getPageInfoSeparator());
//                buffer.append(pageMapName);
//            } else if (pmContainsLetter && pageId != null && versionNumber.intValue() == 0) {
//                // pageMap.pageId (pageMap must start with a letter)
//                buffer.append(pageMapName);
//                buffer.append(getPageInfoSeparator());
//                buffer.append(pageId);
//            } else if (!pmEmpty && pageId != null) {
//                // pageMap.pageId.pageVersion
//                buffer.append(pageMapName);
//                buffer.append(getPageInfoSeparator());
//                buffer.append(pageId);
//                buffer.append(getPageInfoSeparator());
//                buffer.append(versionNumber);
//            }
//
//            return buffer.toString();
//        }

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

        /**
         * <ul> <li>pageId <li>pageId.version <li>pageMap (only in if pagemap starts with a letter) <li>.pageMap
         * <li>pageMap.pageId (only in if pageMap name starts with a letter) <li>pageMap.pageId.version </ul>
         *
         * @param src
         * @return
         */
        public static PageInfo parsePageInfo(String src) {
            if (src == null || src.length() == 0) {
                return null;
            }

            String segments[] = Strings.split(src, getPageInfoSeparator());

            if (segments.length > 3) {
                return null;
            }

            // go through the segments to determine if they don't contain
            // invalid characters
            for (int i = 0; i < segments.length; ++i) {
                for (int j = 0; j < segments[i].length(); ++j) {
                    char c = segments[i].charAt(j);
                    if (!Character.isLetterOrDigit(c) && c != '-' && c != '_') {
                        return null;
                    }
                }
            }

            if (segments.length == 1 && isNumber(segments[0])) {
                // pageId
                return new PageInfo(Integer.valueOf(segments[0]));
            } else if (segments.length == 2 && isNumber(segments[0]) && isNumber(segments[1])) {
                // pageId:pageVersion
                return new PageInfo(Integer.valueOf(segments[0])
                );
            } else if (segments.length == 1 && !isNumber(segments[0])) {
                // pageMap (starts with letter)
                return new PageInfo(null);
            } else if (segments.length == 2 && segments[0].length() == 0) {
                // .pageMap
                return new PageInfo(null);
            } else if (segments.length == 2 && !isNumber(segments[0]) && isNumber(segments[1])) {
                // pageMap.pageId (pageMap starts with letter)
                return new PageInfo(Integer.valueOf(segments[1]));
            } else if (segments.length == 3) {
                if (segments[2].length() == 0 && isNumber(segments[1])) {
                    // we don't encode it like this, but we still should be able
                    // to parse it
                    // pageMapName.pageId.
                    return new PageInfo(Integer.valueOf(segments[1]));
                } else if (isNumber(segments[1]) && isNumber(segments[2])) {
                    // pageMapName.pageId.pageVersion
                    return new PageInfo(Integer.valueOf(segments[1])
                    );
                }
            }

            return null;
        }
    }

    @Override
    public int getCompatibilityScore(Request request) {
        log.trace("Entering getCompatibilityScore");
        return 0;
    }

    @Override
    public IRequestHandler mapRequest(Request request) {
        log.trace("Entering mapRequest");
        return null;
    }

    @Override
    public Url mapHandler(IRequestHandler iRequestHandler) {
        log.trace("Entering mapHandler");
        return Url.parse(encode(iRequestHandler).toString());
    }

}
