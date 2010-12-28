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
