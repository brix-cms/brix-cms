package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;

public class PageParametersAwareEnabler implements IComponentOnBeforeRenderListener
{

    public void onBeforeRender(Component component)
    {
        if (component instanceof PageParametersAware)
        {
            PageParametersAware aware = (PageParametersAware)component;
            aware.initializeFromPageParameters(BrixPageParameters.getCurrent());
        }
    }

}
