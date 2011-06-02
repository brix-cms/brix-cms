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
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.tag.Tag;
import org.brixcms.markup.tag.simple.SimpleTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Transformer that gathers content from all &lt;head&gt; and &lt;wicket:head&gt; sections in markup. It groups the head
 * content and inserts it all into the first &lt;head&gt; section removing all other head sections.
 * <p/>
 * Also if there are nested &lt;body&gt; sections it removes the inner &lt;body&gt; sections leaving one one (outer)
 * &lt;body&gt; section.
 *
 * @author Matej Knopp
 */
public class HeadTransformer extends MarkupSourceTransformer {
    public HeadTransformer(MarkupSource delegate) {
        super(delegate);
    }

    @Override
    protected List<Item> transform(List<Item> originalItems) {
        List<Item> headContent = extractHeadContent(originalItems);
        return transform(originalItems, headContent);
    }

    /**
     * Returns all items from &lt;head&gt; and &lt;wicket:head&gt; sections in the markup.
     *
     * @param items
     * @return
     */
    protected List<Item> extractHeadContent(List<Item> items) {
        List<Item> result = new ArrayList<Item>();

        int headDepth = 0;

        for (Item i : items) {
            if (i instanceof Tag) {
                Tag tag = (Tag) i;
                if (isHead(tag)) {
                    if (tag.getType() == Tag.Type.OPEN) {
                        ++headDepth;
                    } else if (tag.getType() == Tag.Type.CLOSE) {
                        if (headDepth > 0) {
                            --headDepth;
                        }
                    }
                    continue;
                }
            }
            if (headDepth > 0) {
                result.add(i);
            }
        }

        return result;
    }

    private boolean isHead(Tag tag) {
        String name = tag.getName();
        return "head".equals(name) || "wicket:head".equals(name);
    }

    /**
     * Transform the given list of original items. If headContent is not null, the items from head content are placed in
     * the first head section of originalItems. All other head sections are removed. If head content is null, this
     * method removes all head sections from original items. This method also removes duplicate &lt;body&gt; tags.
     *
     * @param originalItems
     * @param headContent
     * @return
     */
    protected List<Item> transform(List<Item> originalItems, List<Item> headContent) {
        List<Item> result = new ArrayList<Item>();

        // do not add head section if the headContent is null
        boolean wasHead = headContent == null;
        int headDepth = 0;
        int bodyDepth = 0;

        for (Item i : originalItems) {
            if (i instanceof Tag) {
                Tag tag = (Tag) i;

                if (wasHead == false && (isHead(tag) || "body".equals(tag.getName()))) {
                    Map<String, String> emptyMap = Collections.emptyMap();
                    result.add(new SimpleTag("head", Tag.Type.OPEN, emptyMap));
                    result.addAll(headContent);
                    result.add(new SimpleTag("head", Tag.Type.CLOSE, null));
                    if ("body".equals(tag.getName())) {
                        bodyDepth++;
                        result.add(tag);
                    }
                    wasHead = true;
                    if (isHead(tag) && (tag.getType() == Tag.Type.OPEN)) {
                        ++headDepth;
                    }
                    continue;
                }

                if ("body".equals(tag.getName())) {
                    if (tag.getType() == Tag.Type.OPEN) {
                        if (bodyDepth == 0) {
                            result.add(tag);
                        }
                        ++bodyDepth;
                    } else if (tag.getType() == Tag.Type.CLOSE) {
                        --bodyDepth;
                        if (bodyDepth == 0) {
                            result.add(tag);
                        }
                    }
                    continue;
                }

                if (isHead(tag)) {
                    if (tag.getType() == Tag.Type.OPEN) {
                        ++headDepth;
                    } else if (tag.getType() == Tag.Type.CLOSE) {
                        if (headDepth > 0) {
                            --headDepth;
                        }
                    }
                    continue;
                }
            }
            if (headDepth == 0) {
                result.add(i);
            }
        }

        return result;
    }
}
