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

package org.brixcms.plugin.site.page.global;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.admin.VariablesPanel;
import org.brixcms.workspace.Workspace;

public class GlobalVariablesPanel extends AbstractGlobalPanel {
    public GlobalVariablesPanel(String id, IModel<Workspace> workspaceModel) {
        super(id, workspaceModel);
    }

    @Override
    protected Panel newManagePanel(String id, IModel<BrixNode> containerNodeModel) {
        return new VariablesPanel(id, containerNodeModel);
    }
}
