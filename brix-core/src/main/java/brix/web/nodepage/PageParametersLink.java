package brix.web.nodepage;

import org.apache.wicket.model.IModel;

/**
 * Stateless link component that allows {@link PageParametersAware} components to contribute to it's
 * URL. This link doesn't have a callback method, instead it allows user to override it's
 * {@link #contributeToPageParameters(BrixPageParameters)} method to postprocess the link's URL.
 *
 * @todo it's unclear how this is semantically distinguished from {@link PageParametersCarryingLink}
 * 
 * @author Matej Knopp
 */
public class PageParametersLink extends AbstractPageParametersLink
{
    public PageParametersLink(String id)
    {
        super(id);
    }

    public PageParametersLink(String id, IModel< ? > model)
    {
        super(id, model);
    }

    @Override
    protected String constructUrl(BrixPageParameters params)
    {
        return params.toCallbackURL();
    }
}
