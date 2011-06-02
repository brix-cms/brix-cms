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

import org.apache.jackrabbit.core.security.UserPrincipal;
import org.apache.jackrabbit.core.security.authentication.CredentialsCallback;
import org.brixcms.rmiserver.Role;
import org.brixcms.rmiserver.User;

import javax.jcr.Credentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerLoginModule implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;

    private final Authorizer authorizer;

    /**
     * local principals that were logged in
     */
    private final Set<Principal> principals = new HashSet<Principal>();

    /**
     * Constructor
     *
     * @param authorizer authorizer
     */
    public ServerLoginModule(Authorizer authorizer) {
        this.authorizer = authorizer;
    }


    /**
     * {@inheritDoc}
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    /**
     * {@inheritDoc}
     */
    public boolean login() throws LoginException {
        try {
            // clear any existing principals
            principals.clear();

            // authorize
            Credentials credentials = getCredentials();
            User user = authorizer.authorize(credentials, Role.WEBDAV, Role.RMI);

            // store authorized principal
            principals.add(new UserPrincipal(user.getLogin()));

            return true;
        } catch (AuthorizationException e) {
            principals.clear();
            throw new FailedLoginException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean commit() throws LoginException {
        if (principals.isEmpty()) {
            return false;
        } else {
            // add authenticated principals to the subject
            subject.getPrincipals().addAll(principals);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean abort() throws LoginException {
        if (principals.isEmpty()) {
            return false;
        } else {
            logout();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        return true;
    }

    private Credentials getCredentials() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("Null callback handler");
        }

        CredentialsCallback ccb = new CredentialsCallback();
        try {
            callbackHandler.handle(new Callback[]{ccb});
        } catch (Exception e) {
            throw new LoginException("Failed to retrieve login credentials");
        }
        return ccb.getCredentials();
    }
}
