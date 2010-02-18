package brix.plugin.menu.tile;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.web.util.AbstractModel;

public class MenuPicker extends Panel
{

	/**
	 * @param id
	 * @param selection
	 *            model containing selected menu node
	 * @param workspaceNode
	 *            model containing any existing node in the repository - used to
	 *            retrieve the workspace name. Usually this is the node that
	 *            represents the tile's parent.
	 */
	public MenuPicker(String id, IModel<BrixNode> selection, IModel<BrixNode> workspaceNode)
	{
		super(id, selection);
		add(new MenuListView("menues", selection, workspaceNode));
	}

	private static class MenuListView extends ListView<BrixNode>
	{
		private final IModel<BrixNode> selection;
		
		public MenuListView(String id, IModel<BrixNode> selection, IModel<BrixNode> workspaceNode)
		{
			super(id, new MenuNodesListModel(workspaceNode));
			this.selection = selection;
		}

		@Override
		protected void populateItem(final ListItem<BrixNode> item)
		{
			Link<Object> select = new Link<Object>("select")
			{
				@Override
				public void onClick()
				{
					selection.setObject(item.getModelObject());
				}

				@Override
				public boolean isEnabled()
				{
					BrixNode current = selection.getObject();
					return current == null || !item.getModelObject().isSame(current);
				}
			};
			IModel<String> labelModel = new AbstractModel<String>()
			{
				@Override
				public String getObject()
				{
					BrixNode node = item.getModelObject();
					Menu menu = new Menu();
					menu.loadName(node);
					return menu.getName();
				}
			};
			select.add(new Label("label", labelModel));
			item.add(select);
		}

		@Override
		protected IModel<BrixNode> getListItemModel(IModel<? extends List<BrixNode>> listViewModel,
				int index)
		{
			List<BrixNode> nodes = listViewModel.getObject();
			return new BrixNodeModel(nodes.get(index));
		}

		@Override
		protected void detachModel()
		{
			super.detachModel();
			selection.detach();
		}
	};

}
