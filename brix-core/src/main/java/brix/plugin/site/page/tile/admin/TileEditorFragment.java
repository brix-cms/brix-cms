package brix.plugin.site.page.tile.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.tile.Tile;
import brix.web.generic.IGenericComponent;

public abstract class TileEditorFragment extends Fragment implements IGenericComponent<BrixNode>
{

	public TileEditorFragment(String id, String markupId, MarkupContainer markupProvider,
			final IModel<BrixNode> nodeModel, final String tileId, boolean filterFeedback)
	{
		super(id, markupId, markupProvider, nodeModel);

		final Form<Void> form = new Form<Void>("form");
		add(form);

		form.add(new FeedbackPanel("feedback", filterFeedback ? new ContainerFeedbackMessageFilter(form) : null));

		Brix brix = nodeModel.getObject().getBrix();
		final String tileClassName = getTileContainerNode().tiles().getTileClassName(tileId);
		final Tile tile = Tile.Helper.getTileOfType(tileClassName, brix);

		final TileEditorPanel editor;

		form.add(editor = tile.newEditor("editor", nodeModel));

		editor.load(getTileContainerNode().tiles().getTile(tileId));

		form.add(new SubmitLink("submit")
		{
			@Override
			public void onSubmit()
			{
				BrixNode node = TileEditorFragment.this.getModelObject();
				BrixNode tile = getTileContainerNode().tiles().getTile(tileId);
				node.checkout();
				editor.save(tile);
				node.save();
				node.checkin();
				getSession().info(getString("tileSuccessfullySaved"));
			}
		});

		form.add(new Link<Void>("delete")
		{

			@Override
			public void onClick()
			{
				onDelete(tileId);
			}

			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				super.onComponentTag(tag);
				String confirm = TileEditorFragment.this.getString("deleteConfirmation");
				tag.put("onclick", "if (!confirm('" + confirm + "')) return false; "
						+ tag.getAttributes().get("onclick"));
			}

		});
	}

	protected abstract void onDelete(String tileId);

	private AbstractContainer getTileContainerNode()
	{
		return (AbstractContainer) getModelObject();
	}

	@SuppressWarnings("unchecked")
	public IModel<BrixNode> getModel()
	{
		return (IModel<BrixNode>) getDefaultModel();
	}

	public BrixNode getModelObject()
	{
		return (BrixNode) getDefaultModelObject();
	}

	public void setModel(IModel<BrixNode> model)
	{
		setDefaultModel(model);
	}

	public void setModelObject(BrixNode object)
	{
		setDefaultModelObject(object);
	}
}
