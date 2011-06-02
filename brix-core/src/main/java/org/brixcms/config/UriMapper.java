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

package org.brixcms.config;

import org.apache.wicket.request.cycle.RequestCycle;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.workspace.Workspace;

/**
 * Used to translate between the web's URI space and jcr's node space
 *
 * @author ivaynberg
 */
public interface UriMapper {
    /**
     * Translates a uri path into a node path. This method is used to resolve a jcr node from a url.
     *
     * @param uriPath uri path
     * @param brix    brix instance
     * @return absolute path of node this uri maps to, or null if none
     */
    public Path getNodePathForUriPath(Path uriPath, Brix brix);

    /**
     * Translates a node path into uri path. This method is used to generate urls that map to a specific node.
     *
     * @param nodePath node path
     * @param brix     brix instance
     * @return uri path that represents node path
     */
    public Path getUriPathForNode(Path nodePath, Brix brix);

    /**
     * Resolves JCR workspace to be used for the specified request.
     *
     * @param requestCycle
     * @return JCR workspace or <code>null</code> if no suitable one is found
     */
    public Workspace getWorkspaceForRequest(RequestCycle requestCycle, Brix brix);

    /**
     * Rewrites relative urls found in static markup to be context-relative.
     *
     * @param url           relative url to rewrite
     * @param contextPrefix prefix that will make url context-relative, eg <code>../../</code>
     * @return rewritten url
     * @see IRequestCodingStrategy#rewriteStaticRelativeUrl(String)
     */
    public String rewriteStaticRelativeUrl(String url, String contextPrefix);
}
