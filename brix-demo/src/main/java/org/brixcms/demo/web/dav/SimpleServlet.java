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

package org.brixcms.demo.web.dav;

import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.AbstractLocatorFactory;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.apache.wicket.Application;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.SessionAwarePlugin;
import org.brixcms.demo.web.WicketApplication;
import org.brixcms.jcr.base.BrixSession;
import org.brixcms.jcr.base.EventUtil;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class SimpleServlet extends SimpleWebdavServlet {
    // XXX NOTE only include leading /
    static final String WORKSPACE_ROOT_PATH = "/brix:root/brix:web/brix:site";

    private AbstractLocatorFactory locatorFactory;

    /**
     * Constructor
     */
    public SimpleServlet() {

    }

    /**
     * Returns the <code>DavLocatorFactory</code>. If no locator factory has been set or created a new instance of
     * {@link org.apache.jackrabbit.webdav.simple.LocatorFactoryImpl} is returned.
     *
     * @return the locator factory
     * @see AbstractWebdavServlet#getLocatorFactory()
     */
    public DavLocatorFactory getLocatorFactory() {
        if (locatorFactory == null) {
            locatorFactory = new SiteRootLocatorFactory(this, getPathPrefix());
        }
        return locatorFactory;
    }

    private Brix getBrix() {
        WicketApplication app = (WicketApplication) Application.get("wicket.brix-demo");
        return app.getBrix();
    }

    @Override
    public Repository getRepository() {
        WicketApplication app = (WicketApplication) Application.get("wicket.brix-demo");
        return app.getRepository();
    }

    @Override
    public synchronized SessionProvider getSessionProvider() {
        final SessionProvider original = super.getSessionProvider();

        return new SessionProvider() {
            public Session getSession(HttpServletRequest request, Repository rep, String workspace)
                    throws LoginException, ServletException, RepositoryException {
                final String key = Brix.NS_PREFIX + "jcr-session";
                BrixSession s = (BrixSession) request.getAttribute(key);
                if (s == null) {
                    s = EventUtil.wrapSession(original.getSession(request, rep, workspace));
                    for (Plugin p : getBrix().getPlugins()) {
                        if (p instanceof SessionAwarePlugin) {
                            ((SessionAwarePlugin) p).onWebDavSession(s);
                        }
                    }
                    request.setAttribute(key, s);
                }
                return s;
            }

            public void releaseSession(Session session) {
                original.releaseSession(EventUtil.unwrapSession(session));
            }
        };
    }
}
