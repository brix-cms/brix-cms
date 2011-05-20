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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.brixcms.web.generic.BrixGenericPanel;

import javax.servlet.http.HttpServletRequest;

public class WebdavUrlPanel extends BrixGenericPanel<String> {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;

// --------------------------- CONSTRUCTORS ---------------------------

    public WebdavUrlPanel(String id, IModel<String> model) {
        super(id, model);

        add(new WebMarkupContainer("webdav") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put("href", getWorkspaceUrl("webdav", getWorkspaceId()));
            }
        });

        add(new WebMarkupContainer("jcrwebdav") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put("href", getWorkspaceUrl("jcrwebdav", getWorkspaceId()));
            }
        });
    }

    private String getWorkspaceUrl(String type, String workspaceId) {
        HttpServletRequest request = ((WebRequest) getRequest()).getHttpServletRequest();
        StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(request.getServerName());
        if (request.getServerPort() != 80) {
            url.append(":");
            url.append(request.getServerPort());
        }
        url.append(request.getContextPath());
        url.append("/");
        url.append(type);
        url.append("/");
        url.append(workspaceId);

        return url.toString();
    }

    private String getWorkspaceId() {
        return getModelObject();
    }
}
