/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package org.brixcms.plugin.site.page.tile.admin;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.global.GlobalContainerNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericFragment;
import org.brixcms.web.util.validators.NodeNameValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class NewTileFragment extends BrixGenericFragment<BrixNode> {
    private String newTileId;
    private String newTileTypeName;
    private Component newTileEditor;

    public NewTileFragment(String id, String fragmentId, MarkupContainer markupContainer,
                           final IModel<BrixNode> nodeModel) {
        super(id, fragmentId, markupContainer, nodeModel);
        final Form<Void> form = new Form<Void>("form");
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", form));

        final FormComponent<String> tileId;

        form.add(tileId = new TextField<String>("tileId", new PropertyModel<String>(this, "newTileId")));
        tileId.setRequired(true).add(new NewTileIdValidator()).add(NodeNameValidator.getInstance()).setOutputMarkupId(true);

        IModel<List<? extends String>> idSuggestionModel = new LoadableDetachableModel<List<? extends String>>() {
            @Override
            protected List<? extends String> load() {
                List<String> res = new ArrayList<String>();
                Collection<String> ids = ((AbstractContainer) NewTileFragment.this.getModelObject()).getTileIDs();
                res.addAll(ids);
                return res;
            }
        };

        final DropDownChoice<String> idSuggestion;
        form.add(idSuggestion = new DropDownChoice<String>("idSuggestion", new Model<String>(), idSuggestionModel) {
            @Override
            public boolean isVisible() {
                return NewTileFragment.this.getModelObject() instanceof GlobalContainerNode == false;
            }
        });

        idSuggestion.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                tileId.setModelObject(idSuggestion.getModelObject());
                idSuggestion.setModelObject(null);
                target.addComponent(tileId);
                target.addComponent(idSuggestion);
                target.focusComponent(tileId);
            }
        });
        idSuggestion.setNullValid(true);

        form.add(new DropDownChoice<String>("tileType", new PropertyModel<String>(this, "newTileTypeName"),
                new TileTypeNamesModel(), new TileTypeNameRenderer()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String tileTypeName) {
                ;
                if (tileTypeName == null) {
                    EmptyPanel ep = new EmptyPanel(newTileEditor.getId());
                    newTileEditor.replaceWith(ep);
                    newTileEditor = ep;
                } else {
                    final Brix brix = NewTileFragment.this.getModelObject().getBrix();
                    final Tile tile = Tile.Helper.getTileOfType(tileTypeName, brix);
                    TileEditorPanel ed = tile.newEditor(newTileEditor.getId(), nodeModel);
                    newTileEditor.replaceWith(ed);
                    newTileEditor = ed;
                }
            }

        }.setRequired(true));

        newTileEditor = new EmptyPanel("tile-editor");
        form.add(newTileEditor);

        form.add(new SubmitLink("add") {
            @Override
            public void onSubmit() {
                onAddTile(newTileId, newTileTypeName);
            }
        });
    }

    protected abstract void onAddTile(String tileId, String ntileTypeName);

    public TileEditorPanel getEditor() {
        if (newTileEditor instanceof TileEditorPanel) {
            return (TileEditorPanel) newTileEditor;
        } else {
            return null;
        }
    }

    private AbstractContainer getTileContainer() {
        return (AbstractContainer) getModelObject();
    }

    class NewTileIdValidator implements IValidator {
        public void validate(IValidatable validatable) {
            final String tileId = (String) validatable.getValue();
            if (getTileContainer().tiles().getTile(tileId) != null) {
                validatable.error(new ValidationError().addMessageKey("tileExists"));
            }
        }
    }

    class TileTypeNamesModel extends LoadableDetachableModel<List<? extends String>> {
        @Override
        protected List<? extends String> load() {
            final Collection<Tile> tiles = Tile.Helper.getTiles(NewTileFragment.this.getModelObject().getBrix());
            List<String> choices = new ArrayList<String>(tiles.size());
            for (Tile tile : tiles) {
                choices.add(tile.getTypeName());
            }
            return choices;
        }
    }

    class TileTypeNameRenderer implements IChoiceRenderer<String> {
        public Object getDisplayValue(String type) {
            return Tile.Helper.getTileOfType(type, getModelObject().getBrix()).getDisplayName();
        }

        public String getIdValue(String object, int index) {
            return object;
        }
    }
}