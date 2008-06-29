package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;


public class PageParametersLink extends WebMarkupContainer
{
    public PageParametersLink(String id)
    {
        super(id);
    }

    public PageParametersLink(String id, IModel<?> model)
    {
        super(id, model);
    }

    protected String buildUrl()
    {
        final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
        getPage().visitChildren(PageParametersAware.class, new IVisitor<Component>()
        {
            public Object component(Component component)
            {
                ((PageParametersAware)component).contributeToPageParameters(parameters);
                return IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        contributeToPageParameters(parameters);
        return parameters.toCallbackURL();
    }

    // it is intentional that this class doesn't implement PageParametersContributior
    protected void contributeToPageParameters(BrixPageParameters parameters)
    {

    }

    protected BrixPageParameters getInitialParameters()
    {
        // not sure, should we use exiting parameters from URL?
        // e.g.return BrixPageParameters.getCurrent();
        return new BrixPageParameters();
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        // TODO: check for other tag than anchor and create onclick instead
        tag.put("href", buildUrl());

        if (!isEnabled() || !isEnableAllowed())
        {
            tag.setName("span");
        }
    }

}
