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

package org.brixcms.rmiserver.web.admin;

import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.rmiserver.AuthenticationException;
import org.brixcms.rmiserver.User;
import org.brixcms.rmiserver.UserService;

public class AdminSession extends WebSession {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService users;

    private Long userId;

    public static AdminSession get() {
        return (AdminSession) Session.get();
    }

    public AdminSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    public boolean isUserLoggedIn() {
        return userId != null;
    }

    public User loggedinUser() {
        return (userId == null) ? null : users.load(userId);
    }

    public Long loggedinUserId() {
        return userId;
    }

    public User loginUser(String login, String password) throws AuthenticationException {
        User user = users.query(login, password);
        if (user == null) {
            throw new AuthenticationException();
        }
        userId = user.getId();
        return user;
    }
}
