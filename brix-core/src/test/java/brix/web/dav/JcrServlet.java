package brix.web.dav;

import javax.jcr.Repository;

import org.apache.jackrabbit.webdav.jcr.JCRWebdavServerServlet;
import org.apache.wicket.Application;

import brix.web.admin.AdminApp;

public class JcrServlet extends JCRWebdavServerServlet
{

    public JcrServlet()
    {

    }

    @Override
    protected Repository getRepository()
    {
        AdminApp app = (AdminApp)Application.get("wicket.brix");
        return app.getRepository();
    }

}
