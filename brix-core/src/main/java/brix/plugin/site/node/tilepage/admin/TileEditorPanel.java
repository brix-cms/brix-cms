package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
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

    abstract public void load(JcrNode node);

    abstract public void save(JcrNode node);
}
