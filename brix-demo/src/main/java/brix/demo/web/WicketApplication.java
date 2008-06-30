package brix.demo.web;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.config.BrixConfig;
import brix.demo.ApplicationProperties;
import brix.demo.util.JcrUtils;
import brix.demo.web.admin.AdminPage;
import brix.jcr.ThreadLocalSessionFactory;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.nodepage.ForbiddenPage;
import brix.web.nodepage.ResourceNotFoundPage;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
    private static final Logger logger = LoggerFactory.getLogger(WicketApplication.class);

    private ApplicationProperties properties;
    private Brix brix;
    private Repository repository;
    private ThreadLocalSessionFactory sessionFactory;

    /**
     * Constructor
     */
    public WicketApplication()
    {
    }


    /**
     * @see wicket.Application#getHomePage()
     */
    public Class getHomePage()
    {
        // brix takes over the homepage, so no need to return one
        return null;
    }

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor()
    {

        return new BrixRequestCycleProcessor(brix)
        {

            @Override
            protected String getDefaultWorkspaceName()
            {
                String name = properties.getJcrDefaultWorkspace();
                return SitePlugin.get().getSiteWorkspace(name, "").getId();
            }

        };
    }

    @Override
    protected void init()
    {
        super.init();

        // read application properties
        properties = new ApplicationProperties();

        logger.info("Using JCR repository url: " + properties.getJcrRepositoryUrl());

        // create jcr repository
        repository = JcrUtils.createRepository(properties.getJcrRepositoryUrl());

        // create session factory that will be used to feed brix jcr sessions
        sessionFactory = new ThreadLocalSessionFactory(repository, properties
            .buildSimpleCredentials());

        // create workspace manager brix will use to access workspace-related functionality
        final WorkspaceManager workspaceManager = JcrUtils.createWorkspaceManager(properties
            .getWorkspaceManagerUrl(), sessionFactory);

        try
        {

            BrixConfig config = new BrixConfig(sessionFactory, workspaceManager);
            config.setHttpPort(properties.getHttpPort());
            config.setHttpsPort(properties.getHttpsPort());
            brix = new DemoBrix(config);
            brix.attachTo(this);
            initializeRepository();
            initDefaultWorkspace();
        }
        finally
        {
            sessionFactory.cleanupLocalSessions();
        }


        getMarkupSettings().setStripWicketTags(true);

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
            final String wn = properties.getJcrDefaultWorkspace();
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


    public ApplicationProperties getProperties()
    {
        return properties;
    }

    public Brix getBrix()
    {
        return brix;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public static WicketApplication get()
    {
        return (WicketApplication)WebApplication.get();
    }

    public void cleanupSessionFactory()
    {
        sessionFactory.cleanupLocalSessions();
    }
}
