package brix.web.picker.node;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

public class NodePickerPanel extends FormComponentPanel<BrixNode>
{
	private final JcrTreeNode rootNode;
	private final NodeFilter enabledFilter;
	private final NodeFilter visibilityFilter;

	public NodePickerPanel(String id, JcrTreeNode rootNode, NodeFilter visibilityFilter,
			NodeFilter enabledFilter)
	{
		super(id);

		this.rootNode = rootNode;
		this.enabledFilter = enabledFilter;
		this.visibilityFilter = visibilityFilter;
	}

	public NodePickerPanel(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter)
	{
		super(id, model);

		this.rootNode = rootNode;
		this.enabledFilter = enabledFilter;
		this.visibilityFilter = visibilityFilter;
	}

	public JcrTreeNode getRootNode()
	{
		return rootNode;
	}

	public NodeFilter getEnabledFilter()
	{
		return enabledFilter;
	}

	@Override
	protected void onBeforeRender()
	{
		super.onBeforeRender();
		if (!hasBeenRendered())
		{
			init();
		}
	}

	protected static final String MODAL_WINDOW_ID = "modalWindow";

	@Override
	public void updateModel()
	{
		// don't you dare!
	}

	private void init()
	{
		add(newModalWindow(MODAL_WINDOW_ID));
		final Label<?> label = new Label<String>("label", newLabelModel())
		{
			@Override
			public boolean isVisible()
			{
				return NodePickerPanel.this.getModelObject() != null;
			}
		};
		setOutputMarkupId(true);
		add(label);

		add(new AjaxLink<Void>("edit")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				getModalWindow().setModel(NodePickerPanel.this.getModel());
				getModalWindow().setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
				{
					public void onClose(AjaxRequestTarget target)
					{
						target.addComponent(NodePickerPanel.this);
						NodePickerPanel.this.onClose(target);
					}
				});
				getModalWindow().show(target);
			}
		});

		add(new AjaxLink<Void>("clear")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				NodePickerPanel.this.setModelObject(null);
				target.addComponent(NodePickerPanel.this);
			}

			@Override
			public boolean isEnabled()
			{
				return NodePickerPanel.this.getModelObject() != null;
			}
		});
	}

	protected void onClose(AjaxRequestTarget target)
	{

	}

	protected NodePickerModalWindow getModalWindow()
	{
		return (NodePickerModalWindow) get(MODAL_WINDOW_ID);
	}

	protected IModel<String> newLabelModel()
	{
		return new Model<String>()
		{
			@Override
			public String getObject()
			{
				IModel<BrixNode> model = NodePickerPanel.this.getModel();
				BrixNode node = (BrixNode) model.getObject();
				return node != null ? SitePlugin.get().pathForNode(node) : "";
			}
		};
	}

	protected Component<?> newModalWindow(String id)
	{
		return new NodePickerModalWindow(id, getModel(), rootNode, visibilityFilter, enabledFilter);
	}

	@Override
	public boolean checkRequired()
	{
		if (isRequired())
		{
			JcrNode node = (JcrNode) getModelObject();
			if (node == null)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isInputNullable()
	{
		return false;
	}
}
