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
 * Transformer that makes the markup usable with wicket panel. All &lt;head&gt; sections are grouped in one
 * &lt;wicket:head&gt; section and the rest of markup (except for the &lt;html&gt;tag if present) is grouped in a
 * &lt;wicket:panel&gt; section.
 * <p/>
 * Also removes all &lt;body&gt; and &lt;wicket:panel&gt; tags in markup.
 *
 * @author Matej Knopp
 */
public class PanelTransformer extends HeadTransformer {
    public PanelTransformer(MarkupSource delegate) {
        super(delegate);
    }


    @Override
    public String getDoctype() {
        return null;
    }

    @Override
    protected List<Item> transform(List<Item> originalItems) {
        List<Item> headContent = extractHeadContent(originalItems);
        List<Item> body = filter(transform(originalItems, null));

        Map<String, String> emptyMap = Collections.emptyMap();
        List<Item> result = new ArrayList<Item>();

        result.add(new SimpleTag("wicket:head", Tag.Type.OPEN, emptyMap));
        result.addAll(headContent);
        result.add(new SimpleTag("wicket:head", Tag.Type.CLOSE, emptyMap));

        result.add(new SimpleTag("wicket:panel", Tag.Type.OPEN, emptyMap));
        result.addAll(body);
        result.add(new SimpleTag("wicket:panel", Tag.Type.CLOSE, emptyMap));

        return result;
    }

    private List<Item> filter(List<Item> items) {
        List<Item> result = new ArrayList<Item>();

        for (Item i : items) {
            if (i instanceof Tag) {
                Tag tag = (Tag) i;
                if (shouldFilter(tag.getName())) {
                    continue;
                }
            }
            result.add(i);
        }

        return result;
    }

    private boolean shouldFilter(String tagName) {
        return "html".equals(tagName) || "body".equals(tagName) || "wicket:panel".equals(tagName);
    }
}
