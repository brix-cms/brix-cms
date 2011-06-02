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

package org.brixcms.plugin.webdavurl;

import org.brixcms.auth.Action;
import org.brixcms.workspace.Workspace;

public class AccessWebDavUrlPluginAction implements Action {
    private final Workspace workspace;

    public AccessWebDavUrlPluginAction(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "AccessWebDavUrlPluginAction{" + "workspace=" + workspace + '}';
    }


    public Context getContext() {
        return Context.ADMINISTRATION;
    }
}
