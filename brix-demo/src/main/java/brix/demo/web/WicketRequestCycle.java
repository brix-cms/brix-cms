package brix.demo.web;

import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.jcr.api.JcrSession;

public class WicketRequestCycle extends WebRequestCycle implements BrixRequestCycle
{
    public WicketRequestCycle(WebApplication application, WebRequest request, Response response)
    {
        super(application, request, response);

    }

   
    public JcrSession getJcrSession(String workspace)
    {
        return getBrix().getCurrentSession(workspace);
    }

    public Brix getBrix()
    {
        return Brix.get(Application.get());
    }

    @Override
    public void detach()
    {
        WicketApplication.get().cleanupSessionFactory();
        super.detach();
    }

    public static WicketRequestCycle get()
    {
        return (WicketRequestCycle)RequestCycle.get();
    }

}
