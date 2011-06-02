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

package org.brixcms.markup.variable;

import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.tag.ComponentTag;
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.tag.Tag;
import org.brixcms.markup.tag.simple.SimpleTag;
import org.brixcms.markup.transform.MarkupSourceTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VariableTransformer extends MarkupSourceTransformer {
    private static final String VAR_TAG_NAME = Brix.NS_PREFIX + "var";

    int skipLevel = 0;
    private final BrixNode pageNode;

    public VariableTransformer(MarkupSource delegate, BrixNode pageNode) {
        super(delegate);
        this.pageNode = pageNode;
    }

    @Override
    protected List<Item> transform(List<Item> originalItems) {
        List<Item> result = new ArrayList<Item>(originalItems.size());
        for (Item i : originalItems) {
            if (i instanceof Tag) {
                Item item = processTag((Tag) i);
                if (item != null) {
                    result.add(item);
                }
            } else if (skipLevel == 0) {
                result.add(i);
            }
        }
        return result;
    }

    private Item processTag(Tag tag) {
        if (skipLevel > 0) {
            if (tag.getType() == Tag.Type.OPEN) {
                ++skipLevel;
            } else if (tag.getType() == Tag.Type.CLOSE) {
                --skipLevel;
            }
            return null;
        }

        String name = tag.getName();
        if (VAR_TAG_NAME.equals(name)) {
            if (tag.getType() == Tag.Type.OPEN) {
                ++skipLevel;
            }
            String key = tag.getAttributeMap().get("key");
            return new VariableText(pageNode, key);
        } else if (tag.getClass().equals(SimpleTag.class)) {
            // simple tag is guaranteed to be "static" so we will only wrap it
            // if really needed
            return processSimpleTag(tag);
        } else if (tag instanceof ComponentTag) {
            return new VariableComponentTag(pageNode, (ComponentTag) tag);
        } else {
            return new VariableTag(pageNode, tag);
        }
    }

    private Item processSimpleTag(Tag tag) {
        Map<String, String> attributes = tag.getAttributeMap();
        if (attributes != null) {
            for (String s : attributes.values()) {
                if (VariableTag.getKey(s) != null) {
                    return new VariableTag(pageNode, tag);
                }
            }
        }
        return tag;
    }
}
