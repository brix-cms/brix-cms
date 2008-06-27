package brix.plugin.site.picker.node;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.tree.SiteNodeFilter;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.NodePickerPanel;
import brix.web.tree.NodeFilter;

public class SiteNodePickerPanel extends NodePickerPanel
{

	public SiteNodePickerPanel(String id, String workspaceId, boolean foldersOnly, NodeFilter enabledFilter)
	{
		super(id, TreeAwareNode.Util.getTreeNode(SitePlugin.get().getSiteRootNode(workspaceId)),
				new SiteNodeFilter(foldersOnly, null), enabledFilter);
	}

	public SiteNodePickerPanel(String id, IModel<BrixNode> model, String workspaceId, boolean foldersOnly,
			NodeFilter enabledFilter)
	{
		super(id, model, TreeAwareNode.Util.getTreeNode(SitePlugin.get().getSiteRootNode(workspaceId)),
				new SiteNodeFilter(foldersOnly, null), enabledFilter);
	}

	public SiteNodePickerPanel(String id, String workspaceId, NodeFilter enabledFilter)
	{
		this(id, workspaceId, false, enabledFilter);
	}

	public SiteNodePickerPanel(String id, IModel<BrixNode> model, String workspaceId, NodeFilter enabledFilter)
	{
		this(id, model, workspaceId, false, enabledFilter);
	}

}
