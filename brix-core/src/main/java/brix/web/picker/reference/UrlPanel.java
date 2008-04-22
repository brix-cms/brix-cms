package brix.web.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class UrlPanel extends Panel
{

    public UrlPanel(String id, IModel urlModel)
    {
        super(id);

        TextField tf;
        add(tf = new TextField("url", urlModel));
        tf.add(new AjaxFormComponentUpdatingBehavior("onblur")
        {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {

            }
        });
    }

}
