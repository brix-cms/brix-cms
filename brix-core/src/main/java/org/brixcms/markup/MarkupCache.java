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

package org.brixcms.markup;

import org.apache.wicket.MarkupContainer;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.IGenericComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains {@link GeneratedMarkup} instances associated with {@link MarkupContainer}s. The {@link MarkupContainer}s
 * must also implement {@link MarkupSourceProvider} so that the cache can check if the {@link GeneratedMarkup} is still
 * valid and generate new one in case it is not.
 *
 * @author Matej Knopp
 */
public class MarkupCache {
    private Map<String, GeneratedMarkup> map = new ConcurrentHashMap<String, GeneratedMarkup>();

    /**
     * Returns the {@link GeneratedMarkup} instance for given container. The container must implement {@link
     * MarkupSourceProvider}. If the {@link GeneratedMarkup} instance is expired or not found, new {@link
     * GeneratedMarkup} instance is generated and stored in the cache.
     *
     * @param container
     * @return
     */
    public GeneratedMarkup getMarkup(IGenericComponent<BrixNode> container) {
        if (!(container instanceof MarkupSourceProvider)) {
            throw new IllegalArgumentException("Argument 'container' must implement MarkupSourceProvider");
        }
        MarkupSourceProvider provider = (MarkupSourceProvider) container;
        final String key = getKey(container);
        GeneratedMarkup markup = map.get(key);
        if (markup != null) {
            // check if markup is still valid
            if (provider.getMarkupSource().isMarkupExpired(markup.expirationToken)) {
                markup = null;
            }
        }
        if (markup == null) {
            markup = new GeneratedMarkup(provider.getMarkupSource());
            map.put(key, markup);
        }
        return markup;
    }

    /**
     * Returns the string representation of cache key for the given container.
     *
     * @param container
     * @return
     */
    private String getKey(IGenericComponent<BrixNode> container) {
        BrixNode node = container.getModelObject();
        String nodeId = "";
        if (node != null) {
            if (node.isNodeType("mix:referenceable")) {
                nodeId = node.getIdentifier();
            } else {
                nodeId = node.getPath();
            }
        }
        String workspace = node.getSession().getWorkspace().getName();
        return container.getClass().getName() + "-" + workspace + "-" + nodeId;
    }
}
