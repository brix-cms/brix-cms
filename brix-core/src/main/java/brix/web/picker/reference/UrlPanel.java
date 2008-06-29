package brix.web.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import brix.web.generic.BrixGenericPanel;

public class UrlPanel extends BrixGenericPanel<String>
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
