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

package org.brixcms.plugin.site.auth;

import org.brixcms.auth.AbstractNodeAction;
import org.brixcms.jcr.wrapper.BrixNode;

public class ConvertNodeAction extends AbstractNodeAction {
// ------------------------------ FIELDS ------------------------------

    private final String targetType;

// --------------------------- CONSTRUCTORS ---------------------------

    public ConvertNodeAction(Context context, BrixNode node, String targetType) {
        super(context, node);
        this.targetType = targetType;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "ConvertNodeAction{" + "targetType='" + targetType + '\'' + "} " + super.toString();
    }

// -------------------------- OTHER METHODS --------------------------

    public String getTargetNodeType() {
        return targetType;
    }
}
