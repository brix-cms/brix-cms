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

package org.brixcms.plugin.menu;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.editor.MenuEditor;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.util.AbstractModel;
import org.brixcms.workspace.Workspace;

import javax.jcr.ReferentialIntegrityException;
import java.util.List;

public class ManageMenuPanel extends BrixGenericPanel<Workspace> {
// ------------------------------ FIELDS ------------------------------
    ;
    private Component editor;

    private IModel<BrixNode> currentNode = new BrixNodeModel();
    private Menu currentMenu = new Menu();

    public ManageMenuPanel(String id, final IModel<Workspace> model) {
        super(id, model);

        IModel<List<BrixNode>> listViewModel = new LoadableDetachableModel<List<BrixNode>>() {
            @Override
            protected List<BrixNode> load() {
                return MenuPlugin.get().getMenuNodes(ManageMenuPanel.this.getModelObject().getId());
            }
        };

        add(new MenuListView("listView", listViewModel));
        setupEditor();

        add(new Link<Object>("newMenu") {
            @Override
            public void onClick() {
                currentMenu = new Menu();
                currentNode.setObject(null);
                setupEditor();
            }

            @Override
            public boolean isEnabled() {
                return currentNode.getObject() != null;
            }
        });
    }

    private void setupEditor() {
        Component editor = new EditorPanel("editor", new PropertyModel<Menu>(this, "currentMenu"));
        if (this.editor == null) {
            add(editor);
        } else {
            this.editor.replaceWith(editor);
        }
        this.editor = editor;
    }

    @Override
    protected void onBeforeRender() {
        String workspaceId = getModelObject().getId();
        BrixNode current = currentNode.getObject();
        if (current != null
                && current.getSession().getWorkspace().getName().equals(workspaceId) == false) {
            currentNode.setObject(null);
            currentMenu = new Menu();
            setupEditor();
        }
        super.onBeforeRender();
    }

    @Override
    protected void onDetach() {
        currentNode.detach();
        currentMenu.detach();
        super.onDetach();
    }

    private void onSelectLinkClicked(BrixNode node) {
        currentNode.setObject(node);
        currentMenu = new Menu();
        currentMenu.load(node);
        setupEditor();
    }

    private class MenuListView extends ListView<BrixNode> {
        public MenuListView(String id, IModel<List<BrixNode>> model) {
            super(id, model);
        }

        @Override
        protected void populateItem(final ListItem<BrixNode> item) {
            Link<Object> select = new Link<Object>("select") {
                @Override
                public void onClick() {
                    onSelectLinkClicked(item.getModelObject());
                }

                @Override
                public boolean isEnabled() {
                    BrixNode myNode = item.getModelObject();
                    BrixNode selectedNode = currentNode.getObject();
                    if (selectedNode != null) {
                        return !Objects.equal(myNode.getIdentifier(), selectedNode.getIdentifier());
                    } else {
                        return true;
                    }
                }
            };
            IModel<String> labelModel = new AbstractModel<String>() {
                @Override
                public String getObject() {
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
                                                    int index) {
            List<BrixNode> nodes = listViewModel.getObject();
            return new BrixNodeModel(nodes.get(index));
        }
    }

    private class EditorPanel extends BrixGenericPanel<Menu> {
        public EditorPanel(String id, final IModel<Menu> model) {
            super(id, model);

            Form<Menu> form = new Form<Menu>("form", new CompoundPropertyModel<Menu>(model));
            add(form);

            form.add(new TextField<String>("name").setRequired(true));

            form.add(new MenuEditor("editor", model));

            form.add(new SubmitLink("save") {
                @Override
                public void onSubmit() {
                    MenuPlugin plugin = MenuPlugin.get();
                    currentNode.setObject(plugin.saveMenu(model.getObject(), ManageMenuPanel.this
                            .getModelObject().getId(), currentNode.getObject()));
                    getSession().info(ManageMenuPanel.this.getString("menuSaved"));
                }
            });

            form.add(new SubmitLink("delete") {
                @Override
                public void onSubmit() {
                    try {
                        JcrSession session = currentNode.getObject().getSession();
                        currentNode.getObject().remove();
                        session.save();

                        currentNode.setObject(null);
                        currentMenu = new Menu();
                        setupEditor();
                    } catch (JcrException e) {
                        if (e.getCause() instanceof ReferentialIntegrityException) {
                            currentNode.getObject().getSession().refresh(false);
                            currentNode.detach();
                            getSession().error(
                                    "Couldn't delete menu, it is referenced from a tile(s).");
                        }
                    }

                }

                @Override
                public boolean isVisible() {
                    return currentNode.getObject() != null;
                }
            }.setDefaultFormProcessing(false));

            add(new FeedbackPanel("feedback"));
        }
    }
}
