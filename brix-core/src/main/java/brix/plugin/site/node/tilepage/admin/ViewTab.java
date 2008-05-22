package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.plugin.site.SitePlugin;

public class ViewTab extends Panel<JcrNode>
{

    public ViewTab(String id, IModel<JcrNode> model)
    {
        super(id, model);

        add(new Label("title", new PropertyModel(model, "title")));
        add(new Label("template", new PropertyModel(model, "templatePath")));
        add(new Label("requiresSSL", new PropertyModel(model, "requiresSSL")));
        add(new Label("content", new PropertyModel(model, "dataAsString")));
    }

}
