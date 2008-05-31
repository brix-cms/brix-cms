package brix.web.nodepage.markup;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import brix.jcr.wrapper.BrixNode;

/**
 * Panel that uses {@link MarkupSource} for it's markup and child components.
 * 
 * @author Matej Knopp
 */
public abstract class BrixMarkupNodePanel extends Panel<BrixNode> implements IMarkupResourceStreamProvider,
		IMarkupCacheKeyProvider, MarkupSourceProvider
{
	public BrixMarkupNodePanel(String id, IModel<BrixNode> model)
	{
		super(id, model);
	}

	public BrixMarkupNodePanel(String id)
	{
		super(id);
	}

	public String getCacheKey(MarkupContainer<?> container, Class<?> containerClass)
	{
		return null;
	}

	@Override
	protected void onBeforeRender()
	{
		this.markupHelper = new MarkupHelper(this);
		super.onBeforeRender();
	}

	private MarkupHelper markupHelper;

	@Override
	protected void onDetach()
	{
		super.onDetach();
		markupHelper = null;
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer<?> container, Class<?> containerClass)
	{
		return new StringResourceStream(markupHelper.getMarkup(), "text/html");
	}
}
