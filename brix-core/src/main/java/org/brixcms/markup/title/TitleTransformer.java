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

package org.brixcms.markup.title;

import org.brixcms.Brix;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.tag.Tag;
import org.brixcms.markup.transform.MarkupSourceTransformer;
import org.brixcms.plugin.site.page.AbstractContainer;

import java.util.ArrayList;
import java.util.List;

public class TitleTransformer extends MarkupSourceTransformer {
    private static final String TAG_NAME = Brix.NS_PREFIX + "title";
    private final AbstractContainer container;

    public TitleTransformer(MarkupSource delegate, AbstractContainer container) {
        super(delegate);
        this.container = container;
    }

    @Override
    protected List<Item> transform(List<Item> originalItems) {
        List<Item> result = new ArrayList<Item>(originalItems.size());

        int skipLevel = 0;

        for (Item i : originalItems) {
            if (skipLevel > 0) {
                if (i instanceof Tag) {
                    Tag tag = (Tag) i;
                    if (tag.getType() == Tag.Type.OPEN) {
                        ++skipLevel;
                    } else if (tag.getType() == Tag.Type.CLOSE) {
                        --skipLevel;
                    }
                }
                continue;
            }

            if (i instanceof Tag) {
                Tag tag = (Tag) i;
                if (TAG_NAME.equals(tag.getName())) {
                    result.add(new TitleText(container));

                    if (tag.getType() == Tag.Type.OPEN) {
                        ++skipLevel;
                    }

                    continue;
                }
            }
            result.add(i);
        }

        return result;
    }
}
