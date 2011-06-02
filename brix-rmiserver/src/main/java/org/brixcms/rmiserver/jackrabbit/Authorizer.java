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

package org.brixcms.rmiserver.jackrabbit;

import org.brixcms.rmiserver.Role;
import org.brixcms.rmiserver.User;
import org.brixcms.rmiserver.UserService;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

/**
 * Authorizes {@link Credentials} for given {@link Role}
 *
 * @author ivaynberg
 */
public class Authorizer {
    private final UserService users;

    public Authorizer(UserService users) {
        this.users = users;
    }

    public User authorize(Credentials creds, Role... requiredRoles) throws AuthorizationException {
        if (creds instanceof SimpleCredentials) {
            User user = null;

            // authenticate
            SimpleCredentials sc = (SimpleCredentials) creds;
            user = users.query(sc.getUserID(), new String(sc.getPassword()));

            // authorize
            if (user != null) {
                boolean authorized = false;
                for (Role requiredRole : requiredRoles) {
                    if (user.getRoles().contains(requiredRole)) {
                        authorized = true;
                        break;
                    }
                }
                if (!authorized) {
                    user = null;
                }
            }

            // return
            if (user != null) {
                return user;
            } else {
                throw new AuthorizationException("User: " + sc.getUserID() + " is not authorized");
            }
        } else {
            throw new AuthorizationException("Unsupported type of credentials: " +
                    creds.getClass().getName() + ". Only supporting: " +
                    SimpleCredentials.class.getName());
        }
    }
}
