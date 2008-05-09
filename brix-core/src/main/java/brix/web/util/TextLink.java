package brix.web.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public abstract class TextLink<T> extends Link<T>
{
    private final IModel<String> textModel;

    public TextLink(String id, IModel<String> textModel)
    {
        super(id);
        this.textModel = wrap(textModel);
    }

    public TextLink(String id, IModel<T> model, IModel<String> textModel)
    {
        super(id, model);
        this.textModel = wrap(textModel);
    }

    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
    {
        replaceComponentTagBody(markupStream, openTag, textModel.getObject());
    }

    @Override
    protected void onDetach()
    {
        textModel.detach();
        super.onDetach();
    }
}
