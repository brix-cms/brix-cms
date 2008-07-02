package brix.web.picker.node;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.picker.common.NodePickerTreeGridPanel;
import brix.web.picker.common.TreeAwareNode;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

import com.inmethod.grid.treegrid.TreeGrid;

public class NodePicker extends BrixGenericPanel<BrixNode>
{
	private final JcrTreeNode rootNode;
	private final NodePickerTreeGridPanel grid;

	public NodePicker(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter)
	{
		super(id, model);

		this.rootNode = rootNode;

		add(grid = new NodePickerTreeGridPanel("grid", visibilityFilter, enabledFilter)
		{
			@Override
			protected JcrTreeNode getRootNode()
			{
				return NodePicker.this.rootNode;
			}

			@Override
			protected void configureGrid(TreeGrid grid)
			{
				super.configureGrid(grid);
				grid.setAllowSelectMultiple(false);
				updateSelection();
				NodePicker.this.configureGrid(grid);
			}

			@Override
			protected void onNodeSelected(BrixNode node)
			{
				NodePicker.this.setModelObject(node);
			}

			@Override
			protected void onNodeDeselected(BrixNode node)
			{
				NodePicker.this.setModelObject(null);
			}
		});
	}

	@Override
	protected void onBeforeRender()
	{
		// First time updateSelection has been rendered from within
		// NodePickerTreePanel#configureGrid

		// In all subsequent renders it's called from here. It must be called
		// before super.onBeforeRender so that the expanded tree items get
		// chance to render
		if (hasBeenRendered())
		{
			updateSelection();
		}

		super.onBeforeRender();

	}

	private void updateSelection()
	{
		BrixNode current = getModelObject();
		if (current == null)
		{
			grid.getGrid().resetSelectedItems();
		}
		else
		{
			JcrTreeNode node = TreeAwareNode.Util.getTreeNode(getModelObject(), grid.getVisibilityFilter());
			if (node == null)
			{
				grid.getGrid().resetSelectedItems();
			}
			else
			{
				grid.getGrid().selectItem(new Model<JcrTreeNode>(node), true);
			}
		}
	}

	protected void configureGrid(TreeGrid grid)
	{

	}
	
	@Override
	protected void onDetach()
	{
		this.rootNode.detach();
		super.onDetach();
	}
}
