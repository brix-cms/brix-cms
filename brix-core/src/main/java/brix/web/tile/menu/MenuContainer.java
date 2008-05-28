package brix.web.tile.menu;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;

public class MenuContainer implements IDetachable
{
    IModel<BrixNode> menuNodeModel = new BrixNodeModel(null);
    private Menu cachedMenu;
    
    public void setMenuNode(BrixNode node)
    {
        this.cachedMenu = null;
        this.menuNodeModel.setObject(node);
    }
    
    public BrixNode getMenuNode() 
    {
        return menuNodeModel.getObject();
    }
    
    public Menu getMenu() 
    {
        if (cachedMenu == null && getMenuNode() != null)
        {
            cachedMenu = new Menu();
            cachedMenu.load(getMenuNode());
        }
        return cachedMenu;
    }
    
    private String outerContainerStyleClass;
    
    private String innerContainerStyleClass;
    
    private String selectedItemStyleClass;
    
    private String itemStyleClass;
    
    public String getOuterContainerStyleClass()
    {
        return outerContainerStyleClass;
    }

    public void setOuterContainerStyleClass(String outerContainerStyleClass)
    {
        this.outerContainerStyleClass = outerContainerStyleClass;
    }

    public String getInnerContainerStyleClass()
    {
        return innerContainerStyleClass;
    }

    public void setInnerContainerStyleClass(String innerContainerStyleClass)
    {
        this.innerContainerStyleClass = innerContainerStyleClass;
    }

    public String getSelectedItemStyleClass()
    {
        return selectedItemStyleClass;
    }

    public void setSelectedItemStyleClass(String selectedItemStyleClass)
    {
        this.selectedItemStyleClass = selectedItemStyleClass;
    }

    public String getItemStyleClass()
    {
        return itemStyleClass;
    }

    public void setItemStyleClass(String itemStyleClass)
    {
        this.itemStyleClass = itemStyleClass;
    }

    private static final String PROP_OUTER_CONTAINER_STYLE_CLASS = "outerContainerStyleClass";
    
    private static final String PROP_INNER_CONTAINER_STYLE_CLASS = "innerContainerStyleClass";
    
    private static final String PROP_ITEM_STYLE_CLASS = "itemStyleClass";
    
    private static final String PROP_SELECTED_ITEM_STYLE_CLASS = "selectedItemStyleClass";
    
    private static final String PROP_MENU = "menu";
    
    public void save(BrixNode node)
    {
        node.setProperty(PROP_INNER_CONTAINER_STYLE_CLASS, getInnerContainerStyleClass());
        node.setProperty(PROP_OUTER_CONTAINER_STYLE_CLASS, getOuterContainerStyleClass());
        node.setProperty(PROP_ITEM_STYLE_CLASS, getInnerContainerStyleClass());
        node.setProperty(PROP_SELECTED_ITEM_STYLE_CLASS, getSelectedItemStyleClass());
        node.setProperty(PROP_MENU, getMenuNode());
    }
    
    public void load(BrixNode node)
    {
        if (node.hasProperty(PROP_INNER_CONTAINER_STYLE_CLASS))
        {
            setInnerContainerStyleClass(node.getProperty(PROP_INNER_CONTAINER_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_OUTER_CONTAINER_STYLE_CLASS))
        {
            setOuterContainerStyleClass(node.getProperty(PROP_OUTER_CONTAINER_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_ITEM_STYLE_CLASS))
        {
            setItemStyleClass(node.getProperty(PROP_ITEM_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_SELECTED_ITEM_STYLE_CLASS))
        {
            setSelectedItemStyleClass(node.getProperty(PROP_SELECTED_ITEM_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_MENU))
        {
            setMenuNode((BrixNode) node.getProperty(PROP_MENU).getNode());
        }
    }
    
    public void detach()
    {
        menuNodeModel.detach();
        cachedMenu = null;
    }
}
