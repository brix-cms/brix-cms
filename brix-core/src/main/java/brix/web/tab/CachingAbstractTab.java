package brix.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class CachingAbstractTab extends AbstractTab 
{

    public CachingAbstractTab(IModel<String> title)
    {
        super(title);
    }


    public boolean isVisible()
    {
        return true;
    }

    public abstract Panel<?> newPanel(String panelId);

    private Panel<?> panel = null;

    @Override
    public Panel<?> getPanel(String panelId)
    {
        if (panel == null)
        {
            panel = newPanel(panelId);
        }
        return panel;
    }
}
