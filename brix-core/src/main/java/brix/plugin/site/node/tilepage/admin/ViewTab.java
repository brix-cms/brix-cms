package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.PreviewNodeIFrame;

public class ViewTab extends Panel<BrixNode> {

	public ViewTab(String id, IModel<BrixNode> model) {
		super(id, model);

		add(new Label("title", new PropertyModel(model, "title")));
		add(new Label("template", new PropertyModel(model, "templatePath")));
		add(new Label("requiresSSL", new PropertyModel(model, "requiresSSL")));

		// add(new Label("content", new PropertyModel(model, "dataAsString")));

		add(new PreviewNodeIFrame("preview", model));
	}
}
