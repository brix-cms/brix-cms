package brix.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.IModel;

public abstract class AbstractBrixTab extends AbstractTab implements BrixTab
{

    public AbstractBrixTab(IModel title)
    {
        super(title);
    }


    public boolean isVisible()
    {
        return true;
    }

}
