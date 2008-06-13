package brix.plugin.site.page.fragment;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.admin.TilesPanel;

public class FragmentsPanel extends TilesPanel
{

	public FragmentsPanel(String id, IModel<BrixNode> nodeModel)
	{
		super(id, nodeModel);
	}

}
