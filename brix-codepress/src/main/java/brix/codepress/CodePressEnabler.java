package brix.codepress;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WicketEventReference;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;

public class CodePressEnabler extends AbstractBehavior
{
    private final String language;
    private final boolean lineNumbers;
    private Component owner;

    private static final ResourceReference JS = new ResourceReference(CodePressEnabler.class,
        "codepress.js");

    public CodePressEnabler(String language, boolean lineNumbers)
    {
        this.language = language;
        this.lineNumbers = lineNumbers;
    }

    @Override
    public void bind(Component component)
    {
        if (owner != null)
        {
            throw new IllegalStateException("This behavior is already bound to a component");
        }
        // TODO validate markup id of owner will not contain any funky
        // characters
        // because codepress creates a javascript variable whose name is the
        // html id attribute
        owner = component;
        owner.setOutputMarkupId(true);
    }

    @Override
    public void beforeRender(Component component)
    {
        if (component instanceof FormComponent)
        {
            ((FormComponent)component).getForm().getRootForm().setOutputMarkupId(true);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.renderJavascriptReference(JS);

        if (owner instanceof FormComponent)
        {
            response.renderJavascriptReference(WicketEventReference.INSTANCE);
            final FormComponent fc = (FormComponent)owner;
            final Form form = fc.getForm().getRootForm();
            response.renderOnDomReadyJavascript("Wicket.Event.add(document.getElementById('" +
                    form.getMarkupId() + "'), 'submit', function() { " + fc.getMarkupId() +
                    ".toggleEditor();});");
        }
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag)
    {
        String clazz = (String)tag.getAttributes().get("class");
        clazz = (clazz == null) ? "" : clazz + " ";
        clazz += "codepress ";
        clazz += language;
        clazz += " linenumbers-";
        clazz += (lineNumbers) ? "on" : "off";
        tag.put("class", clazz);
    }
}
