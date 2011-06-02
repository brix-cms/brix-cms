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

import org.brixcms.auth.AbstractAction;
import org.brixcms.jcr.api.JcrNode;

/**
 * Action used to filter the list of available node types upon node creation.
 *
 * @author Matej Knopp
 */
public class SelectNewNodeTypeAction extends AbstractAction {
    private final JcrNode parentNode;
    private final String nodeType;

    public SelectNewNodeTypeAction(Context context, JcrNode parentNode, String nodeType) {
        super(context);
        this.parentNode = parentNode;
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public JcrNode getParentNode() {
        return parentNode;
    }

    @Override
    public String toString() {
        return "SelectNewNodeTypeAction{" + "nodeType='" + nodeType + '\'' + ", parentNode=" + parentNode + "} " + super.toString();
    }
}
