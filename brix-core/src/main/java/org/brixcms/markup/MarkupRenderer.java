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

import org.brixcms.markup.tag.Comment;
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.tag.Tag;
import org.brixcms.markup.tag.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Renders the given list of items into an XHTML markup.
 *
 * @author Matej Knopp
 */
public abstract class MarkupRenderer {
    private final String doctype;
    private final List<Item> items;

    MarkupRenderer(List<Item> items, String doctype) {
        this.items = items;
        this.doctype = doctype;
    }

    /**
     * Renders the items.
     *
     * @return XHTML string
     */
    public String render() {
        StringBuilder builder = new StringBuilder();

        if (doctype != null) {
            builder.append(doctype);
        }

        for (Item item : items) {
            render(item, builder);
        }

        return builder.toString();
    }

    private void render(Item item, StringBuilder builder) {
        if (item instanceof Tag) {
            render((Tag) item, builder);
        } else if (item instanceof Text) {
            render((Text) item, builder);
        } else if (item instanceof Comment) {
            render((Comment) item, builder);
        } else {
            throw new IllegalStateException("Unknown item type '" + item.getClass().getName() + "'");
        }
    }

    private void render(Tag tag, StringBuilder builder) {
        if (tag.getType() == Tag.Type.CLOSE) {
            builder.append("</");
        } else {
            builder.append("<");
        }
        builder.append(tag.getName());

        if (tag.getType() == Tag.Type.OPEN || tag.getType() == Tag.Type.OPEN_CLOSE) {
            Map<String, String> attributeMap = new HashMap<String, String>(tag.getAttributeMap());
            postprocessTagAttributes(tag, attributeMap);
            for (Entry<String, String> e : attributeMap.entrySet()) {
                builder.append(" ");
                builder.append(e.getKey());
                builder.append("=\"");
                builder.append(e.getValue());
                builder.append("\"");
            }
        }

        if (tag.getType() == Tag.Type.OPEN_CLOSE) {
            builder.append(" /");
        }

        builder.append(">");
    }

    abstract void postprocessTagAttributes(Tag tag, Map<String, String> attributes);

    private void render(Text text, StringBuilder builder) {
        builder.append(text.getText());
    }

    private void render(Comment comment, StringBuilder builder) {
        builder.append("<!-- ");
        builder.append(comment.getText());
        builder.append(" -->");
    }
}
