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

import org.brixcms.Brix;
import org.brixcms.Path;

/**
 * Uri mapper that mounts cms urls on a certain prefix. Eg <code>new PrefixUriMapper(new Path("/docs/cms"))</code> will
 * mount all cms urls under the <code>/docs/cms/*</code> url space.
 *
 * @author ivaynberg
 */
public abstract class PrefixUriMapper implements UriMapper {
    private final Path prefix;

    /**
     * Constructor
     *
     * @param prefix absolute path to mount the cms uri space on
     */
    public PrefixUriMapper(Path prefix) {
        if (!prefix.isAbsolute()) {
            throw new IllegalArgumentException("Prefix must be an absolute path");
        }

        this.prefix = prefix;
    }


    /**
     * {@inheritDoc}
     */
    public Path getNodePathForUriPath(Path uriPath, Brix brix) {
        if (prefix.isAncestorOf(uriPath)) {
            // strip prefix from path
            return uriPath.toRelative(prefix).toAbsolute();
        } else if (prefix.equals(uriPath)) {
            // path is same as prefix, which equates to root
            return Path.ROOT;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Path getUriPathForNode(Path nodePath, Brix brix) {
        Path uriPath = prefix;

        if (!nodePath.isRoot()) {
            // nodePath is not root, we have to append it to prefix
            uriPath = prefix.append(nodePath.toRelative(Path.ROOT));
        }

        return uriPath;
    }

    public String rewriteStaticRelativeUrl(String url, String contextPrefix) {
        if (prefix.isRoot()) {
            return contextPrefix + url;
        } else {
            return contextPrefix + prefix.toRelative(Path.ROOT) + "/" + url;
        }
    }
}
