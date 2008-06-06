package brix.demo.web.tile;

import java.text.SimpleDateFormat;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.TileEditorPanel;

public class TimeTileEditor extends TileEditorPanel
{

    public TimeTileEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        super(id);
        add(new TextField("format", new PropertyModel(this, "format"))
                .setLabel(new Model("format")).add(new IValidator()
                {

                    public void validate(IValidatable validatable)
                    {
                        String expr = validatable.getValue().toString();
                        try
                        {
                            new SimpleDateFormat(expr);
                        }
                        catch (IllegalArgumentException e)
                        {
                            validatable.error(new ValidationError()
                                    .setMessage("${input} is an illegal date format pattern"));
                        }
                    }

                }));


    }

    private String format;


    @Override
    public void load(BrixNode node)
    {
        if (node.hasProperty("format"))
        {
            format = node.getProperty("format").getString();
        }
    }

    @Override
    public void save(BrixNode node)
    {
        node.setProperty("format", format);
    }

}
