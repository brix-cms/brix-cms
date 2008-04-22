package brix.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersAware;
import brix.web.nodepage.PageParametersLink;

public class StatelessLinkPanel extends Panel implements PageParametersAware
{

    public StatelessLinkPanel(String id, final BrixPageParameters pageParameters)
    {
        super(id);

        add(new Label("label", new PropertyModel(this, "count")));

        add(new PageParametersLink("link")
        {
            @Override
            protected void contributeToPageParameters(BrixPageParameters parameters)
            {
                parameters.setQueryParam("count", (count + 1));
            }
        });
    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();

        count = BrixPageParameters.getCurrent().getQueryParam("count").toInt(0);
    }

    int count;

    public void contributeToPageParameters(BrixPageParameters pageParameters)
    {
        pageParameters.setQueryParam("count", count);
    }

    public void initializeFromPageParameters(BrixPageParameters pageParameters)
    {
    }


}
