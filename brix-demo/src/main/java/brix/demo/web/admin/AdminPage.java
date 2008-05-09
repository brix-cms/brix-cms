package brix.demo.web.admin;

import org.apache.wicket.markup.html.WebPage;

import brix.Path;
import brix.plugin.site.SitePlugin;
import brix.web.admin.AdminPanel;

/**
 * @author igor.vaynberg
 */

public class AdminPage extends WebPage
{
    public AdminPage()
    {
        add(new AdminPanel("admin", null, new Path(SitePlugin.get().getSiteRootPath())));
    }
}
