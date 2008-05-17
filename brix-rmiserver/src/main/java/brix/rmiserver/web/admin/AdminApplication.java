package brix.rmiserver.web.admin;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see wicket.myproject.StartJackrabbitServer#main(String[])
 */
public class AdminApplication extends WebApplication
{
    /**
     * Constructor
     */
    public AdminApplication()
    {
    }

    /**
     * @see wicket.Application#getHomePage()
     */
    public Class< ? extends WebPage< ? >> getHomePage()
    {
        return HomePage.class;
    }

    @Override
    protected void init()
    {
        addComponentInstantiationListener(new SpringComponentInjector(this));
        getSecuritySettings().setAuthorizationStrategy(new AdminAuthorizationStrategy());

        mountBookmarkablePage("/users", UsersPage.class);
    }

    @Override
    public Session newSession(Request request, Response response)
    {
        return new AdminSession(request);
    }


}
