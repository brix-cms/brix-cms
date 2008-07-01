package brix.demo.web.admin;

import org.apache.wicket.markup.html.WebPage;

import brix.web.admin.AdminPanel;

/**
 * This page hosts Brix's {@link AdminPanel}
 * 
 * @author igor.vaynberg
 */
public class AdminPage extends WebPage
{
    /**
     * Constructor
     */
    public AdminPage()
    {
        add(new AdminPanel("admin", null));
    }
}
