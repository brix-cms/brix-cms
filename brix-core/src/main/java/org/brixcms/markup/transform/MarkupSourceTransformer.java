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

package org.brixcms.markup.transform;

import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.MarkupSourceWrapper;
import org.brixcms.markup.tag.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for {@link MarkupSource} transformers. This class allows to interact with the list of all {@link Item}s
 * rather than with one item at a time.
 *
 * @author Matej Knopp
 */
public abstract class MarkupSourceTransformer extends MarkupSourceWrapper {
    private List<Item> items = null;
    private Iterator<Item> iterator = null;

    public MarkupSourceTransformer(MarkupSource delegate) {
        super(delegate);
    }


    @Override
    public Item nextMarkupItem() {
        if (items == null) {
            List<Item> temp = new ArrayList<Item>();
            Item i = getDelegate().nextMarkupItem();
            while (i != null) {
                temp.add(i);
                i = getDelegate().nextMarkupItem();
            }
            items = transform(temp);

            if (items == null) {
                items = Collections.emptyList();
            }
            iterator = items.iterator();
        }

        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    /**
     * Performs the actual transformation.
     *
     * @param originalItems
     * @return
     */
    protected abstract List<Item> transform(List<Item> originalItems);
}
