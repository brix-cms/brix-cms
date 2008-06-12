package brix.plugin.site.page.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.auth.Action;
import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.PreviewNodeIFrame;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.auth.SiteNodeAction.Type;

public class ViewTab extends Panel<BrixNode> {

	public ViewTab(String id, IModel<BrixNode> model) {
		super(id, model);

		add(new Label<String>("title", new PropertyModel<String>(model, "title")));
		add(new Label<String>("template", new PropertyModel<String>(model, "templatePath")));
		add(new Label<Boolean>("requiresSSL", new PropertyModel<Boolean>(model, "requiresSSL")));

		// add(new Label("content", new PropertyModel(model, "dataAsString")));

		add(new PreviewNodeIFrame("preview", model));
		
		add(new Link<Void>("edit") {
			@Override
			public void onClick()
			{				
				EditTab edit = new EditTab(ViewTab.this.getId(), ViewTab.this.getModel())
				{
					@Override
					void goBack()
					{
						replaceWith(ViewTab.this);
					}
				};
				ViewTab.this.replaceWith(edit);
			}
			@Override
			public boolean isVisible()
			{
				BrixNode node = ViewTab.this.getModelObject();
				Action action = new SiteNodeAction(Context.ADMINISTRATION, Type.NODE_EDIT, node);
				return node.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
			}
		});
	}
}
