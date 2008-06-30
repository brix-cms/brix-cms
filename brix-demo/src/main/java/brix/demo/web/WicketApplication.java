package brix.demo.web;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Session;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;

import brix.Brix;
import brix.config.BrixConfig;
import brix.demo.web.admin.AdminPage;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.nodepage.ForbiddenPage;
import brix.web.nodepage.ResourceNotFoundPage;
import brix.workspace.Workspace;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public final class WicketApplication extends AbstractWicketApplication
{

    /** brix instance */
    private Brix brix;

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor()
    {

        return new BrixRequestCycleProcessor(brix)
        {

            @Override
            protected String getDefaultWorkspaceName()
            {
                String name = getProperties().getJcrDefaultWorkspace();
                return SitePlugin.get().getSiteWorkspace(name, "").getId();
            }

        };
    }

    @Override
    protected void init()
    {
        super.init();

        try
        {

            // create brix configuration
            BrixConfig config = new BrixConfig(getJcrSessionFactory(), getWorkspaceManager());
            config.setHttpPort(getProperties().getHttpPort());
            config.setHttpsPort(getProperties().getHttpsPort());

            // create brix instance
            brix = new DemoBrix(config);
            brix.attachTo(this);
            initializeRepository();
            initDefaultWorkspace();
        }
        finally
        {
            cleanupSessionFactory();
        }

        // mount admin page
        mount(new HybridUrlCodingStrategy("/admin", AdminPage.class)
        {
            @SuppressWarnings("unchecked")
            @Override
            protected IRequestTarget handleExpiredPage(String pageMapName, Class pageClass,
                    int trailingSlashesCount, boolean redirect)
            {
                return new HybridBookmarkablePageRequestTarget(pageMapName, (Class)pageClassRef
                    .get(), null, trailingSlashesCount, redirect);
            }
        });

        mountBookmarkablePage("/NotFound", ResourceNotFoundPage.class);
        mountBookmarkablePage("/Forbiden", ForbiddenPage.class);
    }

    private void initDefaultWorkspace()
    {
        try
        {
            final String defaultState = "";
            final String wn = getProperties().getJcrDefaultWorkspace();
            final SitePlugin sp = SitePlugin.get(brix);


            if (!sp.siteExists(wn, defaultState))
            {
                Workspace w = sp.createSite(wn, defaultState);
                JcrSession session = brix.getCurrentSession(w.getId());

                session.importXML("/", getClass().getResourceAsStream("workspace.xml"),
                    ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

                session.save();
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
        }
    }

    private void initializeRepository()
    {
        try
        {
            Session session = brix.getCurrentSession(null);
            brix.initRepository(session);
            session.save();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Couldn't initialize jackrabbit repository", e);
        }
    }


}
