package brix.web.nodepage;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

@SuppressWarnings("unchecked")
public class PageParametersDropDownChoice<T> extends DropDownChoice<T>
{


    public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> choices)
    {
        super(id, model, choices);

    }

    public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> data,
            IChoiceRenderer<T> renderer)
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
        getPage().visitChildren(PageParametersAware.class, new Component.IVisitor()
        {
            public Object component(Component component)
            {
                ((PageParametersAware)component).contributeToPageParameters(parameters);
                return Component.IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        contributeToPageParameters(parameters);
        IRequestTarget target = new BrixNodeRequestTarget((BrixNodeWebPage)getPage(), parameters);
        return target;
    }

    // it is intentional that this class doesn't implement PageParametersContributior
    protected void contributeToPageParameters(BrixPageParameters parameters)
    {

    }

    protected BrixPageParameters getInitialParameters()
    {
        // not sure, should we use exiting parameters from URL?
        // e.g.return BrixPageParameters.getCurrent();
        return new BrixPageParameters();
    }

    @Override
    protected boolean getStatelessHint()
    {
        return true;
    }
}
