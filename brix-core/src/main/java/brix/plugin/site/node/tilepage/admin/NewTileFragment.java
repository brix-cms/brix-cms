/**
 * 
 */
package brix.plugin.site.node.tilepage.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.tilepage.TileContainerNode;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.web.ContainerFeedbackPanel;
import brix.web.util.validators.NodeNameValidator;

abstract class NewTileFragment extends Fragment<BrixNode>
{
    private String newTileId;
    private String newTileTypeName;
    private Component newTileEditor;

    public TileEditorPanel getEditor()
    {
        if (newTileEditor instanceof TileEditorPanel)
        {
            return (TileEditorPanel)newTileEditor;
        }
        else
        {
            return null;
        }
    }

    public NewTileFragment(String id, String fragmentId, MarkupContainer markupContainer,
            final IModel<BrixNode> nodeModel)
    {
        super(id, fragmentId, markupContainer, nodeModel);
        final Form form = new Form("form");
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", form));
        form.add(new TextField("tile-id", new PropertyModel(this, "newTileId")).setLabel(
                new Model("Tile Id")).setRequired(true).add(new NewTileIdValidator()).add(
                NodeNameValidator.getInstance()));

        form.add(new DropDownChoice("tile-type", new PropertyModel(this, "newTileTypeName"),
                new TileTypeNamesModel(), new TileTypeNameRenderer())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }

            @Override
            protected void onSelectionChanged(Object newSelection)
            {
                final String tileTypeName = (String)newSelection;
                if (tileTypeName == null)
                {
                    EmptyPanel ep = new EmptyPanel(newTileEditor.getId());
                    newTileEditor.replaceWith(ep);
                    newTileEditor = ep;
                }
                else
                {
                    final Tile tile = getCmsPage().getNodePlugin().getTileOfType(tileTypeName);
                    TileEditorPanel ed = tile.newEditor(newTileEditor.getId(), nodeModel);
                    newTileEditor.replaceWith(ed);
                    newTileEditor = ed;
                }
            }

        }.setLabel(new Model("Tile Type")).setRequired(true));

        newTileEditor = new EmptyPanel("tile-editor");
        form.add(newTileEditor);

        form.add(new Button("add")
        {
            @Override
            public void onSubmit()
            {
                onAddTile(newTileId, newTileTypeName);
            }

        });
    }

    private TileContainerNode getCmsPage()
    {
        return (TileContainerNode)getModelObject();
    }

    protected abstract void onAddTile(String tileId, String ntileTypeName);

    class NewTileIdValidator implements IValidator
    {

        public void validate(IValidatable validatable)
        {
            final String tileId = (String)validatable.getValue();
            if (getCmsPage().getTile(tileId) != null)
            {
                validatable.error(new ValidationError()
                        .setMessage("A tile with id ${input} already exists"));
            }
        }
    }

    class TileTypeNamesModel extends LoadableDetachableModel
    {

        @Override
        protected Object load()
        {
            final Collection<Tile> tiles = getCmsPage().getNodePlugin().getTiles();
            List<String> choices = new ArrayList<String>(tiles.size());
            for (Tile tile : tiles)
            {
                choices.add(tile.getTypeName());
            }
            return choices;

        }
    }

    class TileTypeNameRenderer implements IChoiceRenderer
    {

        public Object getDisplayValue(Object object)
        {
            String type = (String)object;
            TileNodePlugin plugin = (TileNodePlugin)getCmsPage().getNodePlugin();
            return plugin.getTileOfType(type).getDisplayName();
        }

        public String getIdValue(Object object, int index)
        {
            return (String)object;
        }

    }
}