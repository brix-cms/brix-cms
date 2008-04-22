package brix.web.nodepage;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebResponse;

import brix.web.nodepage.toolbar.ToolbarBehavior;

public class ResourceNotFoundPage extends WebPage
{

    public ResourceNotFoundPage()
    {
        this("");
    }

    public ResourceNotFoundPage(String name)
    {
        add(new Label("name", name));
        add(new ToolbarBehavior() {
            @Override
            protected String getWorkspaceName()
            {                
                return null;
            }
        });
    }

    @Override
    protected void configureResponse()
    {
        super.configureResponse();

        WebResponse response = (WebResponse)getResponse();
        response.getHttpServletResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

}
