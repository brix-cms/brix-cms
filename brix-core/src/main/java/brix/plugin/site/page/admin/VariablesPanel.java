package brix.plugin.site.page.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.AbstractContainer;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.column.editable.SubmitCancelColumn;
import com.inmethod.grid.datagrid.DataGrid;

public class VariablesPanel extends Panel<BrixNode>
{
	private AjaxLink<?> delete;
	
	public VariablesPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);
		
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		columns.add(new CheckBoxColumn("checkbox"));
		columns.add(new PropertyColumn(new Model("key"), "key"));
		columns.add(new EditablePropertyColumn(new Model("value"), "value") {
			@Override
			protected void addValidators(FormComponent component)
			{
				component.setRequired(true);
			}
		});
		columns.add(new SubmitCancelColumn("submitcancel", new Model("edit")) {
			@Override
			protected void onError(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent)
			{
				target.addChildren(VariablesPanel.this, FeedbackPanel.class);
			}
			@Override
			protected void onSubmitted(AjaxRequestTarget target, IModel rowModel, WebMarkupContainer rowComponent)
			{
				target.addChildren(VariablesPanel.this, FeedbackPanel.class);
				super.onSubmitted(target, rowModel, rowComponent);
			}
		});
							
		final DataGrid grid = new DataGrid("grid", new DataSource(), columns) {
			@Override
			public void onItemSelectionChanged(IModel item, boolean newValue)
			{
				AjaxRequestTarget target = AjaxRequestTarget.get();
				if (target != null)
				{
					target.addComponent(delete);
				}
				super.onItemSelectionChanged(item, newValue);
			}
		};
		add(grid);
		grid.setSelectToEdit(false);
		grid.setClickRowToSelect(true);

		add(delete = new AjaxLink<Void>("deleteSelected") {
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
				for (IModel<?> m : grid.getSelectedItems())
				{
					Entry e = (Entry) m.getObject();
					node.setVariableValue(e.getKey(), null);
				}
				node.save();
				grid.markAllItemsDirty();
				grid.update();
				grid.resetSelectedItems();
				target.addComponent(this);
			}
			@Override
			public boolean isEnabled()
			{
				return grid.getSelectedItems().isEmpty() == false;
			}
		});
		delete.setOutputMarkupId(true);
		
		add(new InsertForm("form")
		{
			@Override
			protected void onItemAdded()
			{
				grid.markAllItemsDirty();
				grid.update();
			}
		});

		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
	}

	private class DataSource implements IDataSource
	{

		public IModel model(Object object)
		{
			return new Model((Serializable) object);
		}

		public void query(IQuery query, IQueryResult result)
		{
			AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
			List<Entry> res = new ArrayList<Entry>();
			for (String s : node.getSavedVariableKeys())
			{
				res.add(new Entry(s));
			}
			Collections.sort(res, new Comparator<Entry>() {

				public int compare(Entry o1, Entry o2)
				{
					return o1.getKey().compareTo(o2.getKey());
				}				
			});
			result.setItems(res.iterator());
			result.setTotalCount(res.size());
		}

		public void detach()
		{		
		}		
	};
	
	private class Entry implements Serializable
	{
		private final String key;

		public Entry(String key)
		{
			this.key = key;
		}

		public String getKey()
		{
			return key;
		}

		public String getValue()
		{
			AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
			return node.getVariableValue(key, false);
		}

		public void setValue(String value)
		{
			AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
			node.setVariableValue(key, value);
			node.save();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj instanceof Entry == false)
				return false;
			Entry that = (Entry) obj;
			return Objects.equal(key, that.key);			
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hashCode(key);
		}
	};

	private abstract class InsertForm extends Form<Void>
	{

		public InsertForm(String id)
		{
			super(id);

			IModel<List<? extends String>> choicesModel = new LoadableDetachableModel<List<? extends String>>()
			{
				@Override
				protected List<? extends String> load()
				{
					List<String> result = new ArrayList<String>();
					AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
					result.addAll(node.getVariableKeys());
					return result;
				}
			};

			final DropDownChoice<String> ddc;
			add(ddc = new DropDownChoice<String>("ddc", new PropertyModel<String>(this, "selected"), choicesModel));
			ddc.setNullValid(true);

			final TextField<String> tf;
			add(tf = new TextField<String>("custom", new PropertyModel<String>(this, "custom"))
			{
				@Override
				public boolean isVisible()
				{
					return selected == null;
				}

				@Override
				public boolean isRequired()
				{
					return selected == null;
				}
			});
			tf.setOutputMarkupPlaceholderTag(true);

			ddc.add(new AjaxFormComponentUpdatingBehavior("onchange")
			{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
				{
					target.addComponent(tf);
				}

			});

			add(new TextField<String>("value", new PropertyModel<String>(this, "value")).setRequired(true));

			add(new AjaxButton<Void>("submit")
			{
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
					String key = selected != null ? selected : custom;
					AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
					node.setVariableValue(key, value);
					node.save();
					onItemAdded();
					selected = null;
					custom = null;
					value = null;
					form.modelChanged();
					target.addComponent(form);
					target.addChildren(findParent(VariablesPanel.class), FeedbackPanel.class);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form)
				{
					target.addChildren(findParent(VariablesPanel.class), FeedbackPanel.class);
				}
			});

			add(new AbstractFormValidator()
			{
				public FormComponent<?>[] getDependentFormComponents()
				{
					if (selected != null)
						return new FormComponent<?>[] { ddc };
					else
						return new FormComponent<?>[] { ddc, tf };
				}

				private void report(String messageKey, String key)
				{
					Map<String, Object> attr = new HashMap<String, Object>();
					attr.put("key", key);
					error(ddc, messageKey, attr);
				}

				public void validate(Form<?> form)
				{
					String key = selected != null ? selected : tf.getConvertedInput();
					AbstractContainer node = (AbstractContainer) VariablesPanel.this.getModelObject();
					if (key.contains("/") || key.contains(":"))
					{
						report("keyValidator.invalidKey", key);
					}
					else if (node.getVariableValue(key, false) != null)
					{
						report("keyValidator.duplicateKey", key);
					}
				}
			});
		}

		private String selected;
		private String custom;
		private String value;

		abstract protected void onItemAdded();
	};

}
