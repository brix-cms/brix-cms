package brix.markup.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupHelper;
import brix.markup.MarkupSource;
import brix.markup.MarkupSourceProvider;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;

/**
 * Page that uses {@link MarkupSource} for it's markup and child components.
 * 
 * @author Matej Knopp
 */

public abstract class BrixMarkupNodeWebPage extends BrixNodeWebPage implements IMarkupResourceStreamProvider,
		IMarkupCacheKeyProvider, MarkupSourceProvider
{

	public BrixMarkupNodeWebPage(IModel<BrixNode> nodeModel)
	{
		super(nodeModel);
	}

	public BrixMarkupNodeWebPage(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters)
	{
		super(nodeModel, pageParameters);
	}

	public String getCacheKey(MarkupContainer container, Class<?> containerClass)
	{
		return null;
	}	
	
	@Override
	protected void onBeforeRender()
	{
		this.markupHelper = new MarkupHelper(this);
		super.onBeforeRender();
	}
	
	private transient MarkupHelper markupHelper;
	
	@Override
	protected void onDetach()
	{		
		super.onDetach();
		markupHelper = null;
	}
	
	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass)
	{
		return new StringResourceStream(markupHelper.getMarkup(), "text/html");
	}
}
