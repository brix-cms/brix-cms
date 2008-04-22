package brix.web.nodepage;

import org.apache.wicket.IRequestTarget;

public interface PageParametersRequestTarget extends IRequestTarget
{
    BrixPageParameters getPageParameters();
}
