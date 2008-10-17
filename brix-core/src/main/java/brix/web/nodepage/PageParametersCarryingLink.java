package brix.web.nodepage;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

/**
 * Constructs a link to another page including all parameters from the current page. This link makes
 * it easy to link together tiles located on different pages.
 * 
 * @author igor.vaynberg
 * 
 */
public class PageParametersCarryingLink extends AbstractPageParametersLink
{
    private final IModel<BrixNode> page;

    /**
     * Constructor
     * 
     * @param id
     * @param page
     */
    public PageParametersCarryingLink(String id, IModel<BrixNode> page)
    {
        this(id, null, page);
    }

    /**
     * Constructor
     * 
     * @param id
     * @param model
     * @param page
     */
    public PageParametersCarryingLink(String id, IModel< ? > model, IModel<BrixNode> page)
    {
        super(id, model);
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetach()
    {
        page.detach();
        super.onDetach();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String constructUrl(BrixPageParameters params)
    {
        return params.urlFor(page);
    }

}
