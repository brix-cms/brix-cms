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

package org.brixcms.plugin.snapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.brixcms.Brix;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.exception.BrixException;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.snapshot.auth.CreateSnapshotAction;
import org.brixcms.plugin.snapshot.auth.DeleteSnapshotAction;
import org.brixcms.plugin.snapshot.auth.RestoreSnapshotAction;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceModel;

public class ManageSnapshotsPanel extends BrixGenericPanel<Workspace> {
    public ManageSnapshotsPanel(String id, final IModel<Workspace> model) {
        super(id, model);

        add(new FeedbackPanel("feedback"));

        IModel<List<Workspace>> snapshotsModel = new LoadableDetachableModel<List<Workspace>>() {
            @Override
            protected List<Workspace> load() {
                List<Workspace> list = SnapshotPlugin.get().getSnapshotsForWorkspace(
                        getModelObject());
                return getBrix().filterVisibleWorkspaces(list, Context.ADMINISTRATION);
            }
        };

        add(new ListView<Workspace>("snapshots", snapshotsModel) {
            @Override
            protected IModel<Workspace> getListItemModel(IModel<? extends List<Workspace>> listViewModel,
                                                         int index) {
                return new WorkspaceModel(listViewModel.getObject().get(index));
            }

            @Override
            protected void populateItem(final ListItem<Workspace> item) {
                Workspace workspace = item.getModelObject();
                final String name = SnapshotPlugin.get().getUserVisibleName(workspace, true);
                final String comment = SnapshotPlugin.get().getComment(workspace);

                Link<Object> link = new Link<Object>("browse") {
                    @Override
                    public void onClick() {
                        Workspace workspace = item.getModelObject();
                        model.setObject(workspace);
                    }
                };
                item.add(link);

                Link restoreLink = new Link<Void>("restore") {
                    @Override
                    public void onClick() {
                        Workspace target = ManageSnapshotsPanel.this.getModelObject();
                        SnapshotPlugin.get().restoreSnapshot(item.getModelObject(), target);
                        getSession().info(ManageSnapshotsPanel.this.getString("restoreSuccessful"));
                    }

                    /**
                     * Take care that restoring is only allowed in case the workspaces aren't the same
                     */
                    @Override
                    public boolean isEnabled() {
                        if (item.getModelObject().getId().equals(ManageSnapshotsPanel.this.getModelObject().getId())) {
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public boolean isVisible() {
                        Workspace target = ManageSnapshotsPanel.this.getModelObject();
                        Action action = new RestoreSnapshotAction(Context.ADMINISTRATION, item
                                .getModelObject(), target);
                        return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
                    }
                };

                /*
                 * in case the link is enabled, make sure it is intended...
                 */
                if (restoreLink.isEnabled()) {
                    restoreLink.add(new SimpleAttributeModifier("onClick", "return confirm('" + getLocalizer().getString("restoreOnClick", this) + "')"));
                }

                item.add(restoreLink);

                item.add(new Link<Void>("delete") {
                    @Override
                    public void onClick() {
                        Workspace snapshot = item.getModelObject();
                        snapshot.delete();
                    }

                    @Override
                    public boolean isVisible() {
                        Action action = new DeleteSnapshotAction(Context.ADMINISTRATION, item
                                .getModelObject());
                        return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
                    }
                });

                item.add(new Label("label", name));

                item.add(new Label("commentlabel", comment));
            }
        });


        add(new Link<Object>("downloadWorkspace") {
            @Override
            public void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new IRequestHandler() {
                    public void detach(IRequestCycle requestCycle) {
                    }

                    public void respond(IRequestCycle requestCycle) {
                        WebResponse resp = (WebResponse) requestCycle.getResponse();
                        resp.setAttachmentHeader("workspace.xml");
                        String id = ManageSnapshotsPanel.this.getModelObject().getId();
                        Brix brix = getBrix();
                        JcrSession session = brix.getCurrentSession(id);
                        HttpServletResponse containerResponse = (HttpServletResponse) resp.getContainerResponse();
                        ServletOutputStream containerResponseOutputStream = null;
                        try {
                            containerResponseOutputStream = containerResponse.getOutputStream();
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        session.exportSystemView(brix.getRootPath(), containerResponseOutputStream, false, false);
                    }
                });
            }
        });

        /**
         * Form to create a new Snapshot and put any comment to it
         */
        Form<Object> commentForm = new Form<Object>("commentForm") {
            @Override
            public boolean isVisible() {
                Workspace target = ManageSnapshotsPanel.this.getModelObject();
                Action action = new CreateSnapshotAction(Context.ADMINISTRATION, target);
                return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }
        };

        final TextArea<String> area = new TextArea<String>("area", new Model<String>());
        commentForm.add(area);

        commentForm.add(new SubmitLink("createSnapshot") {
            /**
             * @see org.apache.wicket.markup.html.form.IFormSubmittingComponent#onSubmit()
             */
            @Override
            public void onSubmit() {
                String comment = area.getModelObject();
                SnapshotPlugin.get().createSnapshot(ManageSnapshotsPanel.this.getModelObject(), comment);
                area.setModelObject("");
            }
        });
        add(commentForm);


        Form<Object> uploadForm = new Form<Object>("uploadForm") {
            @Override
            public boolean isVisible() {
                Workspace target = ManageSnapshotsPanel.this.getModelObject();
                Action action = new RestoreSnapshotAction(Context.ADMINISTRATION, target);
                return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }
        };

        final FileUploadField upload = new FileUploadField("upload", new Model());
        uploadForm.add(upload);

        uploadForm.add(new SubmitLink("submit") {
            @Override
            public void onSubmit() {
                List<FileUpload> uploadList = upload.getModelObject();
                if (uploadList != null) {
                    for (FileUpload u : uploadList) {
                        try {
                            InputStream s = u.getInputStream();
                            String id = ManageSnapshotsPanel.this.getModelObject().getId();
                            Brix brix = getBrix();
                            JcrSession session = brix.getCurrentSession(id);

                            if (session.itemExists(brix.getRootPath())) {
                                session.getItem(brix.getRootPath()).remove();
                            }
                            session.importXML("/", s,
                                    ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
                            session.save();

                            brix.initWorkspace(ManageSnapshotsPanel.this.getModelObject(), session);

                            getSession().info(ManageSnapshotsPanel.this.getString("restoreSuccessful"));
                        } catch (IOException e) {
                            throw new BrixException(e);
                        }
                    }
                }
            }
        });

        add(uploadForm);
    }

    private Brix getBrix() {
        // TODO: We don't really have a node here
        return Brix.get();
    }
}