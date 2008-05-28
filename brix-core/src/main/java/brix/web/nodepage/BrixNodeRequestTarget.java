package brix.web.nodepage;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.component.IPageRequestTarget;

import brix.jcr.wrapper.BrixNode;
import brix.web.BrixRequestCycleProcessor;

public class BrixNodeRequestTarget implements IPageRequestTarget
{

    private final IModel<BrixNode> nodeModel;
    private final BrixNodeWebPage page;
    private BrixPageParameters parameters;
    
    public BrixNodeRequestTarget(IModel<BrixNode> nodeModel, BrixPageParameters parameters)
    {

        if (nodeModel == null)
        {
            throw new IllegalArgumentException("Argument 'nodeModel' may not be null.");
        }

        if (parameters == null)
        {
            throw new IllegalArgumentException("Argument 'parameters' may not be null.");
        }

        this.nodeModel = nodeModel;
        this.parameters = parameters;
        this.page = null;
    }

    public BrixNodeRequestTarget(IModel<BrixNode> nodeModel)
    {
        this(nodeModel, new BrixPageParameters());
    }

    public BrixNodeRequestTarget(BrixNodeWebPage page, BrixPageParameters parameters)
    {
        if (page == null)
        {
            throw new IllegalArgumentException("Argument 'page' may not be null.");
        }

        if (parameters == null)
        {
            throw new IllegalArgumentException("Argument 'parameters' may not be null.");
        }

        this.page = page;
        this.nodeModel = page.getNodeModel();
        this.parameters = parameters;
    }

    public String getNodeURL()
    {
        return ((BrixRequestCycleProcessor)RequestCycle.get().getProcessor()).getUriPathForNode(
                nodeModel.getObject()).toString();
    }

    public BrixNodeWebPage getPage()
    {
        return page;
    }

    public BrixPageParameters getParameters()
    {
        return parameters;
    }

    public BrixNodeRequestTarget(BrixNodeWebPage page)
    {
        this(page, page.getBrixPageParameters());
    }

    public void detach(RequestCycle requestCycle)
    {

    }

    public void respond(RequestCycle requestCycle)
    {
        CharSequence url = requestCycle.urlFor(this);
        requestCycle.getResponse().redirect(url.toString());
    }

}
