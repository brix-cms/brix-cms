/**
 * 
 */
package brix.plugin.site.node.tilepage.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import brix.jcr.api.JcrNode;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.node.tilepage.TileContainerNode;

class TilesPanel extends NodeManagerPanel
{
    String selectedTileId;
    private Component editor;

    private List<String> getTileIds()
    {
        List<String> result = new ArrayList<String>();
        List<JcrNode> nodes = getTileContainerNode().getTileNodes();
        for (JcrNode node : nodes)
        {
            result.add(TileContainerNode.getTileId(node));
        }
        return result;
    }

    public TilesPanel(String id, IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);

        add(new RefreshingView("tile-selector")
        {

            @SuppressWarnings("unchecked")
            @Override
            protected Iterator getItemModels()
            {

                List<String> tileIds = getTileIds();
                List<String> displayIds = new ArrayList<String>(tileIds.size() + 1);
                displayIds.add(null);
                displayIds.addAll(tileIds);
                return new ModelIteratorAdapter(displayIds.iterator())
                {

                    @Override
                    protected IModel model(Object object)
                    {
                        return new Model((String)object);
                    }

                };
            }

            @Override
            protected void populateItem(Item item)
            {
                item.add(new AbstractBehavior()
                {
                    private Item item;

                    @Override
                    public void bind(Component component)
                    {
                        item = (Item)component;
                    }

                    @Override
                    public void onComponentTag(Component component, ComponentTag tag)
                    {
                        final boolean selected = Objects.equal(selectedTileId, item.getModel()
                                .getObject());
                        if (selected)
                        {
                            tag.put("class", "selected");
                        }
                    }
                });

                Link link = new Link("link", item.getModel())
                {

                    @Override
                    public void onClick()
                    {
                        selectedTileId = (String)getModel().getObject();
                        setupTileEditor();
                    }
                };
                item.add(link);

                link.add(new Label("name", (item.getModelObject() == null)
                        ? "Create New"
                        : (String)item.getModel().getObject()));
            }

        });

        editor = new WebComponent("tile-editor");
        add(editor);

        // init first editor
        setupTileEditor();

    }


    private TileContainerNode getTileContainerNode()
    {
        return (TileContainerNode)getNode();
    }

    private void setupTileEditor()
    {
        Fragment newEditor = null;

        if (Strings.isEmpty(selectedTileId))
        {
            newEditor = new NewTileFragment(editor.getId(), "new-tile-form-fragment", this,
                    getNodeModel())
            {

                @Override
                protected void onAddTile(String tileId, String tileTypeName)
                {
                    JcrNode containerNode = getNode();
                    containerNode.checkout();
                    JcrNode node = getTileContainerNode().createTile(tileId, tileTypeName);
                    getEditor().save(node);
                    containerNode.save();
                    containerNode.checkin();
                    selectedTileId = tileId;
                    setupTileEditor();
                }

            };
        }
        else
        {
            newEditor = new TileEditorFragment(editor.getId(), "editor-form-fragment", this,
                    getNodeModel(), selectedTileId)
            {

                @Override
                protected void onDelete(String tileId)
                {
                    JcrNode tile = getTileContainerNode().getTile(selectedTileId);
                    getNode().checkout();
                    tile.remove();
                    getNode().save();
                    getNode().checkin();
                    selectedTileId = null;
                    setupTileEditor();
                }

            };
        }
        editor.replaceWith(newEditor);
        editor = newEditor;

    }

}