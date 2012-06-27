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

package org.brixcms.plugin.site.page.tile.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.web.generic.IGenericComponent;

public abstract class TileEditorFragment extends Fragment implements IGenericComponent<BrixNode> {
    public TileEditorFragment(String id, String markupId, MarkupContainer markupProvider,
                              final IModel<BrixNode> nodeModel, final String tileId, boolean filterFeedback) {
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

        form.add(new SubmitLink("submit") {
            @Override
            public void onSubmit() {
                BrixNode node = TileEditorFragment.this.getModelObject();
                BrixNode tile = getTileContainerNode().tiles().getTile(tileId);
                node.checkout();
                editor.save(tile);
                node.save();
                node.checkin();
                getSession().info(getString("tileSuccessfullySaved"));
            }
        });

        form.add(new Link<Void>("delete") {
            @Override
            public void onClick() {
                onDelete(tileId);
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                String confirm = TileEditorFragment.this.getString("deleteConfirmation");
                tag.put("onclick", "if (!confirm('" + confirm + "')) return false; "
                        + tag.getAttributes().get("onclick"));
            }
        });
    }

    public BrixNode getModelObject() {
        return (BrixNode) getDefaultModelObject();
    }

    protected abstract void onDelete(String tileId);


    @SuppressWarnings("unchecked")
    public IModel<BrixNode> getModel() {
        return (IModel<BrixNode>) getDefaultModel();
    }

    public void setModel(IModel<BrixNode> model) {
        setDefaultModel(model);
    }

    public void setModelObject(BrixNode object) {
        setDefaultModelObject(object);
    }

    private AbstractContainer getTileContainerNode() {
        return (AbstractContainer) getModelObject();
    }
}
