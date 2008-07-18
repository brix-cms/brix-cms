package brix.plugin.site.webdav;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import brix.plugin.site.webdav.Rule.Type;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;

public class TypePanel extends EditableCellPanel
{

	public TypePanel(String id, AbstractColumn column, IModel rowModel, IModel itemModel)
	{
		super(id, column, rowModel);
	
		List<Type> types = Arrays.asList(Type.values());
		
		DropDownChoice<Type> choice;
		add(choice = new DropDownChoice<Type>("dropDown", itemModel, types, typeRenderer));
		choice.setNullValid(false);
		choice.setRequired(true);
	}	
	

	private IChoiceRenderer<Type> typeRenderer = new IChoiceRenderer<Type>()
	{
		public Object getDisplayValue(Type object)
		{
			return getString(object.toString());
		}

		public String getIdValue(Type object, int index)
		{
			return "" + index;
		}
	};

	@Override
	protected FormComponent getEditComponent()
	{
		return (FormComponent) get("dropDown");
	}

}
