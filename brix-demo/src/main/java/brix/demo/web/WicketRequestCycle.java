package brix.demo.web;

import javax.jcr.Credentials;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.jcr.api.JcrSession;
import brix.web.RequestCycleSessionManager;

public class WicketRequestCycle extends WebRequestCycle implements BrixRequestCycle
{
	public WicketRequestCycle(WebApplication application, WebRequest request, Response response)
	{
		super(application, request, response);

	}

	private RequestCycleSessionManager sessionManager;

	public JcrSession getJcrSession(String workspace)
	{
		if (sessionManager == null)
		{
			WicketApplication app = WicketApplication.get();
			Credentials cred = app.getProperties().buildSimpleCredentials();
			sessionManager = new RequestCycleSessionManager(BrixRequestCycle.Locator.getBrix(), app.getRepository(),
					cred);
		}
		return sessionManager.getJcrSession(workspace);
	}

	public Brix getBrix()
	{
		return WicketApplication.get().getBrix();
	}

	@Override
	public void detach()
	{
		if (sessionManager != null)
		{
			sessionManager.detach();
		}
		super.detach();
	}

	public static WicketRequestCycle get()
	{
		return (WicketRequestCycle) RequestCycle.get();
	}

}
