package brix.web.admin.navigation;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class NavigationAwarePanel<T> extends Panel<T>
{

    public NavigationAwarePanel(String id, IModel<T> model)
    {
        super(id, model);
    }

    public NavigationAwarePanel(String id)
    {
        super(id);
    }    

    private transient NavigationContainer navigationContainer;
    
    public Navigation getNavigation()
    {
        if (navigationContainer == null)
        {
            navigationContainer = findParent(NavigationContainer.class);
        }
        if (navigationContainer == null)
        {
            throw new IllegalStateException("Couldn't not find a 'Navigation' parent component.");
        }
        return navigationContainer.getNavigation();
    }
    
}
