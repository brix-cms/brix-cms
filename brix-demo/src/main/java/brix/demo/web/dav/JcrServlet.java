package brix.demo.web.dav;

import javax.jcr.Repository;

import org.apache.jackrabbit.webdav.jcr.JCRWebdavServerServlet;
import org.apache.wicket.Application;

import brix.demo.web.WicketApplication;

public class JcrServlet extends JCRWebdavServerServlet
{

    public JcrServlet()
    {

    }

    @Override
    protected Repository getRepository()
    {
        WicketApplication app = (WicketApplication)Application.get("wicket.brix-demo");
        return app.getRepository();
    }

}
