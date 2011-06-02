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

package org.brixcms.web.nodepage.toolbar;

import org.brixcms.auth.Action;

/**
 * Action that determines whether or not a user can see the workspace switching toolbar when viewing a site.
 *
 * @author igor.vaynberg
 */
public class AccessWorkspaceSwitcherToolbarAction implements Action {

    public Context getContext() {
        return Context.PRESENTATION;
    }
}
