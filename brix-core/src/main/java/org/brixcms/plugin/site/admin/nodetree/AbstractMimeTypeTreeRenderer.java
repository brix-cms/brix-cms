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

package org.brixcms.plugin.site.admin.nodetree;

import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.ResourceNode;

import java.util.Arrays;
import java.util.List;

/**
 * A renderer that decides if it is rendering the node based on mime type
 *
 * @author Jeremy Thomerson
 */
public abstract class AbstractMimeTypeTreeRenderer extends AbstractNodeTreeRenderer {
    private static final long serialVersionUID = 1L;

    private final List<String> mimeTypes;
    private final List<String> mimeTypePrefixes;

    protected AbstractMimeTypeTreeRenderer(String[] mimeTypes) {
        this(mimeTypes, new String[0]);
    }

    protected AbstractMimeTypeTreeRenderer(String[] mimeTypes, String[] mimeTypePrefixes) {
        this.mimeTypes = Arrays.asList(mimeTypes);
        this.mimeTypePrefixes = Arrays.asList(mimeTypePrefixes);
    }

    @Override
    protected final Class<? extends BrixNode> getNodeClass() {
        return ResourceNode.class;
    }

    @Override
    protected boolean isForThisNode(BrixNode bn) {
        return super.isForThisNode(bn) && isCorrectMimeType(bn);
    }

    protected boolean isCorrectMimeType(BrixNode bn) {
        ResourceNode rn = (ResourceNode) bn;
        if (bn == null || rn.getMimeType() == null) {
            return false;
        }
        // TODO: perhaps should only test on lower case here?
        // if so, need to lowercase all incoming types in the constructor
        String type = rn.getMimeType();
        int slash = type.indexOf('/');
        if (slash < 0) {
            return mimeTypePrefixes.contains(type);
        } else {
            String prefix = type.substring(0, slash);
            return mimeTypes.contains(type) || mimeTypePrefixes.contains(prefix);
        }
    }
}
