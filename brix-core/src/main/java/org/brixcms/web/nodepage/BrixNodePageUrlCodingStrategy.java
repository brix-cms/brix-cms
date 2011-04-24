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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IRedirectListener;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkableListenerInterfaceRequestTarget;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.listener.ListenerInterfaceRequestTarget;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.Path;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.web.nodepage.BrixNodePageRequestTarget.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Matej Knopp
 */
public class BrixNodePageUrlCodingStrategy implements IRequestTargetUrlCodingStrategy {
// ------------------------------ FIELDS ------------------------------

    private static final Logger log = LoggerFactory.getLogger(BrixNodePageUrlCodingStrategy.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IRequestTargetUrlCodingStrategy ---------------------


    public String getMountPath() {
        return "--brix-internal";
    }

    public CharSequence encode(IRequestTarget requestTarget) {
        if (requestTarget instanceof BrixNodeRequestTarget) {
            BrixNodeRequestTarget target = (BrixNodeRequestTarget) requestTarget;
            PageInfo info = null;
            Page page = target.getPage();
            if (page != null && !page.isPageStateless()) {
                info = new PageInfo(page.getNumericId(), page.getCurrentVersionNumber(), page
                        .getPageMapName());
            }
            String nodeURL = target.getNodeURL();
            return encode(nodeURL, target.getParameters(), info, null);
        } else if (requestTarget instanceof ListenerInterfaceRequestTarget) {
            ListenerInterfaceRequestTarget target = (ListenerInterfaceRequestTarget) requestTarget;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            return encode(page);
        } else if (requestTarget instanceof BookmarkableListenerInterfaceRequestTarget) {
            BookmarkableListenerInterfaceRequestTarget target = (BookmarkableListenerInterfaceRequestTarget) requestTarget;
            BrixNodeWebPage page = (BrixNodeWebPage) target.getPage();
            BrixNode node = page.getModelObject();
            PageInfo info = new PageInfo(page.getNumericId(), page.getCurrentVersionNumber(), page
                    .getPageMapName());
            String componentPath = target.getComponentPath();

            // remove the page id from component path, we don't really need it
            componentPath = componentPath.substring(componentPath.indexOf(':') + 1);
            String iface = componentPath + ":" + target.getInterfaceName();
            return encode(node, page.getBrixPageParameters(), info, iface);
        } else if (requestTarget instanceof IBookmarkablePageRequestTarget
                && ((IBookmarkablePageRequestTarget) requestTarget).getPageClass().equals(
                HomePage.class)) {
            BrixNode node = ((BrixRequestCycleProcessor) RequestCycle.get().getProcessor())
                    .getNodeForUriPath(Path.ROOT);
            return encode(new BrixNodeRequestTarget(new BrixNodeModel(node)));
        } else {
            return null;
        }
    }

    public IRequestTarget decode(RequestParameters requestParameters) {
        throw new UnsupportedOperationException();
    }

    public boolean matches(IRequestTarget requestTarget) {
        if (requestTarget instanceof ListenerInterfaceRequestTarget) {
            ListenerInterfaceRequestTarget target = (ListenerInterfaceRequestTarget) requestTarget;
            return isBrixPage(target.getPage())
                    && target.getRequestListenerInterface().equals(IRedirectListener.INTERFACE);
        } else if (requestTarget instanceof BookmarkableListenerInterfaceRequestTarget) {
            BookmarkableListenerInterfaceRequestTarget target = (BookmarkableListenerInterfaceRequestTarget) requestTarget;
            return isBrixPage(target.getPage());
        } else if (requestTarget instanceof BrixNodeRequestTarget) {
            return true;
        }
        return false;
    }

    public boolean matches(String path, boolean caseSensitive) {
        return false;
    }

// -------------------------- OTHER METHODS --------------------------

    public IRequestTarget decode(RequestParameters requestParameters,
                                 final IModel<BrixNode> nodeModel) {
        PageInfo pageInfo = null;
        String query = requestParameters.getQueryString();
        final BrixPageParameters pageParameters = new BrixPageParameters();

        String iface = null;

        if (query != null) {
            pageInfo = extractPageInfo(query);
        }
        iface = addQueryStringParameters(pageParameters, pageInfo, requestParameters);

        addIndexedParameters(requestParameters.getPath(), pageParameters, nodeModel);

        BrixNodeWebPage page = null;
        PageFactory factory = null;

        if (pageInfo != null) {
            page = (BrixNodeWebPage) getPage(pageInfo);
        }

        if (page == null) {
            factory = new PageFactory() {
                public BrixNodeWebPage newPage() {
                    BrixNodeWebPage page = newPageInstance(nodeModel, pageParameters);
                    return page;
                }

                public BrixPageParameters getPageParameters() {
                    return pageParameters;
                }
            };
        } else {
            page.getBrixPageParameters().assign(pageParameters);
        }

        if (factory == null) {
            if (iface == null) {
                return new BrixNodePageRequestTarget(nodeModel, page);
            } else {
                return new BrixNodePageListenerRequestTarget(nodeModel, page, iface);
            }
        } else {
            if (iface == null) {
                return new BrixNodePageRequestTarget(nodeModel, factory);
            } else {
                return new BrixNodePageListenerRequestTarget(nodeModel, factory, iface);
            }
        }
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
                                            RequestParameters requestParameters) {
        final String pageInfoString = pageInfo != null ? pageInfo.toString() : null;

        String iface = null;

        for (Iterator i = requestParameters.getParameters().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String p = (String) entry.getKey();
            String v[] = (String[]) entry.getValue();
            if (p.equals(getInterfaceParameter()) && v.length > 0) {
                iface = v[0];
            } else if (p.equals(pageInfoString) && v.length == 1 && "".equals(v[0])) {
                // don't add this to page parameters
            } else {
                for (int j = 0; j < v.length; ++j) {
                    pageParameters.addQueryParam(p, v[j]);
                }
            }
        }

        return iface;
    }

    ;

    private String getInterfaceParameter() {
        return Brix.NS_PREFIX + "i";
    }

    private void addIndexedParameters(String requestPathString, BrixPageParameters parameters, IModel<BrixNode> nodeModel) {
        if (!requestPathString.startsWith("/"))
            requestPathString = "/" + requestPathString;

        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get()
                .getProcessor();
        Path nodePath = processor.getUriPathForNode(nodeModel.getObject());
        Path requestPath = new Path(requestPathString, false);

        if (nodePath.isAncestorOf(requestPath)) {
            Path remaining = new Path(requestPathString, false).toRelative(nodePath);
            int i = 0;
            for (String s : remaining) {
                parameters.setIndexedParam(i, urlDecode(s));
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

    private Page getPage(PageInfo info) {
        Page page;

        if (Strings.isEmpty(info.getPageMapName()) && Application.exists()
                && Application.get().getSessionSettings().isPageIdUniquePerSession()) {
            page = Session.get().getPage(info.getPageId().intValue(),
                    info.getVersionNumber() != null ? info.getVersionNumber().intValue() : 0);
        } else {
            page = Session.get().getPage(info.getPageMapName(), "" + info.getPageId(),
                    info.getVersionNumber() != null ? info.getVersionNumber().intValue() : 0);
        }

        if (page != null && isBrixPage(page)) {
            return page;
        } else {
            return null;
        }
    }

    private boolean isBrixPage(Page page) {
        return page instanceof BrixNodeWebPage;
    }

    protected BrixNodeWebPage newPageInstance(IModel<BrixNode> nodeModel,
                                              BrixPageParameters pageParameters) {
        throw new UnsupportedOperationException();
    }

    private CharSequence encode(BrixNodeWebPage page) {
        BrixNode node = (BrixNode) page.getModelObject();
        PageInfo info = new PageInfo(page.getNumericId(), page.getCurrentVersionNumber(), page
                .getPageMapName());

        // This is a URL for redirect. Allow components to contribute state to
        // URL if they want to
        final BrixPageParameters parameters = page.getBrixPageParameters();
        page.visitChildren(PageParametersAware.class, new Component.IVisitor<Component>() {
            public Object component(Component component) {
                ((PageParametersAware) component).contributeToPageParameters(parameters);
                return Component.IVisitor.CONTINUE_TRAVERSAL;
            }
        });

        return encode(node, parameters, info, null);
    }

    private CharSequence encode(BrixNode node, BrixPageParameters parameters, PageInfo info,
                                String iface) {
        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get()
                .getProcessor();
        return encode(processor.getUriPathForNode(node).toString(), parameters, info, iface);
    }

    private CharSequence encode(String nodeURL, BrixPageParameters parameters, PageInfo info,
                                String iface) {
        StringBuilder builder = new StringBuilder();

        if (nodeURL.startsWith("/")) {
            nodeURL = nodeURL.substring(1);
        }

        builder.append(urlEncodePath(new Path(nodeURL, false)));

        boolean skipFirstSlash = builder.charAt(builder.length() - 1) == '/';

        for (int i = 0; i < parameters.getIndexedParamsCount(); ++i) {
            if (!skipFirstSlash)
                builder.append('/');
            else
                skipFirstSlash = false;

            final StringValue value = parameters.getIndexedParam(i);
            final String s = value.toString();

            if (s != null)
                builder.append(urlEncode(s));
        }

        Set<String> keys = parameters.getQueryParamKeys();
        if (info != null || !keys.isEmpty()) {
            builder.append("?");
        }

        if (info != null) {
            builder.append(info.toString());
        }

        boolean first = (info == null);

        for (String key : keys) {
            List<StringValue> values = parameters.getQueryParams(key);
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

// -------------------------- INNER CLASSES --------------------------

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
        private final Integer versionNumber;
        private final String pageMapName;

        /**
         * Construct.
         *
         * @param pageId
         * @param versionNumber
         * @param pageMapName
         */
        public PageInfo(Integer pageId, Integer versionNumber, String pageMapName) {
            if ((pageId == null && (versionNumber != null || pageMapName == null))
                    || (versionNumber == null && (pageId != null || pageMapName == null))) {
                throw new IllegalArgumentException(
                        "Either both pageId and versionNumber must be null or none of them.");
            }
            this.pageId = pageId;
            this.versionNumber = versionNumber;
            this.pageMapName = pageMapName;
        }

        /**
         * @return
         */
        public Integer getPageId() {
            return pageId;
        }

        /**
         * @return
         */
        public Integer getVersionNumber() {
            return versionNumber;
        }

        /**
         * @return
         */
        public String getPageMapName() {
            return pageMapName;
        }

        private static char getPageInfoSeparator() {
            return '.';
        }

        /**
         * <ul> <li>pageId <li>pageId.version <li>pageMap (only in if pagemap starts with a letter) <li>.pageMap
         * <li>pageMap.pageId (only in if pageMap name starts with a letter) <li>pageMap.pageId.version </ul>
         */
        public String toString() {
            String pageMapName = this.pageMapName;

            // we don't need to encode the pageMapName when the pageId is unique
            // per session
            if (pageMapName != null && pageId != null && Application.exists()
                    && Application.get().getSessionSettings().isPageIdUniquePerSession()) {
                pageMapName = null;
            }

            AppendingStringBuffer buffer = new AppendingStringBuffer(5);

            final boolean pmEmpty = Strings.isEmpty(pageMapName);
            final boolean pmContainsLetter = !pmEmpty && !isNumber(pageMapName);

            if (pageId != null && pmEmpty && versionNumber.intValue() == 0) {
                // pageId
                buffer.append(pageId);
            } else if (pageId != null && pmEmpty && versionNumber.intValue() != 0) {
                // pageId.version
                buffer.append(pageId);
                buffer.append(getPageInfoSeparator());
                buffer.append(versionNumber);
            } else if (pageId == null && pmContainsLetter) {
                // pageMap (must start with letter)
                buffer.append(pageMapName);
            } else if (pageId == null && !pmEmpty && !pmContainsLetter) {
                // .pageMap
                buffer.append(getPageInfoSeparator());
                buffer.append(pageMapName);
            } else if (pmContainsLetter && pageId != null && versionNumber.intValue() == 0) {
                // pageMap.pageId (pageMap must start with a letter)
                buffer.append(pageMapName);
                buffer.append(getPageInfoSeparator());
                buffer.append(pageId);
            } else if (!pmEmpty && pageId != null) {
                // pageMap.pageId.pageVersion
                buffer.append(pageMapName);
                buffer.append(getPageInfoSeparator());
                buffer.append(pageId);
                buffer.append(getPageInfoSeparator());
                buffer.append(versionNumber);
            }

            return buffer.toString();
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
                return new PageInfo(Integer.valueOf(segments[0]), new Integer(0), null);
            } else if (segments.length == 2 && isNumber(segments[0]) && isNumber(segments[1])) {
                // pageId:pageVersion
                return new PageInfo(Integer.valueOf(segments[0]), Integer.valueOf(segments[1]),
                        null);
            } else if (segments.length == 1 && !isNumber(segments[0])) {
                // pageMap (starts with letter)
                return new PageInfo(null, null, segments[0]);
            } else if (segments.length == 2 && segments[0].length() == 0) {
                // .pageMap
                return new PageInfo(null, null, segments[1]);
            } else if (segments.length == 2 && !isNumber(segments[0]) && isNumber(segments[1])) {
                // pageMap.pageId (pageMap starts with letter)
                return new PageInfo(Integer.valueOf(segments[1]), new Integer(0), segments[0]);
            } else if (segments.length == 3) {
                if (segments[2].length() == 0 && isNumber(segments[1])) {
                    // we don't encode it like this, but we still should be able
                    // to parse it
                    // pageMapName.pageId.
                    return new PageInfo(Integer.valueOf(segments[1]), new Integer(0), segments[0]);
                } else if (isNumber(segments[1]) && isNumber(segments[2])) {
                    // pageMapName.pageId.pageVersion
                    return new PageInfo(Integer.valueOf(segments[1]), Integer.valueOf(segments[2]),
                            segments[0]);
                }
            }

            return null;
        }
    }
}
