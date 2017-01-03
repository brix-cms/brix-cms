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

package org.brixcms.workspace;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;

public class WorkspaceUtils {

    public static final String WORKSPACE_PARAM = Brix.NS_PREFIX + "workspace";

    public static final String COOKIE_NAME = "brix-revision";

    public static final MetaDataKey<String> WORKSPACE_METADATA = new MetaDataKey<String>() {
        private static final long serialVersionUID = 1L;
    };

    public static String getWorkspace() {
        String workspace = getWorkspaceFromUrl();

        if (workspace != null) {
            return workspace;
        }

        RequestCycle rc = RequestCycle.get();
        workspace = rc.getMetaData(WORKSPACE_METADATA);
        if (workspace == null) {
            WebRequest req = (WebRequest) RequestCycle.get().getRequest();
            WebResponse resp = (WebResponse) RequestCycle.get().getResponse();
            Cookie cookie = req.getCookie(COOKIE_NAME);
            workspace = getDefaultWorkspaceName();
            if (cookie != null) {
                if (cookie.getValue() != null)
                    workspace = cookie.getValue();
            }
            if (!checkSession(workspace)) {
                workspace = getDefaultWorkspaceName();
            }
            if (workspace == null) {
                throw new IllegalStateException("Could not resolve jcr workspace to use for this request");
            }
            Cookie c = new Cookie(COOKIE_NAME, workspace);
            c.setPath("/");
            if (workspace.toString().equals(getDefaultWorkspaceName()) == false)
                resp.addCookie(c);
            else if (cookie != null)
                resp.clearCookie(cookie);
            rc.setMetaData(WORKSPACE_METADATA, workspace);
        }
        return workspace;
    }

    private static String getWorkspaceFromUrl() {
        HttpServletRequest request = (HttpServletRequest) ((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();

        if (request.getParameter(WORKSPACE_PARAM) != null) {
            return request.getParameter(WORKSPACE_PARAM);
        }

        String referer = request.getHeader("referer");

        if (!Strings.isEmpty(referer)) {
            return extractWorkspaceFromReferer(referer);
        } else {
            return null;
        }
    }

    private static String extractWorkspaceFromReferer(String refererURL) {
        int i = refererURL.indexOf('?');
        if (i != -1 && i != refererURL.length() - 1) {
            String param = refererURL.substring(i + 1);
            String params[] = Strings.split(param, '&');
            for (String s : params) {
                try {
                    s = URLDecoder.decode(s, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // rrright
                    throw new RuntimeException(e);
                }
                if (s.startsWith(WORKSPACE_PARAM + "=")) {
                    String value = s.substring(WORKSPACE_PARAM.length() + 1);
                    if (value.length() > 0) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private static boolean checkSession(String workspaceId) {
        return Brix.get().getWorkspaceManager().workspaceExists(workspaceId);
    }

    private static String getDefaultWorkspaceName() {
        Brix brix = Brix.get();
        final Workspace workspace = brix.getConfig().getMapper().getWorkspaceForRequest(RequestCycle.get(), brix);
        return (workspace != null) ? workspace.getId() : null;
    }

}