package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;

public class BrixNodePageListenerRequestTarget extends BrixNodePageRequestTarget
		implements
			IListenerInterfaceRequestTarget
{
	private final String iface;

	public BrixNodePageListenerRequestTarget(IModel<?> node, BrixNodeWebPage page, String iface)
	{
		super(node, page);
		this.iface = iface;
	}

	public BrixNodePageListenerRequestTarget(IModel<?> node, PageFactory pageFactory, String iface)
	{
		super(node, pageFactory);
		this.iface = iface;
	}
	
	public RequestListenerInterface getRequestListenerInterface()
	{
		if (this.iface != null)
		{
			int separator = iface.lastIndexOf(':');
			if (separator != -1)
			{
				String interfaceName = iface.substring(separator + 1);
				RequestListenerInterface listenerInterface = RequestListenerInterface
						.forName(interfaceName);
				return listenerInterface;
			}
		}
		return null;
	}

	public RequestParameters getRequestParameters()
	{
		return RequestCycle.get().getRequest().getRequestParameters();
	}


	public Component getTarget()
	{
		if (this.iface != null)
		{
			int separator = iface.lastIndexOf(':');
			if (separator != -1)
			{

				String componentPath = iface.substring(0, separator);
				getPage().prepareForRender(false);
				Component component = getPage().get(componentPath);
				if (component == null)
				{
					throw new WicketRuntimeException(
							"unable to find component with path "
									+ componentPath
									+ " on stateless page "
									+ getPage()
									+ " it could be that the component is inside a repeater make your component return false in getStatelessHint()");
				}
				return component;
			}
		}
		return null;
	}

	@Override
	protected void respondWithInitialRedirectHandled(RequestCycle requestCycle)
	{
		int separator = iface.lastIndexOf(':');
		if (separator != -1)
		{
			Component component = getTarget();
			RequestListenerInterface listenerInterface = getRequestListenerInterface();
			listenerInterface.invoke(getPage(), component);
		}

		super.respondWithInitialRedirectHandled(requestCycle);
	}


}
