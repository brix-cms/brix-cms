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
import brix.exception.BrixException;
import brix.jcr.api.JcrSession;
import brix.web.admin.AdminPanel;
import brix.web.admin.navigation.NavigationAwarePanel;

public class ManageSnapshotsPanel extends NavigationAwarePanel<Object>
{
    private final String currentWorkspaceName;

    public ManageSnapshotsPanel(String id, final String currentWorkspaceName)
    {
        super(id);
        this.currentWorkspaceName = currentWorkspaceName;

        IModel<List<String>> snapshotsModel = new LoadableDetachableModel<List<String>>()
        {
            @Override
            protected List<String> load()
            {
                return SnapshotPlugin.get().getSnapshotsForWorkspace(currentWorkspaceName);
            }

        };

        add(new ListView<String>("snapshots", snapshotsModel)
        {

            @Override
            protected void populateItem(final ListItem<String> item)
            {
                final String formatted = SnapshotPlugin.get().getSnapshotSuffixFormatted(item.getModelObject());

                String suffix = item.getModelObject();
                final Brix brix = BrixRequestCycle.Locator.getBrix();
                final String id = brix.getWorkspaceResolver().getWorkspaceId(currentWorkspaceName);
                final String workspaceName = brix.getWorkspaceResolver().getWorkspaceName(
                    SnapshotPlugin.PREFIX, id, suffix);

                Link<Object> link = new Link<Object>("browse")
                {
                    @Override
                    public void onClick()
                    {


                        AdminPanel adminPanel = findParent(AdminPanel.class);
                        String visibleName = brix.getWorkspaceResolver()
                            .getUserVisibleWorkspaceName(id);
                        adminPanel.setWorkspace(workspaceName, "Snapshot " + visibleName + " " +
                            formatted);
                    }
                };
                item.add(link);

                item.add(new Link("restore")
                {
                    @Override
                    public void onClick()
                    {
                        SnapshotPlugin.get().restoreSnapshot(workspaceName);
                    }
                });

                item.add(new Label<String>("label", formatted));
            }
        });

        add(new Link<Object>("createSnapshot")
        {
            @Override
            public void onClick()
            {
                SnapshotPlugin.get().createSnapshot(currentWorkspaceName);
            }

            @Override
            public boolean isVisible()
            {
                return !isCurrentWorkspaceSnapshot();
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
                        JcrSession session = BrixRequestCycle.Locator
                            .getSession(currentWorkspaceName);
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
                return !isCurrentWorkspaceSnapshot();
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
                        JcrSession session = BrixRequestCycle.Locator
                            .getSession(currentWorkspaceName);
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

    private boolean isCurrentWorkspaceSnapshot()
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        return brix.getWorkspaceResolver().getWorkspacePrefix(currentWorkspaceName).equals(
            SnapshotPlugin.PREFIX);
    }

}
