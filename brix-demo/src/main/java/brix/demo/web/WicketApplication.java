package brix.demo.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.BrixRequestCycle.Locator;
import brix.demo.ApplicationProperties;
import brix.demo.web.admin.AdminPage;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.ForbiddenPage;
import brix.web.nodepage.ResourceNotFoundPage;

/**
 * Application object for your web application. If you want to run this application without
 * deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
    private ApplicationProperties properties;
    private Brix brix;
    private Repository repository;

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
        return HomePage.class;
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response)
    {
        return new WicketRequestCycle(this, (WebRequest)request, response);
    }

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor()
    {

        return new BrixRequestCycleProcessor(brix)
        {

            @Override
            public JcrNode getNodeForUriPath(Path path)
            {
                String nodePath = SitePlugin.get().toRealWebNodePath(path.toString());

                String workspace = getWorkspace();
                JcrSession session = ((BrixRequestCycle)RequestCycle.get())
                        .getJcrSession(workspace);
                if (session.itemExists(nodePath))
                    return (JcrNode)session.getItem(nodePath);
                else
                    return null;
            }

            @Override
            protected String getDefaultWorkspaceName()
            {
                return properties.getJcrDefaultWorkspace();
            }

            @Override
            public Path getUriPathForNode(JcrNode node)
            {
                return new Path(SitePlugin.get().fromRealWebNodePath(node.getPath()));
            }

            @Override
            public int getHttpPort()
            {
                return Integer.getInteger("jetty.port", 8080);
            }

            @Override
            public int getHttpsPort()
            {
                return Integer.getInteger("jetty.sslport", 8443);
            }

        };
    }


    @Override
    protected void init()
    {
        super.init();

        brix = new DemoBrix();
        properties = new ApplicationProperties();
        initRepository();

        // allow brix to initialize the repository
        Credentials cred = properties.buildSimpleCredentials();

        try
        {
            javax.jcr.Session classic = repository.login(cred);
            JcrSession session = JcrSession.Wrapper.wrap(classic);

            brix.initRepository(classic);

            // create and init the default workspace if its not already there

            List<String> workspaceNames = brix.getAvailableWorkspaces(session);
            if (!workspaceNames.contains(properties.getJcrDefaultWorkspace()))
            {
                brix.createWorkspace(session, properties.getJcrDefaultWorkspace());
                javax.jcr.Session classic2 = repository.login(cred, properties
                        .getJcrDefaultWorkspace());
                JcrSession session2 = JcrSession.Wrapper.wrap(classic2);
                brix.initWorkspace(session2);
                session2.save();
                session2.logout();

            }

            classic.save();
            classic.logout();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not initialize repository with Brix");
        }

        // allow brix to handle any url that wicket cant
        mount(new BrixNodePageUrlCodingStrategy()
        {
            @Override
            protected BrixNodeWebPage newPageInstance(IModel<JcrNode> nodeModel,
                    BrixPageParameters pageParameters)
            {
                throw new UnsupportedOperationException();
            }
        });

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

    private void initRepository()
    {
        try
        {
            File home = new File(properties.getJcrRepositoryLocation());
            InputStream configStream = new FileInputStream(new File(home, "repository.xml"));
            RepositoryConfig config = RepositoryConfig.create(configStream, home.toString());
            configStream.close();
            repository = RepositoryImpl.create(config);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Couldn't init jackrabbit repository, make sure you"
                    + " have the jcr.repository.location config property set", e);
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
}
