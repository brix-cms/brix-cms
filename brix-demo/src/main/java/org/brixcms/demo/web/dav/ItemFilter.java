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

package org.brixcms.demo.web.dav;

import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;
import org.brixcms.jcr.wrapper.BrixNode;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class ItemFilter extends DefaultItemFilter {
    public ItemFilter() {

    }


    @Override
    public boolean isFilteredItem(Item item) {
        try {
            if (item instanceof Node) {
                Node node = (Node) item;
                if (node.isNodeType(BrixNode.JCR_MIXIN_BRIX_HIDDEN)) {
                    return true;
                }
            } else {
                String name = item.getName();
                if (name.startsWith("brix:")) {
                    return true;
                }
            }
        } catch (RepositoryException e) {
            return true;
        }

        return super.isFilteredItem(item);
    }
}
