package brix.demo.web;

import java.io.FileInputStream;
import java.io.InputStream;

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
import org.apache.wicket.util.file.File;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.demo.ApplicationProperties;
import brix.demo.web.admin.AdminPage;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TilePageNode;
import brix.web.BrixRequestCycleProcessor;
import brix.web.RequestCycleSessionManager;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.ForbiddenPage;
import brix.web.nodepage.ResourceNotFoundPage;
import brix.workspace.Workspace;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see wicket.myproject.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	private static final boolean USE_RMI = false;

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
		return new WicketRequestCycle(this, (WebRequest) request, response);
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
				JcrSession session = ((BrixRequestCycle) RequestCycle.get()).getJcrSession(workspace);
				if (session.itemExists(nodePath))
					return (JcrNode) session.getItem(nodePath);
				else
					return null;
			}

			@Override
			protected String getDefaultWorkspaceName()
			{
				String name = properties.getJcrDefaultWorkspace();
				return SitePlugin.get().getSiteWorkspace(name, "").getId();
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
		createRepository();
		initializeRepository();
		initDefaultWorkspace();

		// allow brix to handle any url that wicket cant
		mount(new BrixNodePageUrlCodingStrategy()
		{
			@Override
			protected BrixNodeWebPage newPageInstance(IModel<JcrNode> nodeModel, BrixPageParameters pageParameters)
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
			protected IRequestTarget handleExpiredPage(String pageMapName, Class pageClass, int trailingSlashesCount,
					boolean redirect)
			{
				return new HybridBookmarkablePageRequestTarget(pageMapName, (Class) pageClassRef.get(), null,
						trailingSlashesCount, redirect);
			}
		});

		mountBookmarkablePage("/NotFound", ResourceNotFoundPage.class);
		mountBookmarkablePage("/Forbiden", ForbiddenPage.class);
	}

	private void initDefaultWorkspace()
	{
		try
		{
			RequestCycleSessionManager manager = new RequestCycleSessionManager(brix, repository, properties
					.buildSimpleCredentials());
			
			final String defaultState = "";
			final String wn = properties.getJcrDefaultWorkspace();
			final SitePlugin sp = SitePlugin.get(brix);

			DemoBrix db = (DemoBrix) brix;
			db.setSessionManager(manager);

			if (!sp.siteExists(wn, defaultState))
			{
				Workspace w = sp.createSite(wn, defaultState);
				JcrSession session = manager.getJcrSession(w.getId());

				brix.initWorkspace(w, session);

				JcrNode siteRoot = (JcrNode) session.getItem(sp.getSiteRootPath());
				JcrNode index = siteRoot.addNode("index.html", "nt:file");
				TilePageNode node = TilePageNode.initialize(index);
				node.setData("<html><head></head><body>Hello, world!</body></html>");
				session.save();
			}

			manager.detach();

		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not initialize jackrabbit workspace with Brix", e);
		}
		finally
		{
			((DemoBrix) brix).setSessionManager(null);
		}
	}

	private void initializeRepository()
	{
		Credentials cred = properties.buildSimpleCredentials();
		try
		{
			javax.jcr.Session session = repository.login(cred);
			brix.initRepository(session);
			session.save();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Couldn't initialize jackrabbit repository", e);
		}
	}

	private void createRepository()
	{
		try
		{
			if (USE_RMI)
			{

				// ClientRepositoryFactory factory = new
				// ClientRepositoryFactory();
				// repository =
				// factory.getRepository("rmi://localhost:1099/jackrabbit");
			}
			else
			{
				File home = new File(properties.getJcrRepositoryLocation());
				InputStream configStream = new FileInputStream(new File(home, "repository.xml"));
				RepositoryConfig config = RepositoryConfig.create(configStream, home.toString());
				configStream.close();
				repository = RepositoryImpl.create(config);

			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Couldn't create jackrabbit repository, make sure you"
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
		return (WicketApplication) WebApplication.get();
	}
}
