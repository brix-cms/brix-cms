package brix.plugin.site.page.admin;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.admin.navigation.NavigationAwarePanel;

public abstract class TileEditorPanel<T> extends NavigationAwarePanel<T>
{

    public TileEditorPanel(String id)
    {
        super(id);
    }

    public TileEditorPanel(String id, IModel<T> model)
    {
        super(id, model);
    }

    abstract public void load(BrixNode node);

    abstract public void save(BrixNode node);
}
