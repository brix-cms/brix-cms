package brix.demo.web.tile;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import brix.web.nodepage.BrixPageParameters;

public class LinkPanel extends Panel
{

    public LinkPanel(String id, final BrixPageParameters pageParameters)
    {
        super(id);
        add(new Link("link")
        {
            @Override
            public void onClick()
            {

                int count = pageParameters.getQueryParam("count").toInt(0);
                ++count;
                pageParameters.setQueryParam("count", count);
            }
        });
    }


}
