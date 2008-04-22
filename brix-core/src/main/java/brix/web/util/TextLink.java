package brix.web.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public abstract class TextLink extends Link
{
    private final IModel textModel;

    public TextLink(String id, IModel textModel)
    {
        super(id);
        this.textModel = textModel;
    }

    public TextLink(String id, IModel model, IModel textModel)
    {
        super(id, model);
        this.textModel = textModel;
    }

    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
    {
        replaceComponentTagBody(markupStream, openTag,
                getModelObjectAsString(textModel.getObject()));
    }

    @Override
    protected void onDetach()
    {
        textModel.detach();
        super.onDetach();
    }
}
