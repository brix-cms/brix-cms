package brix.web;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.PageParametersAware;
import brix.web.nodepage.PageParametersDropDownChoice;
import brix.web.nodepage.PageParametersForm;

public class StatelessFormPanel extends Panel implements PageParametersAware
{

    public StatelessFormPanel(String id)
    {
        super(id);

        

        Form form = new PageParametersForm("form");
        form.add(new TextField("text", new PropertyModel(this, "text")).setRequired(true));
        add(form);

        List<String> options = Arrays.asList(new String[] { "item1", "item2", "item3" });

        DropDownChoice choice = new PageParametersDropDownChoice("choice", new PropertyModel(this, "selected"),
                options)
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }
            @Override
            protected void onSelectionChanged(Object newSelection)
            {
                super.onSelectionChanged(newSelection);
            }
        };
        form.add(choice);
        
        add(new FeedbackPanel("feedback"));
    }
    
    @Override
    protected void onBeforeRender()
    {      
        super.onBeforeRender();
        text = BrixPageParameters.getCurrent().getQueryParam("text").toString();
        selected = BrixPageParameters.getCurrent().getQueryParam("selected").toString();
    }

    private String text;

    private String selected;

    public void contributeToPageParameters(BrixPageParameters pageParameters)
    {
        pageParameters.setQueryParam("text", text);
        pageParameters.setQueryParam("selected", selected);
    }
    
    @Override
    protected boolean getStatelessHint()
    {
        return true;
    }

    public void initializeFromPageParameters(BrixPageParameters pageParameters)
    {
    }

   
}
