package brix.plugin.menu.editor;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import brix.plugin.site.picker.reference.ReferenceEditorConfiguration;
import brix.plugin.site.picker.reference.ReferenceEditorPanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.reference.Reference;
import brix.web.util.AbstractModel;

public abstract class ReferenceColumnPanel extends BrixGenericPanel<Reference>
{

	public ReferenceColumnPanel(String id, IModel<Reference> model)
	{
		super(id, model);
		
		IModel<String> labelModel = new AbstractModel<String>() {
			@Override
			public String getObject()
			{
				Reference reference = ReferenceColumnPanel.this.getModelObject();
				if (reference != null && !reference.isEmpty())
				{
					return reference.generateUrl();
				}
				else
				{
					return "";
				}
			}
		};
		final Label label = new Label("label", labelModel);			
		add(label);
		label.setOutputMarkupId(true);
		
		ReferenceEditorPanel editor = new ReferenceEditorPanel("editor", model) {
			@Override
			public ReferenceEditorConfiguration getConfiguration()
			{
				return ReferenceColumnPanel.this.getConfiguration();
			}
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
		
		editor.setOutputMarkupPlaceholderTag(true);
		add(editor);				
	}

	protected abstract ReferenceEditorConfiguration getConfiguration();

	protected abstract boolean isEditing();
}
