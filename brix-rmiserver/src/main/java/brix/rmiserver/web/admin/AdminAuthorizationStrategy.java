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

package brix.rmiserver.web.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.AbstractRestartResponseException;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.util.crypt.Base64;

import brix.rmiserver.AuthenticationException;
import brix.rmiserver.Role;

public class AdminAuthorizationStrategy implements IAuthorizationStrategy
{

    public boolean isActionAuthorized(Component component, Action action)
    {
        return true;
    }

    public <T extends Component> boolean isInstantiationAuthorized(Class<T> componentClass)
    {
        boolean authorized = false;
        if (Page.class.isAssignableFrom(componentClass))
        {
            if (Application.get().getApplicationSettings().getAccessDeniedPage().isAssignableFrom(
                componentClass))
            {
                return true;
            }


            AdminSession session = AdminSession.get();
            HttpServletRequest req = ((WebRequestCycle)RequestCycle.get()).getWebRequest()
                .getHttpServletRequest();


            if (!session.isUserLoggedIn())
            {
                boolean authenticated = false;

                String[] auth = parseAuthHeader(req.getHeader("Authorization"));
                if (auth != null)
                {
                    try
                    {
                        session.loginUser(auth[0], auth[1]);
                        authenticated = true;
                    }
                    catch (AuthenticationException e)
                    {
                        // noop
                    }

                }

                if (authenticated == false)
                {
                    RequestCycle.get().setRequestTarget(new IRequestTarget()
                    {

                        public void detach(RequestCycle requestCycle)
                        {
                        }

                        public void respond(RequestCycle rc)
                        {
                            HttpServletResponse res = ((WebRequestCycle)rc).getWebResponse()
                                .getHttpServletResponse();

                            res.setHeader("WWW-Authenticate", "BASIC");
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                        }

                    });

                    throw new AbstractRestartResponseException()
                    {
                        private static final long serialVersionUID = 1L;

                    };
                }
            }

            // user is authenticated
            AllowedRoles ar = componentClass.getAnnotation(AllowedRoles.class);
            if (ar != null)
            {
                for (Role role : ar.value())
                {
                    if (session.loggedinUser().getRoles().contains(role))
                    {
                        authorized = true;
                        break;
                    }
                }
            }
            else
            {
                authorized = true;
            }


        }
        else
        {
            // not a page
            authorized = true;
        }
        return authorized;
    }

    private String[] parseAuthHeader(String auth)
    {
        if (auth != null && auth.toLowerCase().startsWith("basic "))
        {
            auth = auth.substring(6);
            auth = new String(Base64.decodeBase64(auth.getBytes()));
            String tokens[] = auth.split(":");
            if (tokens.length == 2)
            {
                return tokens;
            }
        }
        return null;
    }


}
