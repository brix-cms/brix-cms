package brix.rmiserver.jackrabbit;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Credentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.security.CredentialsCallback;
import org.apache.jackrabbit.core.security.UserPrincipal;

import brix.rmiserver.Role;
import brix.rmiserver.User;

public class ServerLoginModule implements LoginModule
{
    private Subject subject;
    private CallbackHandler callbackHandler;

    private final Authorizer authorizer;

    /** local principals that were logged in */
    private final Set<Principal> principals = new HashSet<Principal>();


    /**
     * Constructor
     * 
     * @param authorizer
     *            authoerizer
     */
    public ServerLoginModule(Authorizer authorizer)
    {
        this.authorizer = authorizer;
    }

    /**
     * {@inheritDoc}
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ? > sharedState, Map<String, ? > options)
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    /**
     * {@inheritDoc}
     */
    public boolean login() throws LoginException
    {
        try
        {
            // clear any existing principals
            principals.clear();

            // authorize
            Credentials credentials = getCredentials();
            User user = authorizer.authorize(credentials, Role.WEBDAV, Role.RMI);

            // store authorized principal
            principals.add(new UserPrincipal(user.getLogin()));

            return true;
        }
        catch (AuthorizationException e)
        {
            principals.clear();
            throw new FailedLoginException(e.getMessage());
        }
    }

    private Credentials getCredentials() throws LoginException
    {
        if (callbackHandler == null)
        {
            throw new LoginException("Null callback handler");
        }

        CredentialsCallback ccb = new CredentialsCallback();
        try
        {
            callbackHandler.handle(new Callback[] { ccb });
        }
        catch (Exception e)
        {
            throw new LoginException("Failed to retrieve login credentials");
        }
        return ccb.getCredentials();
    }


    /**
     * {@inheritDoc}
     */
    public boolean abort() throws LoginException
    {
        if (principals.isEmpty())
        {
            return false;
        }
        else
        {
            logout();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean logout() throws LoginException
    {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean commit() throws LoginException
    {
        if (principals.isEmpty())
        {
            return false;
        }
        else
        {
            // add authenticated principals to the subject
            subject.getPrincipals().addAll(principals);
            return true;
        }
    }
}
