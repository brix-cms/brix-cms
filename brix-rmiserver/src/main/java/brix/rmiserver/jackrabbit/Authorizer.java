package brix.rmiserver.jackrabbit;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import brix.rmiserver.Role;
import brix.rmiserver.User;
import brix.rmiserver.UserService;

/**
 * Authorizes {@link Credentials} for given {@link Role}
 * 
 * @author ivaynberg
 * 
 */
public class Authorizer
{
    private final UserService users;

    public Authorizer(UserService users)
    {
        this.users = users;
    }

    public User authorize(Credentials creds, Role... requiredRoles) throws AuthorizationException
    {
        if (creds instanceof SimpleCredentials)
        {
            User user = null;

            // authenticate
            SimpleCredentials sc = (SimpleCredentials)creds;
            user = users.query(sc.getUserID(), new String(sc.getPassword()));

            // authorize
            if (user != null)
            {
                boolean authorized = false;
                for (Role requiredRole : requiredRoles)
                {
                    if (user.getRoles().contains(requiredRole))
                    {
                        authorized = true;
                        break;
                    }
                }
                if (!authorized)
                {
                    user = null;
                }
            }

            // return
            if (user != null)
            {
                return user;
            }
            else
            {
                throw new AuthorizationException("User: " + sc.getUserID() + " is not authorized");
            }
        }
        else
        {
            throw new AuthorizationException("Unsupported type of credentials: " +
                creds.getClass().getName() + ". Only supporting: " +
                SimpleCredentials.class.getName());
        }


    }
}
