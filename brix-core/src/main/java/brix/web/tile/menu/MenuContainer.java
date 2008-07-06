package brix.web.tile.menu;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;

public class MenuContainer implements IDetachable
{
    IModel<BrixNode> menuNodeModel = new BrixNodeModel();
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
    
    private Integer startAtLevel;
    
    private Integer renderLevels;
    
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

	public Integer getStartAtLevel()
	{
		return startAtLevel;
	}

	public void setStartAtLevel(Integer startAtLevel)
	{
		this.startAtLevel = startAtLevel;
	}

	public Integer getRenderLevels()
	{
		return renderLevels;
	}

	public void setRenderLevels(Integer renderLevels)
	{
		this.renderLevels = renderLevels;
	}
    
    private static final String PROP_OUTER_CONTAINER_STYLE_CLASS = "outerContainerStyleClass";
    
    private static final String PROP_INNER_CONTAINER_STYLE_CLASS = "innerContainerStyleClass";
        
    private static final String PROP_SELECTED_ITEM_STYLE_CLASS = "selectedItemStyleClass";
    
    private static final String PROP_MENU = "menu";
    
    private static final String PROP_START_AT_LEVEL = "startAtLevel";
    
    private static final String PROP_RENDER_LEVELS = "renderLevels";
    
    public void save(BrixNode node)
    {
        node.setProperty(PROP_INNER_CONTAINER_STYLE_CLASS, getInnerContainerStyleClass());
        node.setProperty(PROP_OUTER_CONTAINER_STYLE_CLASS, getOuterContainerStyleClass());
        node.setProperty(PROP_SELECTED_ITEM_STYLE_CLASS, getSelectedItemStyleClass());
        node.setProperty(PROP_MENU, getMenuNode());
        
        if (getStartAtLevel() == null)
        {
        	node.setProperty(PROP_START_AT_LEVEL, (String)null);
        }
        else
        {
        	node.setProperty(PROP_START_AT_LEVEL, getStartAtLevel());
        }
        
        if (getRenderLevels() == null)
        {
        	node.setProperty(PROP_RENDER_LEVELS, (String)null);
        }
        else
        {
        	node.setProperty(PROP_RENDER_LEVELS, getRenderLevels());	
        }        
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
        if (node.hasProperty(PROP_SELECTED_ITEM_STYLE_CLASS))
        {
            setSelectedItemStyleClass(node.getProperty(PROP_SELECTED_ITEM_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_MENU))
        {
            setMenuNode((BrixNode) node.getProperty(PROP_MENU).getNode());
        }
        if (node.hasProperty(PROP_START_AT_LEVEL))
        {
        	setStartAtLevel((int) node.getProperty(PROP_START_AT_LEVEL).getLong());
        }
        if (node.hasProperty(PROP_RENDER_LEVELS))
        {
        	setRenderLevels((int)node.getProperty(PROP_RENDER_LEVELS).getLong());
        }
    }
    
    public void detach()
    {
        menuNodeModel.detach();
        cachedMenu = null;
    }

}
