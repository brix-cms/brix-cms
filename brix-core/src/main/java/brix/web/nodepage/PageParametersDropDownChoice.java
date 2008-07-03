package brix.web.nodepage;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * Special kind of {@link DropDownChoice} that checks for
 * {@link PageParametersAware} components when constructing URL to redriect
 * after {@link #onSelectionChanged()}.
 * 
 * @author Matej Knopp
 * 
 * @param <T>
 */
public class PageParametersDropDownChoice<T> extends DropDownChoice<T>
{
	public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> choices)
	{
		super(id, model, choices);

	}

	public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> data, IChoiceRenderer<T> renderer)
	{
		super(id, model, data, renderer);

	}

	public PageParametersDropDownChoice(String id, IModel<T> model, IModel<List<? extends T>> choices)
	{
		super(id, model, choices);

	}

	public PageParametersDropDownChoice(String id, IModel<T> model, IModel<List<? extends T>> choices,
			IChoiceRenderer<T> renderer)
	{
		super(id, model, choices, renderer);

	}

	@Override
	protected void onSelectionChanged(Object newSelection)
	{
		getRequestCycle().setRequestTarget(getRequestTarget());
	}

	protected IRequestTarget getRequestTarget()
	{
		final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
		getPage().visitChildren(PageParametersAware.class, new Component.IVisitor<Component>()
		{
			public Object component(Component component)
			{
				((PageParametersAware) component).contributeToPageParameters(parameters);
				return Component.IVisitor.CONTINUE_TRAVERSAL;
			}
		});
		contributeToPageParameters(parameters);
		IRequestTarget target = new BrixNodeRequestTarget((BrixNodeWebPage) getPage(), parameters);
		return target;
	}

	/**
	 * Allows to change {@link BrixPageParameters} after all other components
	 * have contributed their state to it. This method can be used to
	 * postprocess the URL after selection change.
	 */
	// Note, it is intentional that this class doesn't implement
	// PageParametersAware. This method needs to be invoked after all other
	// components have contributed their state. Also it should be only used
	// when this DropDownChoice is constructing the URL.
	protected void contributeToPageParameters(BrixPageParameters parameters)
	{

	}

	/**
	 * Returns the initial {@link BrixPageParameters} used to build the URL
	 * after selection change.
	 * 
	 * @return
	 */
	protected BrixPageParameters getInitialParameters()
	{
		return new BrixPageParameters();
	}

	@Override
	protected boolean getStatelessHint()
	{
		return true;
	}
}
