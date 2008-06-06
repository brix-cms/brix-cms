/**
 * 
 */
package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.codepress.CodePressEnabler;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.tinymce.TinyMceEnabler;
import brix.web.ContainerFeedbackPanel;
import brix.web.model.ModelBuffer;
import brix.web.picker.node.NodeFilter;
import brix.web.picker.node.NodePickerPanel;
import brix.web.picker.node.NodeTypeFilter;


class EditTab extends NodeManagerPanel
{

    private boolean codeEditorEnabled = true;
    private boolean wysiwygEditorEnabled = false;

    public EditTab(String id, final IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        Brix brix = getModelObject().getBrix();
        final boolean useCodepress = brix.getConfig().getAdminConfig().isEnableCodePress();
        final boolean useWysiwyg = brix.getConfig().getAdminConfig().isEnableWysiwyg();

        Form form = new Form("form");
        add(form);

        final ModelBuffer adapter = new ModelBuffer(nodeModel);

        form.add(new TextField("title", adapter.forProperty("title")));

        String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
        NodeFilter filter = new NodeTypeFilter(TileTemplateNodePlugin.TYPE);
        form.add(new NodePickerPanel("templatePicker", adapter.forNodeProperty("template"),
            workspace, filter));

        form.add(new CheckBox("requiresSSL", adapter.forProperty("requiresSSL")));

        TextArea content = new TextArea("content", adapter.forProperty("dataAsString"));
        form.add(content);

        if (useCodepress)
        {
            content.add(new CodePressEnabler("html", true)
            {
                @Override
                public boolean isEnabled(Component component)
                {
                    return codeEditorEnabled;
                }
            });
        }
        if (useWysiwyg)
        {
            content.add(new TinyMceEnabler()
            {
                @Override
                public boolean isEnabled(Component component)
                {
                    return wysiwygEditorEnabled;
                }
            });
        }
        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new DisableEditorsButton("disable-editors"));
        form.add(new EnableCodeEditorButton("enable-code-editor").setVisible(useCodepress));
        form.add(new EnableWysiwygEditorButton("enable-wysiwig-editor")
            .setVisibilityAllowed(useWysiwyg));

        form.add(new Button("save")
        {
            @Override
            public void onSubmit()
            {
                JcrNode node = nodeModel.getObject();
                node.checkout();
                adapter.apply();
                node.save();
                node.checkin();

                getSession().info(getString("status.saved"));
            }
        });

        form.add(new Link("cancel")
        {

            @Override
            public void onClick()
            {
                getSession().info(getString("status.cancelled"));
                EditTab me = EditTab.this;
                me.replaceWith(new EditTab(me.getId(), me.getModel()));
            }

        });
    }

    private class EnableCodeEditorButton extends Button
    {
        public EnableCodeEditorButton(String id)
        {
            super(id);
            setDefaultFormProcessing(false);
        }

        @Override
        public void onSubmit()
        {
            codeEditorEnabled = true;
            wysiwygEditorEnabled = false;
        }

        @Override
        public boolean isEnabled()
        {
            return codeEditorEnabled == false;
        }
    }

    private class EnableWysiwygEditorButton extends Button
    {
        public EnableWysiwygEditorButton(String id)
        {
            super(id);
            setDefaultFormProcessing(false);
        }

        @Override
        public void onSubmit()
        {
            codeEditorEnabled = false;
            wysiwygEditorEnabled = true;
        }

        @Override
        public boolean isEnabled()
        {
            return wysiwygEditorEnabled == false;
        }
    }

    private class DisableEditorsButton extends Button
    {
        public DisableEditorsButton(String id)
        {
            super(id);
            setDefaultFormProcessing(false);
        }

        @Override
        public void onSubmit()
        {
            codeEditorEnabled = false;
            wysiwygEditorEnabled = false;
        }

        @Override
        public boolean isEnabled()
        {
            return wysiwygEditorEnabled == true || codeEditorEnabled == true;
        }
    }

}