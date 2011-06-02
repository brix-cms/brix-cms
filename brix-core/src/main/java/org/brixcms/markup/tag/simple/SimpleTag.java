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

package org.brixcms.markup.tag.simple;

import org.brixcms.markup.tag.Tag;

import java.util.Map;

/**
 * Simple implementation of the {@link Tag} interface.
 *
 * @author Matej Knopp
 */
public class SimpleTag implements Tag {
    private final Map<String, String> attributeMap;
    private final String name;
    private final Type type;

    public SimpleTag(String name, Type type, Map<String, String> attributeMap) {
        this.name = name;
        this.type = type;
        this.attributeMap = attributeMap;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
