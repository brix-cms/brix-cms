package brix.web.tile.pagetile;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.tilepage.TilePageRenderPanel;
import brix.web.nodepage.BrixNodeWebPage;

public class PageTileViewerPanel extends Panel
{

	public PageTileViewerPanel(String id, IModel<BrixNode> tileNode)
	{
		super(id, tileNode);
		setRenderBodyOnly(true);
	}

	@Override
	protected void onBeforeRender()
	{
		if (!hasBeenRendered())
		{
			init();
		}

		super.onBeforeRender();
	}

	private void init()
	{

		JcrNode tileNode = (JcrNode) getModelObject();

		if (checkLoop(getModel()) == true)
		{
			add(new Label("view", "Loop detected."));
		}
		else
		{

			BrixNode pageNode = (BrixNode) (tileNode.hasProperty("pageNode") ? tileNode.getProperty("pageNode")
					.getNode() : null);

			if (pageNode != null)
			{
				add(new TilePageRenderPanel("view", new BrixNodeModel(pageNode), (BrixNodeWebPage) getPage()));
			}
			else
			{
				add(new Label("view", "Page not found."));
			}
		}

	}

	private boolean checkLoop(final IModel model)
	{
		final boolean loop[] = { false };

		visitParents(PageTileViewerPanel.class, new IVisitor()
		{

			public Object component(Component component)
			{
				// found parent with same model, this indicates a loop
				if (component != PageTileViewerPanel.this && component.getModel().equals(model))
				{
					loop[0] = true;
					return STOP_TRAVERSAL;
				}
				return CONTINUE_TRAVERSAL;
			}
		});

		return loop[0];
	}
}
