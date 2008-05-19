package brix.plugin.site.node.folder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.Brix;
import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.jcr.api.JcrNode;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SelectNewNodeTypeAction;

public class CreateNewNodesTab extends Panel<JcrNode>
{

    private Component editor;
    private PluginEntry selectedType;

    public CreateNewNodesTab(String id, IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);
        Form form = new Form("form");
        add(form);
        form.add(new DropDownChoice("type", new PropertyModel(this, "selectedType"),
                new NodeTypesList(), new ChoiceRenderer("name"))
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }

            @Override
            protected void onSelectionChanged(Object newSelection)
            {
                setupEditor();
            }

        });

        setupEditor();

    }

    private void setupEditor()
    {
        if (get("editor") != null)
        {
            remove("editor");
        }

        if (selectedType != null)
        {
            SiteNodePlugin plugin = SitePlugin.get().getNodePluginForType(selectedType.nodeType);
            editor = plugin.newCreateNodePanel("editor", getModel());
        }
        else
        {
            editor = new WebMarkupContainer("editor");
        }

        add(editor);

    }

    private class PluginEntry implements Serializable
    {
        String nodeType;
        String name;
    };

    private class NodeTypesList extends LoadableDetachableModel
    {

        @Override
        protected Object load()
        {
            Brix brix = Locator.getBrix();
            Collection<SiteNodePlugin> plugins = new ArrayList<SiteNodePlugin>(SitePlugin.get().getNodePlugins());
            List<PluginEntry> types = new ArrayList<PluginEntry>(plugins.size());
            for (SiteNodePlugin plugin : plugins)
            {
                Action action = new SelectNewNodeTypeAction(Action.Context.ADMINISTRATION,
                        (JcrNode)CreateNewNodesTab.this.getModelObject(), plugin.getNodeType());

                if (Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
                {
                    PluginEntry entry = new PluginEntry();
                    entry.nodeType = plugin.getNodeType();
                    entry.name = plugin.getName();
                    types.add(entry);
                }
            }
            return types;
        }
    }

}
