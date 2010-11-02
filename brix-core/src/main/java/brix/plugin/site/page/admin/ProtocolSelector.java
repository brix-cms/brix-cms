package brix.plugin.site.page.admin;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class ProtocolSelector extends DropDownChoice<Boolean> {
	public ProtocolSelector(String id, IModel<Boolean> model) {
		super(id);
		
		setModel(model);
		
		setChoices(Arrays.asList(new Boolean[] { new Boolean(true),
				new Boolean(false) }));
		
		setChoiceRenderer(new IChoiceRenderer<Boolean>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Boolean object) {
				if (object == null) {
					return "";
				}
				return getLocalizer().getString(
						(object ? "protocol.ssl" : "protocol.nossl"),
						ProtocolSelector.this);
			}

			public String getIdValue(Boolean object, int index) {
				return object == null ? "" : object.toString();
			}
		});
	}

	@Override
	public boolean isNullValid() {
		return true;
	}

	@Override
	protected String getNullValidKey() {
		return "protocol.existing";
	}
}
