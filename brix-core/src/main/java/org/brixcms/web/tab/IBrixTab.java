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

package org.brixcms.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

/**
 * Special kind of tab that supports tab ordering. Each BrixTab has a priority that affects the order of displayed
 * tabs.
 *
 * @author Matej Knopp
 */
public interface IBrixTab extends ITab {
    /**
     * Returns the priority of this tab. Tabs with bigger priority go before tabs with lower priority.
     *
     * @return
     */
    public int getPriority();
}
