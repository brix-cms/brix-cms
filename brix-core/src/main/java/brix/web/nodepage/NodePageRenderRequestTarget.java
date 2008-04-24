package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;

class NodePageRenderRequestTarget
        implements
            IPageRequestTarget,
            PageParametersRequestTarget,
            IListenerInterfaceRequestTarget
{
    private final IModel node;
    private BrixNodeWebPage page;
    private final PageFactory pageFactory;
    private final String iface;

    public Page getPage()
    {
        return page;
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

    public Component getTarget()
    {
        if (this.iface != null)
        {
            int separator = iface.lastIndexOf(':');
            if (separator != -1)
            {
                if (page == null) {
                    page = pageFactory.newPage();
                }
                String componentPath = iface.substring(0, separator);
                page.beforeRender();
                Component component = page.get(componentPath);
                if (component == null)
                {                    
                    if (component == null)
                    {
                        throw new WicketRuntimeException(
                                "unable to find component with path " +
                                        componentPath +
                                        " on stateless page " +
                                        page +
                                        " it could be that the component is inside a repeater make your component return false in getStatelessHint()");
                    }
                }
                return component;
            }
        }
        return null;
    }

    public RequestParameters getRequestParameters()
    {
        return RequestCycle.get().getRequest().getRequestParameters();
    }

    public BrixPageParameters getPageParameters()
    {
        if (pageFactory != null)
        {
            return pageFactory.getPageParameters();
        }
        else
        {
            return page.getBrixPageParameters();
        }
    }

    public interface PageFactory
    {
        public BrixNodeWebPage newPage();

        public BrixPageParameters getPageParameters();
    };

    NodePageRenderRequestTarget(IModel node, PageFactory pageProvider, String iface)
    {
        super();
        this.node = node;
        this.pageFactory = pageProvider;
        this.iface = iface;
    }

    NodePageRenderRequestTarget(IModel node, BrixNodeWebPage page, String iface)
    {
        super();
        this.node = node;
        this.page = page;
        this.pageFactory = null;
        this.iface = iface;
    }

    public void respond(RequestCycle requestCycle)
    {
        if (page == null)
        {
            page = pageFactory.newPage();
            if (page.initialRedirect())
            {
                // if the page is newly created and initial redirect is set, we need to redirect to
                // a hybrid URL
                page.setStatelessHint(false);
                Session.get().bind();
                Session.get().touch(page);
                requestCycle.setRequestTarget(new BrixNodeRequestTarget(page));
                return;
            }
        }

        if (this.iface != null)
        {
            int separator = iface.lastIndexOf(':');
            if (separator != -1)
            {                
                executeListenerInterface();
            }
        }

        // check if the listener hasn't changed the request target
        if (RequestCycle.get().getRequestTarget() == this) 
        {
        	getPage().renderPage();
        }
    }

    private void executeListenerInterface()
    {
        Component component = getTarget();
        RequestListenerInterface listenerInterface = getRequestListenerInterface();
        listenerInterface.invoke(page, component);
    }

    public void detach(RequestCycle requestCycle)
    {
        if (getPage() != null)
        {
            getPage().detach();
        }
        node.detach();
    }

}
