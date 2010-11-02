package brix.plugin.site.page.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class ProtocolLabel extends Label {
	private static final long serialVersionUID = 1L;

	public ProtocolLabel(String id, final IModel<Boolean> protocolModel) {
		super(id);
		setDefaultModel(new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				Boolean proto = protocolModel.getObject();
				if (Boolean.TRUE.equals(proto)) {
					return getString("protocol.ssl");
				} else if (Boolean.FALSE.equals(proto)) {
					return getString("protocol.nossl");
				} else {
					return getString("protocol.existing");
				}
			}

			@Override
			public void detach() {
				protocolModel.detach();
			}

		});
	}

}
