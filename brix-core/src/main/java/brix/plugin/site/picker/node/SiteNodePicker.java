package brix.plugin.site.picker.node;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.tree.SiteNodeFilter;
import brix.web.picker.common.TreeAwareNode;
import brix.web.picker.node.NodePicker;
import brix.web.tree.NodeFilter;

public class SiteNodePicker extends NodePicker
{

	public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceId, boolean foldersOnly,
			NodeFilter enabledFilter)
	{
		super(id, model, TreeAwareNode.Util.getTreeNode(SitePlugin.get().getSiteRootNode(workspaceId)),
				new SiteNodeFilter(foldersOnly, null), enabledFilter);
	}

	public SiteNodePicker(String id, IModel<BrixNode> model, String workspaceName, NodeFilter enabledFilter)
	{
		this(id, model, workspaceName, false, enabledFilter);
	}

}
