/**
 * 
 */
package brix.plugin.site.page.tile.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.fragment.TileContainer;
import brix.plugin.site.page.tile.Tile;
import brix.web.ContainerFeedbackPanel;
import brix.web.util.validators.NodeNameValidator;

public abstract class NewTileFragment extends Fragment<BrixNode>
{
	private String newTileId;
	private String newTileTypeName;
	private Component<?> newTileEditor;

	public TileEditorPanel<?> getEditor()
	{
		if (newTileEditor instanceof TileEditorPanel)
		{
			return (TileEditorPanel<?>) newTileEditor;
		}
		else
		{
			return null;
		}
	}

	public NewTileFragment(String id, String fragmentId, MarkupContainer<?> markupContainer,
			final IModel<BrixNode> nodeModel)
	{
		super(id, fragmentId, markupContainer, nodeModel);
		final Form<Void> form = new Form<Void>("form");
		add(form);

		form.add(new ContainerFeedbackPanel("feedback", form));
		form.add(new TextField<String>("tileId", new PropertyModel<String>(this, "newTileId"))
				.setRequired(true).add(new NewTileIdValidator()).add(NodeNameValidator.getInstance()));

		form.add(new DropDownChoice<String>("tileType", new PropertyModel<String>(this, "newTileTypeName"), new TileTypeNamesModel(),
				new TileTypeNameRenderer())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}

			@Override
			protected void onSelectionChanged(String tileTypeName)
			{;
				if (tileTypeName == null)
				{
					EmptyPanel ep = new EmptyPanel(newTileEditor.getId());
					newTileEditor.replaceWith(ep);
					newTileEditor = ep;
				}
				else
				{
					final Brix brix = NewTileFragment.this.getModelObject().getBrix();
					final Tile tile = Tile.Helper.getTileOfType(tileTypeName, brix);
					TileEditorPanel<?> ed = tile.newEditor(newTileEditor.getId(), nodeModel);
					newTileEditor.replaceWith(ed);
					newTileEditor = ed;
				}
			}

		}.setRequired(true));

		newTileEditor = new EmptyPanel("tile-editor");
		form.add(newTileEditor);

		form.add(new SubmitLink<Void>("add")
		{
			@Override
			public void onSubmit()
			{
				onAddTile(newTileId, newTileTypeName);
			}

		});
	}

	private TileContainer getTileContainer()
	{
		return (TileContainer) getModelObject();
	}

	protected abstract void onAddTile(String tileId, String ntileTypeName);

	class NewTileIdValidator implements IValidator
	{

		public void validate(IValidatable validatable)
		{
			final String tileId = (String) validatable.getValue();
			if (getTileContainer().tiles().getTile(tileId) != null)
			{
				validatable.error(new ValidationError().addMessageKey("tileExists"));
			}
		}
	}

	class TileTypeNamesModel extends LoadableDetachableModel<List<? extends String>>
	{

		@Override
		protected List<? extends String> load()
		{
			final Collection<Tile> tiles = Tile.Helper.getTiles(NewTileFragment.this.getModelObject().getBrix());
			List<String> choices = new ArrayList<String>(tiles.size());
			for (Tile tile : tiles)
			{
				choices.add(tile.getTypeName());
			}
			return choices;

		}
	}

	class TileTypeNameRenderer implements IChoiceRenderer<String>
	{

		public Object getDisplayValue(String type)
		{
			return Tile.Helper.getTileOfType(type, getModelObject().getBrix()).getDisplayName();
		}

		public String getIdValue(String object, int index)
		{
			return object;
		}

	}
}