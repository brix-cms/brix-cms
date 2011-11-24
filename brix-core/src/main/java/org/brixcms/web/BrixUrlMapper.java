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

/**
 *
 */
package org.brixcms.web;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.brixcms.BrixNodeModel;
import org.brixcms.Path;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.nodepage.BrixNodePageUrlMapper;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrixUrlMapper implements IRequestMapper {
    Logger logger = LoggerFactory.getLogger(BrixUrlMapper.class);

    /**
     * request cycle processor
     */
    private final BrixRequestCycleProcessor brixRequestCycleProcessor;

    /**
     * Constructor
     *
     * @param brixRequestCycleProcessor
     */
    public BrixUrlMapper(BrixRequestCycleProcessor brixRequestCycleProcessor) {
        this.brixRequestCycleProcessor = brixRequestCycleProcessor;
    }


    public IRequestHandler mapRequest(Request request) {
        String pathStr = request.getContextPath();

        IRequestHandler target = targetForPath(pathStr, request.getRequestParameters());

        if (target == null) {
            // 404 if node not found
            // return new
            // WebErrorCodeResponseTarget(HttpServletResponse.SC_NOT_FOUND,
            // "Resource
            // " + pathStr
            // + " not found");
            return null;
            // return new PageRequestTarget(new
            // ResourceNotFoundPage(pathStr));
        } else {
            return target;
        }
    }

    @Override
    public int getCompatibilityScore(Request request) {
        return 0;
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        return null;
    }

    public IRequestHandler targetForPath(String pathStr, IRequestParameters requestParameters) {
        if (!pathStr.startsWith("/")) {
            pathStr = "/" + pathStr;
        }

        // TODO: This is just a quick fix
        if (pathStr.startsWith("/webdav") || pathStr.startsWith("/jcrwebdav")) {
            return null;
        }

        Path path = decode(new Path(pathStr, false));

        IRequestHandler target = null;
        try {
            while (target == null) {
                final BrixNode node = this.brixRequestCycleProcessor.getNodeForUriPath(path);
                if (node != null) {
                    target = getSwitchTarget(node);
                    if (target == null) {
                        target = SitePlugin.get().getNodePluginForNode(node).respond(
                                new BrixNodeModel(node), new BrixPageParameters(requestParameters));
                    }
                }
                if (path.isRoot() || path.toString().equals(".")) {
                    break;
                }
                path = path.parent();
            }
        } catch (JcrException e) {
            Throwable iter = e;
            while (iter.getCause() != null) {
                iter = iter.getCause();
            }

            //TODO: shall we really rely on Jackrabbit implementation for this??? - trouble is that SPI is not covered by JCR2 intentionally
            //it's the last bit of jackrabbit in core!
//            if (iter instanceof MalformedPathException) {
//                logger.info("JcrException caught due to incorrect url",e);
//            } else {
//                throw e;
//            }
        }

        return target;
    }

    private Path decode(Path path) {
        StringBuilder builder = new StringBuilder(path.toString().length());
        boolean first = true;
        for (String s : path) {
            if (first) {
                first = false;
            } else {
                builder.append("/");
            }
            builder.append(BrixNodePageUrlMapper.urlDecode(s));
        }
        if (builder.length() == 0) {
            builder.append("/");
        }
        return new Path(builder.toString(), false);
    }

    private IRequestHandler getSwitchTarget(BrixNode node) {
        if (node instanceof BrixNode) {
            return SwitchProtocolRequestHandler.requireProtocol((node).getRequiredProtocol());
        } else {
            return null;
        }
    }
}
