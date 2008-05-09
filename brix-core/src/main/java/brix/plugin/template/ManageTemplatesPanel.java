package brix.plugin.template;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.plugin.site.SitePlugin;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.web.admin.AdminPanel;
import brix.web.admin.navigation.NavigationAwarePanel;

public class ManageTemplatesPanel extends NavigationAwarePanel<Object>
{
    private final String workspaceName;

    public ManageTemplatesPanel(String id, final String workspaceName)
    {
        super(id);

        this.workspaceName = workspaceName;

        IModel<List<String>> templatesModel = new LoadableDetachableModel<List<String>>()
        {
            @Override
            protected List<String> load()
            {
                return TemplatePlugin.get().getTemplates();
            }
        };

        add(new ListView<String>("templates", templatesModel)
        {
            @Override
            protected void populateItem(final ListItem<String> item)
            {
                item.add(new Label<String>("label", item.getModel()));
                item.add(new Link<Object>("browse")
                {
                    @Override
                    public void onClick()
                    {
                        AdminPanel panel = findParent(AdminPanel.class);
                        String label = item.getModelObject();
                        String workspace = TemplatePlugin.get().getTemplateWorkspaceName(label);
                        panel.setWorkspace(workspace, label);
                    }
                });
                item.add(new Link<Object>("restore")
                {
                    @Override
                    public void onClick()
                    {
                        String templateWorkspace = TemplatePlugin.get().getTemplateWorkspaceName(
                            item.getModelObject());
                        TemplatePlugin.get().restoreTemplateSnapshot(templateWorkspace,
                            workspaceName);
                    }

                    @Override
                    public boolean isVisible()
                    {
                        return isCurrentWorkspaceSiteDevelopment();
                    }
                });
            }
        });

        Form<Object> form = new Form<Object>("form")
        {
            @Override
            public boolean isVisible()
            {
                return isCurrentworkspaceSiteOrSnapshot();
            }
        };
        TextField<String> templateName = new TextField<String>("templateName",
            new PropertyModel<String>(this, "templateName"));
        form.add(templateName);

        templateName.setRequired(true);
        templateName.add(StringValidator.maximumLength(24));
        templateName.add(new TemplateNameValidator());
        templateName.add(new UniqueTemplateNameValidator());

        form.add(new Button<Object>("submit")
        {
            @Override
            public void onSubmit()
            {
                TemplatePlugin.get().createTemplate(workspaceName, ManageTemplatesPanel.this.templateName);
            }
        });
        
        add(form);
        
        add(new FeedbackPanel("feedback"));
    }

    private String templateName;

    private class TemplateNameValidator implements IValidator
    {
        public void validate(IValidatable validatable)
        {
            String name = (String)validatable.getValue();
            if (!TemplatePlugin.get().isValidTemplateName(name))
            {
                validatable.error(new ValidationError().addMessageKey("TemplateNameValidator"));
            }
        }
    };

    private class UniqueTemplateNameValidator implements IValidator
    {
        public void validate(IValidatable validatable)
        {
            String name = (String)validatable.getValue();
            if (TemplatePlugin.get().getTemplates().contains(name))
            {
                validatable.error(new ValidationError()
                    .addMessageKey("UniqueTemplateNameValidator"));
            }
        }
    }

    private boolean isCurrentworkspaceSiteOrSnapshot()
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        String prefix = brix.getWorkspaceResolver().getWorkspacePrefix(workspaceName);
        return SitePlugin.PREFIX.equals(prefix) || SnapshotPlugin.PREFIX.equals(prefix);
    }

    private boolean isCurrentWorkspaceSiteDevelopment()
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        return SitePlugin.PREFIX.equals(brix.getWorkspaceResolver().getWorkspacePrefix(workspaceName)) &&
            Brix.STATE_DEVELOPMENT.equals(brix.getWorkspaceResolver().getWorkspaceState(
                workspaceName));
    }

}
