package brix.web.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class UrlPanel extends Panel<String>
{

    public UrlPanel(String id, IModel<String> urlModel)
    {
        super(id);

        TextField<String> tf;
        add(tf = new TextField<String>("url", urlModel));
        tf.add(new AjaxFormComponentUpdatingBehavior("onblur")
        {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {

            }
        });
    }

}
