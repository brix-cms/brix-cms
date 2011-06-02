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

import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.tag.Text;

import java.util.Arrays;
import java.util.Collection;

public class VariableText implements Text, VariableKeyProvider {
    private final BrixNodeModel pageNodeModel;
    private final String key;

    public VariableText(BrixNode pageNode, String key) {
        this.pageNodeModel = new BrixNodeModel(pageNode);
        this.key = key;
        this.pageNodeModel.detach();
    }


    public String getText() {
        BrixNode node = new BrixNodeModel(pageNodeModel).getObject();
        if (node instanceof VariableValueProvider) {
            String value = ((VariableValueProvider) node).getVariableValue(key);
            return value != null ? value : "[" + key + "]";
        } else {
            return "Couldn't resolve variable '" + key + "'";
        }
    }


    public Collection<String> getVariableKeys() {
        return Arrays.asList(new String[]{key});
    }
}
