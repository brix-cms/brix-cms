package brix.web.tile.pagetile;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.PageRenderingPanel;
import brix.web.generic.BrixGenericPanel;

public class PageTileViewerPanel extends BrixGenericPanel<BrixNode>
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
				add(new PageRenderingPanel("view", new BrixNodeModel(pageNode)));
			}
			else
			{
				add(new Label("view", "Page not found."));
			}
		}

	}

	private boolean checkLoop(final IModel<BrixNode> model)
	{
		final boolean loop[] = { false };

		visitParents(PageTileViewerPanel.class, new IVisitor<Component>()
		{

			public Object component(Component component)
			{
				// found parent with same model, this indicates a loop
				if (component != PageTileViewerPanel.this && component.getDefaultModel().equals(model))
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
