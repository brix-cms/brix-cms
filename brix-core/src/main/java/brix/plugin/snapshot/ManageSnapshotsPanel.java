package brix.plugin.snapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebResponse;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.exception.BrixException;
import brix.jcr.api.JcrSession;
import brix.plugin.snapshot.auth.CreateSnapshotAction;
import brix.plugin.snapshot.auth.DeleteSnapshotAction;
import brix.plugin.snapshot.auth.RestoreSnapshotAction;
import brix.web.admin.AdminPanel;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class ManageSnapshotsPanel extends NavigationAwarePanel<Workspace>
{

    public ManageSnapshotsPanel(String id, IModel<Workspace> model)
    {
        super(id, model);

        IModel<List<Workspace>> snapshotsModel = new LoadableDetachableModel<List<Workspace>>()
        {
            @Override
            protected List<Workspace> load()
            {
                List<Workspace> list = SnapshotPlugin.get().getSnapshotsForWorkspace(
                    getModelObject());
                return BrixRequestCycle.Locator.getBrix().filterVisibleWorkspaces(list,
                    Context.ADMINISTRATION);
            }

        };

        add(new ListView<Workspace>("snapshots", snapshotsModel)
        {
            @Override
            protected IModel<Workspace> getListItemModel(IModel<List<Workspace>> listViewModel,
                    int index)
            {
                return new WorkspaceModel(listViewModel.getObject().get(index));
            }

            @Override
            protected void populateItem(final ListItem<Workspace> item)
            {
                Workspace workspace = item.getModelObject();
                final String name = SnapshotPlugin.get().getUserVisibleName(workspace, true);

                Link<Object> link = new Link<Object>("browse")
                {
                    @Override
                    public void onClick()
                    {
                        Workspace workspace = item.getModelObject();
                        AdminPanel adminPanel = findParent(AdminPanel.class);
                        adminPanel.setWorkspace(workspace.getId(), name);
                    }
                };
                item.add(link);

                item.add(new Link<Void>("restore")
                {
                    @Override
                    public void onClick()
                    {
                        Workspace target = ManageSnapshotsPanel.this.getModelObject();
                        SnapshotPlugin.get().restoreSnapshot(item.getModelObject(), target);
                    }

                    @Override
                    public boolean isVisible()
                    {
                        Workspace target = ManageSnapshotsPanel.this.getModelObject();
                        Action action = new RestoreSnapshotAction(Context.ADMINISTRATION, item
                            .getModelObject(), target);
                        return BrixRequestCycle.Locator.getBrix().getAuthorizationStrategy()
                            .isActionAuthorized(action);
                    }
                });

                item.add(new Link<Void>("delete")
                {
                    @Override
                    public void onClick()
                    {
                        Workspace snapshot = item.getModelObject();
                        snapshot.delete();
                    }

                    @Override
                    public boolean isVisible()
                    {
                        Action action = new DeleteSnapshotAction(Context.ADMINISTRATION, item
                            .getModelObject());
                        return BrixRequestCycle.Locator.getBrix().getAuthorizationStrategy()
                            .isActionAuthorized(action);
                    }
                });

                item.add(new Label<String>("label", name));
            }
        });

        add(new Link<Object>("createSnapshot")
        {
            @Override
            public void onClick()
            {
                SnapshotPlugin.get().createSnapshot(ManageSnapshotsPanel.this.getModelObject());
            }

            @Override
            public boolean isVisible()
            {
                Workspace current = ManageSnapshotsPanel.this.getModelObject();
                Action action = new CreateSnapshotAction(Context.ADMINISTRATION, current);
                return BrixRequestCycle.Locator.getBrix().getAuthorizationStrategy()
                    .isActionAuthorized(action);
            }
        });

        add(new Link<Object>("downloadWorkspace")
        {
            @Override
            public void onClick()
            {
                getRequestCycle().setRequestTarget(new IRequestTarget()
                {

                    public void detach(RequestCycle requestCycle)
                    {
                    }

                    public void respond(RequestCycle requestCycle)
                    {
                        WebResponse resp = (WebResponse)requestCycle.getResponse();
                        resp.setAttachmentHeader("workspace.xml");
                        String id = ManageSnapshotsPanel.this.getModelObject().getId();
                        JcrSession session = BrixRequestCycle.Locator.getSession(id);
                        Brix brix = BrixRequestCycle.Locator.getBrix();
                        session.exportSystemView(brix.getRootPath(), resp.getOutputStream(), false,
                            false);
                    }

                });
            }
        });

        Form<Object> uploadForm = new Form<Object>("uploadForm")
        {
            @Override
            public boolean isVisible()
            {
                Workspace target = ManageSnapshotsPanel.this.getModelObject();
                Action action = new RestoreSnapshotAction(Context.ADMINISTRATION, target);
                return BrixRequestCycle.Locator.getBrix().getAuthorizationStrategy()
                    .isActionAuthorized(action);
            }
        };

        final FileUploadField upload = new FileUploadField("upload");
        uploadForm.add(upload);

        uploadForm.add(new Button<Object>("submit")
        {
            @Override
            public void onSubmit()
            {
                FileUpload u = upload.getFileUpload();
                if (u != null)
                {
                    try
                    {
                        InputStream s = u.getInputStream();
                        String id = ManageSnapshotsPanel.this.getModelObject().getId();
                        JcrSession session = BrixRequestCycle.Locator.getSession(id);
                        Brix brix = BrixRequestCycle.Locator.getBrix();
                        if (session.itemExists(brix.getRootPath()))
                        {
                            session.getItem(brix.getRootPath()).remove();
                            session.save();
                        }
                        session.importXML("/", s,
                            ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
                        session.save();
                    }
                    catch (IOException e)
                    {
                        throw new BrixException(e);
                    }
                }
            }
        });

        add(uploadForm);
    }

}
