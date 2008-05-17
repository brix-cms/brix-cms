package brix.rmiserver.web.admin;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

import brix.rmiserver.AuthenticationException;
import brix.rmiserver.User;
import brix.rmiserver.UserService;

public class AdminSession extends WebSession
{
    private static final long serialVersionUID = 1L;

    @SpringBean
    private UserService users;

    private Long userId;

    public AdminSession(Request request)
    {
        super(request);
        InjectorHolder.getInjector().inject(this);
    }

    public boolean isUserLoggedIn()
    {
        return userId != null;
    }

    public User loginUser(String login, String password) throws AuthenticationException
    {
        User user = users.query(login, password);
        if (user == null)
        {
            throw new AuthenticationException();
        }
        userId = user.getId();
        return user;
    }

    public Long loggedinUserId()
    {
        return userId;
    }

    public User loggedinUser()
    {

        return (userId == null) ? null : users.load(userId);
    }

    public static AdminSession get()
    {
        return (AdminSession)Session.get();
    }


}
