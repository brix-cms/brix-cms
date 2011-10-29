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

package org.brixcms.web.nodepage;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebResponse;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.web.nodepage.toolbar.ToolbarBehavior;

public class ResourceNotFoundPage extends WebPage {
    public ResourceNotFoundPage() {
        this("");
    }

    public ResourceNotFoundPage(String name) {
        add(new Label("name", name));
        add(new ToolbarBehavior() {
            @Override
            protected String getCurrentWorkspaceId() {
                return ((BrixRequestCycleProcessor) getRequestCycle().getActiveRequestHandler()).getWorkspace();
            }
        });
    }

    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}