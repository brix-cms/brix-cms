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

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.IModel;

public abstract class AbstractBrixTab extends AbstractTab implements IBrixTab {
    private final int priority;

    public AbstractBrixTab(IModel<String> title) {
        this(title, 0);
    }

    public AbstractBrixTab(IModel<String> title, int priority) {
        super(title);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
