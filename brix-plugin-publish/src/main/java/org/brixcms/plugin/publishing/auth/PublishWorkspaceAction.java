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

package org.brixcms.plugin.publishing.auth;

import org.brixcms.auth.AbstractWorkspaceAction;
import org.brixcms.workspace.Workspace;

public class PublishWorkspaceAction extends AbstractWorkspaceAction {
    private final String targetState;

    public PublishWorkspaceAction(Context context, Workspace workspace, String targetState) {
        super(context, workspace);
        this.targetState = targetState;
    }

    public String getTargetState() {
        return targetState;
    }

    @Override
    public String toString() {
        return "PublishWorkspaceAction{" + "targetState='" + targetState + '\'' + "} " + super.toString();
    }
}
