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

package org.brixcms.auth;

import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.workspace.Workspace;


public abstract class AbstractNodeAction extends AbstractAction {
    private final BrixNode node;

    public AbstractNodeAction(Context context, BrixNode node) {
        super(context);
        this.node = node;
    }

    public BrixNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "AbstractNodeAction{" + "node=" + node + "} " + super.toString();
    }

    public Workspace getWorkspace() {
        if (node == null) {
            return null;
        } else {
            String id = node.getSession().getWorkspace().getName();
            return node.getBrix().getWorkspaceManager().getWorkspace(id);
        }
    }
}
