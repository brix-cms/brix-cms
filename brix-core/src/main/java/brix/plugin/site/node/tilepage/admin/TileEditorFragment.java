package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.TileContainerNode;

public abstract class TileEditorFragment extends Fragment<JcrNode>
{

    public TileEditorFragment(String id, String markupId, MarkupContainer markupProvider,
            final IModel<JcrNode> nodeModel, final String tileId)
    {
        super(id, markupId, markupProvider, nodeModel);

        final Form form = new Form("form");
        add(form);

        form.add(new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form)));

        final String tileClassName = getTileContainerNode().getTileClassName(tileId);
        final Tile tile = getTileContainerNode().getNodePlugin().getTileOfType(tileClassName);

        final TileEditorPanel editor;

        form.add(editor = tile.newEditor("editor", nodeModel));

        editor.load(getTileContainerNode().getTile(tileId));

        form.add(new Button("submit")
        {
            @Override
            public void onSubmit()
            {
                JcrNode node = (JcrNode)TileEditorFragment.this.getModelObject();
                JcrNode tile = getTileContainerNode().getTile(tileId);
                node.checkout();
                editor.save(tile);
                node.save();
                node.checkin();
            }
        });

        form.add(new Link("delete")
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
                tag
                        .put(
                                "onclick",
                                "if (!confirm('Are you sure you want to remove this tile?')) return false; " +
                                        tag.getAttributes().get("onclick"));
            }

        });
    }

    protected abstract void onDelete(String tileId);

    private TileContainerNode getTileContainerNode()
    {
        return (TileContainerNode)getModelObject();
    }

}
