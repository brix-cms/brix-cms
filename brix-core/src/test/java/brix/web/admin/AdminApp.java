package brix.web.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.wicket.Application;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.BrixRequestCycle.Locator;
import brix.auth.AuthorizationStrategy;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.plugin.site.node.tilepage.TilePageNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNode;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.LinkTile;
import brix.web.StatelessFormTile;
import brix.web.StatelessLinkTile;
import brix.web.TimeTile;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.ForbiddenPage;
import brix.web.nodepage.ResourceNotFoundPage;
import brix.web.tile.menu.MenuTile;
import brix.web.tile.pagetile.PageTile;
import brix.web.tile.treemenu.TreeMenuTile;


public class AdminApp extends WebApplication
{

    private AppProperties props;

    private class MyBrix extends Brix
    {

        public MyBrix()
        {
            TileNodePlugin plugin = new TilePageNodePlugin();
            addTiles(plugin);
            
            SitePlugin sitePlugin = SitePlugin.get(this);
            sitePlugin.registerNodePlugin(plugin);

            plugin = new TileTemplateNodePlugin();
            addTiles(plugin);
            sitePlugin.registerNodePlugin(plugin);
            
            getWrapperRegistry().registerWrapper(TilePageNode.class);
            getWrapperRegistry().registerWrapper(TileTemplateNode.class);

        }

        private void addTiles(TileNodePlugin plugin)
        {
            plugin.addTile(new TimeTile());
            plugin.addTile(new TreeMenuTile());
            plugin.addTile(new PageTile());
            plugin.addTile(new LinkTile());
            plugin.addTile(new StatelessLinkTile());
            plugin.addTile(new StatelessFormTile());
            plugin.addTile(new MenuTile());
        }

        @Override
        public AuthorizationStrategy newAuthorizationStrategy()
        {
            return new DefaultAuthorizationStrategy();
        }
    }

    private Brix brix = new MyBrix();

    public Brix getBrix()
    {
        return brix;
    }

    @Override
    public Class<? extends Page> getHomePage()
    {
        return null;
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response)
    {
        return new AdminRequestCycle(this, (WebRequest)request, (WebResponse)response);
    }

    public AppProperties getProps()
    {
        return props;
    }

    @Override
    protected void init()
    {
        super.init();

        try
        {
            props = new AppProperties();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load application properties", e);
        }

        getMarkupSettings().setStripWicketTags(true);
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

        mount(new BrixNodePageUrlCodingStrategy()
        {
            @Override
            protected BrixNodeWebPage newPageInstance(IModel<JcrNode> nodeModel, BrixPageParameters pageParameters)
            {
                throw new UnsupportedOperationException();
            }
        });

        mountBookmarkablePage("/jcr-admin", JcrAdminPage.class);
        mountBookmarkablePage("/NotFound", ResourceNotFoundPage.class);
        mountBookmarkablePage("/Forbiden", ForbiddenPage.class);

        initRepository();
    }

    private void initRepository()
    {
        try
        {
            File home = new File(props.getJcrRepositoryLocation());
            InputStream configStream = new FileInputStream(new File(home, "repository.xml"));
            RepositoryConfig config = RepositoryConfig.create(configStream, home.toString());
            configStream.close();
            repository = RepositoryImpl.create(config);

            Credentials cred = new SimpleCredentials(getProps().getJcrLogin(), getProps()
                    .getJcrPassword().toString().toCharArray());
            javax.jcr.Session session = repository.login(cred);

            getBrix().initRepository(session);

        }
        catch (Exception e)
        {
            log.error("Couldn't init jackrabbit repository, make sure you"
                    + " have the jcr.repository.location config property set", e);
        }
    }

    private Repository repository;

    public Repository getRepository()
    {
        return repository;
    }

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor()
    {

        return new BrixRequestCycleProcessor(brix)
        {

            @Override
            public JcrNode getNodeForUriPath(Path path)
            {
                String nodePath = Locator.getBrix().toRealWebNodePath(path.toString());

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
                return props.getJcrDefaultWorkspace();
            }
            
            @Override
            public Path getUriPathForNode(JcrNode node)
            {
                return new Path(Locator.getBrix().fromRealWebNodePath(node.getPath()));
            }

            @Override
            public int getHttpPort()
            {
                return Integer.getInteger("jetty.port", 80);
            }

            @Override
            public int getHttpsPort()
            {
                return Integer.getInteger("jetty.sslport", 443);
            }

        };
    }
 
    public static AdminApp get()
    {
        return (AdminApp)Application.get();
    }

    private static final Logger log = LoggerFactory.getLogger(AdminApp.class);
}
