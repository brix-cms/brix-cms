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
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.Strings;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.tile.TileContainerFacet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TilesPanel extends NodeManagerPanel {
    String selectedTileId;
    private Component editor;

    public TilesPanel(String id, IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        add(new RefreshingView<String>("tile-selector") {
            @Override
            protected Iterator<IModel<String>> getItemModels() {
                List<String> tileIds = getTileIds();
                List<String> displayIds = new ArrayList<String>(tileIds.size() + 1);
                displayIds.add(null);
                displayIds.addAll(tileIds);
                return new ModelIteratorAdapter<String>(displayIds.iterator()) {
                    @Override
                    protected IModel<String> model(String object) {
                        return new Model<String>(object);
                    }
                };
            }

            @Override
            protected void populateItem(Item<String> item) {
                item.add(new AbstractBehavior() {
                    private Item<?> item;

                    @Override
                    public void bind(Component component) {
                        item = (Item<?>) component;
                    }

                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) {
                        final boolean selected = Objects.equal(selectedTileId, item.getModel().getObject());
                        if (selected) {
                            tag.put("class", "selected");
                        }
                    }
                });

                Link<String> link = new Link<String>("link", item.getModel()) {
                    @Override
                    public void onClick() {
                        selectedTileId = getModelObject();
                        setupTileEditor();
                    }
                };
                item.add(link);

                link.add(new Label("name", (item.getModelObject() == null) ? TilesPanel.this.getString("createNew")
                        : (String) item.getModel().getObject()));
            }
        });

        editor = new WebComponent("tile-editor");
        add(editor);

        // init first editor
        setupTileEditor();
    }

    private List<String> getTileIds() {
        List<String> result = new ArrayList<String>();
        List<BrixNode> nodes = getTileContainerNode().tiles().getTileNodes();
        for (BrixNode node : nodes) {
            result.add(TileContainerFacet.getTileId(node));
        }
        return result;
    }

    private void setupTileEditor() {
        Fragment newEditor = null;

        if (Strings.isEmpty(selectedTileId)) {
            newEditor = new NewTileFragment(editor.getId(), "new-tile-form-fragment", this, getModel()) {
                @Override
                protected void onAddTile(String tileId, String tileTypeName) {
                    BrixNode containerNode = getTileContainerNode();
                    containerNode.checkout();
                    BrixNode node = getTileContainerNode().tiles().createTile(tileId, tileTypeName);
                    getEditor().save(node);
                    containerNode.save();
                    containerNode.checkin();
                    selectedTileId = tileId;
                    setupTileEditor();
                }
            };
        } else {
            newEditor = new TileEditorFragment(editor.getId(), "editor-form-fragment", this, getModel(),
                    selectedTileId, filterFeedback()) {
                @Override
                protected void onDelete(String tileId) {
                    BrixNode tile = getTileContainerNode().tiles().getTile(selectedTileId);
                    if (tile != null) {
                        getTileContainerNode().checkout();
                        tile.remove();
                        getTileContainerNode().save();
                        getTileContainerNode().checkin();
                    }
                    selectedTileId = null;
                    setupTileEditor();
                }
            };
        }
        editor.replaceWith(newEditor);
        editor = newEditor;
    }

    private AbstractContainer getTileContainerNode() {
        return (AbstractContainer) getModelObject();
    }

    protected boolean filterFeedback() {
        return true;
    }
}