package brix.plugin.site.webdav;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.Brix;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.webdav.Rule.Type;
import brix.web.generic.BrixGenericPanel;
import brix.workspace.Workspace;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.datagrid.DataGrid;

public class RulesPanel extends BrixGenericPanel<RulesNode>
{
	private final static class RulesNodeModel extends LoadableDetachableModel<RulesNode>
	{
		private final IModel<Workspace> workspaceModel;

		public RulesNodeModel(IModel<Workspace> workspaceModel)
		{
			this.workspaceModel = workspaceModel;
		}

		@Override
		protected RulesNode load()
		{
			JcrSession session = Brix.get().getCurrentSession(workspaceModel.getObject().getId());
			return SitePlugin.get().getWebDavRules(session);
		}
	};

	private class DataSource implements IDataSource
	{
		public void detach()
		{

		}

		public IModel<?> model(Object object)
		{
			return new Model<Rule>((Rule) object);
		}

		public void query(IQuery query, IQueryResult result)
		{
			List<Rule> rules = RulesPanel.this.getModelObject().getRules(false);
			result.setTotalCount(rules.size());
			result.setItems(rules.iterator());
		}
	};

	private final class SubmitColumn extends SubmitCancelColumn
	{
		private SubmitColumn(String columnId, IModel headerModel)
		{
			super(columnId, headerModel);
		}

		@Override
		protected void onError(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent)
		{
			target.addComponent(feedback);
		}

		@Override
		protected void onSubmitted(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent)
		{
			target.addComponent(feedback);
			Rule rule = (Rule) rowModel.getObject();
			RulesPanel.this.getModelObject().saveRule(rule);
			super.onSubmitted(target, rowModel, rowComponent);
		}
	}

	private final class PriorityColumn extends EditablePropertyColumn
	{
		private PriorityColumn(IModel headerModel, String propertyExpression)
		{
			super(headerModel, propertyExpression);
		}

		@Override
		protected void addValidators(FormComponent component)
		{
			super.addValidators(component);
			component.setType(Integer.class);
			component.setRequired(true);
		}
	}

	private final class TypeColumn extends EditablePropertyColumn
	{

		public TypeColumn(IModel headerModel, String propertyExpression)
		{
			super(headerModel, propertyExpression);
		}

		@Override
		protected EditableCellPanel newCellPanel(String componentId, IModel rowModel, IModel cellModel)
		{
			return new TypePanel(componentId, this, rowModel, cellModel);
		}

		@Override
		protected CharSequence convertToString(Object object)
		{
			return object != null ? getString(((Type) object).toString()) : null;
		}
	}

	private final class TemplateColumn extends EditablePropertyColumn
	{
		public TemplateColumn(String columnId, IModel headerModel)
		{
			super(columnId, headerModel, null);
		}

		@Override
		public Component newCell(WebMarkupContainer parent, String componentId, final IModel rowModel)
		{
			String workspace = RulesPanel.this.getModelObject().getSession().getWorkspace().getName();
			return new NodeColumnPanel(componentId, ((Rule) rowModel.getObject()).getTemplateModel(), workspace)
			{
				@Override
				protected boolean isEditing()
				{
					return getGrid().isItemEdited(rowModel);
				}
			};
		}

		@Override
		public boolean isLightWeight(IModel rowModel)
		{
			return false;
		}
	};

	private Component feedback;
	private DataGrid dataGrid;
	private AjaxLink removeSelected;

	public RulesPanel(String id, IModel<Workspace> workspaceModel)
	{
		super(id, new RulesNodeModel(workspaceModel));

		Form<?> form = new Form<Void>("form");
		add(form);

		form.add(feedback = new FeedbackPanel("feedback").setOutputMarkupId(true));

		List<IGridColumn> columns = new ArrayList<IGridColumn>();

		columns.add(new CheckBoxColumn("checkbox"));
		columns.add(new PropertyColumn(new ResourceModel("name"), "name"));
		columns.add(new PriorityColumn(new ResourceModel("priority"), "priority").setInitialSize(60));
		columns.add(new EditablePropertyColumn(new ResourceModel("pathPrefix"), "pathPrefix"));
		columns.add(new EditablePropertyColumn(new ResourceModel("extensions"), "extensions").setInitialSize(100));
		columns.add(new TypeColumn(new ResourceModel("type"), "type").setInitialSize(90));
		columns.add(new TemplateColumn("template", new ResourceModel("template")).setInitialSize(250));

		columns.add(new SubmitColumn("edit", new ResourceModel("edit")));

		dataGrid = new DataGrid("grid", new DataSource(), columns)
		{
			@Override
			public void onItemSelectionChanged(IModel item, boolean newValue)
			{
				super.onItemSelectionChanged(item, newValue);
				if (AjaxRequestTarget.get() != null)
					AjaxRequestTarget.get().addComponent(removeSelected);
			}
		};
		form.add(dataGrid);
		dataGrid.setContentHeight(30, SizeUnit.EM);
		dataGrid.setClickRowToSelect(true);

		form.add(removeSelected = new AjaxLink<Void>("removeSelected")
		{
			@Override
			public boolean isEnabled()
			{
				return !dataGrid.getSelectedItems().isEmpty();
			}

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				for (IModel model : dataGrid.getSelectedItems())
				{
					Rule rule = (Rule) model.getObject();
					RulesPanel.this.getModelObject().removeRule(rule);
				}
				dataGrid.resetSelectedItems();
				dataGrid.markAllItemsDirty();
				dataGrid.update();
			}
		});
		removeSelected.setOutputMarkupId(true);

		form.add(new TextField<String>("newRuleName", new PropertyModel<String>(this, "newRuleName")).add(
				uniqueNameValidator).setRequired(true));

		form.add(new AjaxSubmitLink("submit")
		{
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				target.addComponent(feedback);
				Rule rule = new Rule(newRuleName);
				rule.setType(Type.PAGE);
				RulesPanel.this.getModelObject().saveRule(rule);
				dataGrid.markAllItemsDirty();
				dataGrid.update();
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form)
			{
				target.addComponent(feedback);
			}
		});
		;
	}

	private IValidator uniqueNameValidator = new IValidator()
	{
		public void validate(IValidatable validatable)
		{
			String name = (String) validatable.getValue();
			if (RulesPanel.this.getModelObject().getRule(name) != null)
			{
				validatable.error(new ValidationError().addMessageKey("ruleExists"));
			}
		}
	};

	private String newRuleName;

}
