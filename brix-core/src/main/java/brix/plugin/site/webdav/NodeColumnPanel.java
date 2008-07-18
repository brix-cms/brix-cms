package brix.plugin.site.webdav;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.TemplateSiteNodePlugin;
import brix.plugin.site.picker.node.SiteNodePickerPanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.picker.node.NodeTypeFilter;
import brix.web.tree.NodeFilter;
import brix.web.util.AbstractModel;

public abstract class NodeColumnPanel extends BrixGenericPanel<BrixNode>
{

	public NodeColumnPanel(String id, IModel<BrixNode> model, String workspace)
	{
		super(id, model);
		
		IModel<String> labelModel = new AbstractModel<String>() {
			@Override
			public String getObject()
			{
				BrixNode node = NodeColumnPanel.this.getModelObject();
				return node != null ? SitePlugin.get().pathForNode(node) : "";
			}
		};
		final Label label = new Label("label", labelModel);			
		add(label);
		label.setOutputMarkupId(true);
		
		NodeFilter filter = new NodeTypeFilter(TemplateSiteNodePlugin.TYPE);
		
		SiteNodePickerPanel picker = new SiteNodePickerPanel("picker", model, workspace, false, filter) {
			@Override
			public boolean isVisible()
			{
				return isEditing();
			}			
			@Override
			protected IModel<String> newLabelModel()
			{
				return new AbstractModel<String>() {
					@Override
					public String getObject()
					{
						return "";
					}
				};
			}
			
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{				
				super.onUpdate(target);
				target.addComponent(label);
			}
		};
		
		picker.setOutputMarkupPlaceholderTag(true);
		add(picker);				
	}

	protected abstract boolean isEditing();
}
