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

import org.brixcms.markup.tag.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains list of generated markup items and expiration token.
 * <p/>
 * TODO: Consider optimizing the list of items by grouping static items together (as text).
 *
 * @author Matej Knopp
 */
class GeneratedMarkup {
    final List<Item> items;

    final Object expirationToken;

    final String doctype;

    /**
     * Creates new {@link GeneratedMarkup} instance from given {@link MarkupSource}.
     *
     * @param markupSource
     */
    public GeneratedMarkup(MarkupSource markupSource) {
        if (markupSource == null) {
            throw new IllegalArgumentException("Argument 'markupSource' may not be null.");
        }
        this.expirationToken = markupSource.getExpirationToken();
        items = new ArrayList<Item>();
        Item item = markupSource.nextMarkupItem();
        while (item != null) {
            items.add(item);
            item = markupSource.nextMarkupItem();
        }
        this.doctype = markupSource.getDoctype();
    }
}
