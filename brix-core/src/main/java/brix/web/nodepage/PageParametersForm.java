package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;


public class PageParametersForm extends StatelessForm
{

    public PageParametersForm(String id)
    {
        super(id);
    }

    public PageParametersForm(String id, IModel model)
    {
        super(id, model);
    }

    @Override
    protected String getInputNamePrefix()
    {
        return "brix:";
    }

    @Override
    protected void onSubmit()
    {
        super.onSubmit();
        getRequestCycle().setRequestTarget(getRequestTarget());
    }

    protected IRequestTarget getRequestTarget()
    {
        final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
        getPage().visitChildren(PageParametersAware.class, new IVisitor()
        {
            public Object component(Component component)
            {
                ((PageParametersAware)component).contributeToPageParameters(parameters);
                return IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        contributeToPageParameters(parameters);
        IRequestTarget target = new BrixNodeRequestTarget((BrixNodeWebPage)getPage(), parameters);
        return target;
    }
    
    @Override
    protected void writeParamsAsHiddenFields(String[] params, AppendingStringBuffer buffer)
    {
        for (int j = 0; j < params.length; j++)
        {
            String[] pair = params[j].split("=");

            if (pair[0].startsWith(getInputNamePrefix()))
            {
                buffer.append("<input type=\"hidden\" name=\"").append(pair[0]).append(
                    "\" value=\"").append(pair.length > 1 ? pair[1] : "").append("\" />");
            }
        }
    }

    @Override
    protected boolean encodeUrlInHiddenFields()
    {
        return true;
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        BrixNodeWebPage page = (BrixNodeWebPage)getPage();
        BrixPageParameters parameters = new BrixPageParameters(page.getBrixPageParameters());
        for (String s : parameters.getQueryParamKeys())
        {
            if (s.startsWith("brix:") || s.equals("0"))
            {
                parameters.removeQueryParam(s);
            }
        }
        tag.put("action", RequestCycle.get().urlFor(new BrixNodeRequestTarget(page, parameters)));
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

}
