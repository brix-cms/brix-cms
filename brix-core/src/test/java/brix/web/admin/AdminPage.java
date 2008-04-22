package brix.web.admin;

import org.apache.wicket.markup.html.WebPage;

import brix.BrixRequestCycle;
import brix.Path;

/**
 * @author igor.vaynberg
 */

public class AdminPage extends WebPage
{
    public AdminPage()
    {
        add(new AdminPanel("admin", null, new Path(BrixRequestCycle.Locator.getBrix().getWebPath())));
    }
}
