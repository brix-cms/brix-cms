package brix.plugin.menu.tile.fulltree;

import brix.jcr.AbstractNodeAdapter;
import brix.jcr.wrapper.BrixNode;

class NodeAdapter extends AbstractNodeAdapter
{
	private static final String P_MENU = "menu";
	private static final String P_SELECTED_LI_CSS_CLASS = "selectedLiCssClass";
	private static final String P_OUTER_UL_CSS_CLASS = "outerUlCssClass";


	public NodeAdapter(BrixNode node)
	{
		super(node);
	}

	public BrixNode getMenuNode()
	{
		return getProperty(P_MENU, (BrixNode)null);
	}

	public void setMenuNode(BrixNode node)
	{
		setProperty(P_MENU, node);
	}

	public String getSelectedLiCssClass()
	{
		return getProperty(P_SELECTED_LI_CSS_CLASS, (String)null);
	}

	public void setSelectedLiCssClass(String cssClass)
	{
		setProperty(P_SELECTED_LI_CSS_CLASS, cssClass);
	}

	public String getOuterUlCssClass()
	{
		return getProperty(P_OUTER_UL_CSS_CLASS, (String)null);
	}

	public void setOuterUlCssClass(String cssClass)
	{
		setProperty(P_OUTER_UL_CSS_CLASS, cssClass);
	}

}
