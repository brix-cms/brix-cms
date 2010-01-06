package brix.plugin.site.page.admin;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Default implementation of markup editor factory. Uses a simple textarea.
 * 
 * @author igor.vaynberg
 * 
 */
public class SimpleMarkupEditorFactory implements MarkupEditorFactory
{

    public TextArea<String> newEditor(String id, IModel<String> markup)
    {
        return new TextArea<String>("content", markup);
    }

    public IModel<String> newLabel()
    {
        return new Model<String>("Simple Text");
    }

}
