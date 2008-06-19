package brix.plugin.prototype;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.Brix;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.plugin.prototype.auth.CreatePrototypeAction;
import brix.plugin.prototype.auth.DeletePrototypeAction;
import brix.plugin.prototype.auth.RestorePrototypeAction;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class ManagePrototypesPanel extends Panel<Workspace>
{

	private Brix getBrix()
	{
		// TODO: We don't really have a node here
		return Brix.get();
	}
	
	public ManagePrototypesPanel(String id, final IModel<Workspace> model)
	{
		super(id, model);
		setOutputMarkupId(true);

		IModel<List<Workspace>> prototypesModel = new LoadableDetachableModel<List<Workspace>>()
		{
			@Override
			protected List<Workspace> load()
			{
				List<Workspace> list = PrototypePlugin.get().getPrototypes();
				return getBrix().filterVisibleWorkspaces(list, Context.ADMINISTRATION);
			}
		};

		Form<Void> modalWindowForm = new Form<Void>("modalWindowForm");
		add(modalWindowForm);

		final ModalWindow<?> modalWindow = new ModalWindow<Void>("modalWindow");
		modalWindow.setInitialWidth(64);
		modalWindow.setWidthUnit("em");
		modalWindow.setUseInitialHeight(false);
		modalWindow.setResizable(false);
		modalWindow.setTitle(new ResourceModel("selectItems"));
		modalWindowForm.add(modalWindow);

		add(new ListView<Workspace>("prototypes", prototypesModel)
		{
			@Override
			protected IModel<Workspace> getListItemModel(IModel<List<Workspace>> listViewModel, int index)
			{
				return new WorkspaceModel(listViewModel.getObject().get(index));
			}

			@Override
			protected void populateItem(final ListItem<Workspace> item)
			{
				PrototypePlugin plugin = PrototypePlugin.get();
				final String name = plugin.getUserVisibleName(item.getModelObject(), false);

				item.add(new Label<String>("label", name));
				item.add(new Link<Void>("browse")
				{
					@Override
					public void onClick()
					{
						model.setObject(item.getModelObject());
					}
				});

				item.add(new AjaxLink<Void>("restoreItems")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						String prototypeId = item.getModelObject().getId();
						String targetId = ManagePrototypesPanel.this.getModelObject().getId();
						Panel<Void> panel = new RestoreItemsPanel(modalWindow.getContentId(), prototypeId, targetId);
						modalWindow.setContent(panel);
						modalWindow.show(target);
					}

					@Override
					public boolean isVisible()
					{
						Workspace target = ManagePrototypesPanel.this.getModelObject();
						Action action = new RestorePrototypeAction(Context.ADMINISTRATION, item.getModelObject(), target);
						return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
					}
				});

				item.add(new Link<Void>("delete")
				{
					@Override
					public void onClick()
					{
						Workspace prototype = item.getModelObject();
						prototype.delete();
					}

					@Override
					public boolean isVisible()
					{
						Action action = new DeletePrototypeAction(Context.ADMINISTRATION, item.getModelObject());
						return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
					}
				});
			}
		});

		Form<Object> form = new Form<Object>("form")
		{
			@Override
			public boolean isVisible()
			{
				Workspace current = ManagePrototypesPanel.this.getModelObject();
				Action action = new CreatePrototypeAction(Context.ADMINISTRATION, current);
				return getBrix().getAuthorizationStrategy().isActionAuthorized(action);
			}
		};

		TextField<String> prototypeName = new TextField<String>("prototypeName", new PropertyModel<String>(this,
				"prototypeName"));
		form.add(prototypeName);

		prototypeName.setRequired(true);
		prototypeName.add(new UniquePrototypeNameValidator());

		final FeedbackPanel feedback;

		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);

		form.add(new AjaxButton<Void>("submit")
		{
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				String workspaceId = ManagePrototypesPanel.this.getModelObject().getId();
				CreatePrototypePanel panel = new CreatePrototypePanel(modalWindow.getContentId(), workspaceId,
						ManagePrototypesPanel.this.prototypeName);
				modalWindow.setContent(panel);
				modalWindow.setWindowClosedCallback(new WindowClosedCallback()
				{
					public void onClose(AjaxRequestTarget target)
					{
						target.addComponent(ManagePrototypesPanel.this);
					}
				});
				modalWindow.show(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form)
			{
				target.addComponent(feedback);
			}
		});

		add(form);

	}

	private String prototypeName;

	private class UniquePrototypeNameValidator implements IValidator
	{
		public void validate(IValidatable validatable)
		{
			String name = (String) validatable.getValue();
			if (PrototypePlugin.get().prototypeExists(name))
			{
				validatable.error(new ValidationError().addMessageKey("UniquePrototypeNameValidator"));
			}
		}
	}

}
