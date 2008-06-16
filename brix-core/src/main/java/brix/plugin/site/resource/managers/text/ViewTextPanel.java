package brix.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.auth.SiteNodeAction;

public class ViewTextPanel extends Panel<BrixNode>
{

	public ViewTextPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);

		IModel<String> labelModel = new Model<String>()
		{
			@Override
			public String getObject()
			{
				BrixFileNode node = (BrixFileNode) getModel().getObject();
				return node.getDataAsString();
			}
		};

		add(new Label<String>("label", labelModel));

		add(new Link<Void>("edit")
		{
			@Override
			public void onClick()
			{
				EditTextPanel panel = new EditTextPanel(ViewTextPanel.this.getId(), ViewTextPanel.this.getModel())
				{
					@Override
					protected void goBack()
					{
						replaceWith(ViewTextPanel.this);
					}
				};
				ViewTextPanel.this.replaceWith(panel);				
			}
			
			@Override
			public boolean isVisible()
			{
				return hasEditPermission(ViewTextPanel.this.getModel());
			}
		});
	}

	private static boolean hasEditPermission(IModel<BrixNode> model)
	{
		Action action = new SiteNodeAction(Action.Context.ADMINISTRATION, SiteNodeAction.Type.NODE_EDIT, model
				.getObject());
		return model.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
	}
}
