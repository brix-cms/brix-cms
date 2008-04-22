package brix.web.admin;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.jcr.api.JcrSession;
import brix.web.RequestCycleSessionManager;

public class AdminRequestCycle extends WebRequestCycle implements BrixRequestCycle
{
    public AdminRequestCycle(WebApplication application, WebRequest request, Response response)
    {
        super(application, request, response);

    }

    private RequestCycleSessionManager sessionManager;

    public JcrSession getJcrSession(String workspace)
    {
        if (sessionManager == null)
        {
            AdminApp app = AdminApp.get();
            Credentials cred = new SimpleCredentials(app.getProps().getJcrLogin(), app.getProps()
                    .getJcrPassword().toString().toCharArray());
            sessionManager = new RequestCycleSessionManager(AdminApp.get().getRepository(), cred);
        }
        return sessionManager.getJcrSession(workspace);
    }

    public Brix getBrix()
    {
        return ((AdminApp)getApplication()).getBrix();
    }

    @Override
    public void detach()
    {
        if (sessionManager != null)
            sessionManager.detach();
        super.detach();
    }

    public static AdminRequestCycle get()
    {
        return (AdminRequestCycle)RequestCycle.get();
    }

}
