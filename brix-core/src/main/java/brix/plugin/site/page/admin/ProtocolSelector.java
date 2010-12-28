/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
