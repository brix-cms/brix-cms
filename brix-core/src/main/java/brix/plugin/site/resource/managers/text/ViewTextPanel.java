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

package brix.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;

public class ViewTextPanel extends BrixGenericPanel<BrixNode>
{

	public ViewTextPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);

		IModel<String> labelModel = new Model<String>()
		{
			@Override
			public String getObject()
			{
				BrixFileNode node = (BrixFileNode) getModel().getObject();
				return node.getDataAsString();
			}
		};

		add(new Label("label", labelModel));

		add(new Link<Void>("edit")
		{
			@Override
			public void onClick()
			{
				EditTextPanel panel = new EditTextPanel(ViewTextPanel.this.getId(), ViewTextPanel.this.getModel())
				{
					@Override
					protected void goBack()
					{
						replaceWith(ViewTextPanel.this);
					}
				};
				ViewTextPanel.this.replaceWith(panel);				
			}
			
			@Override
			public boolean isVisible()
			{
				return hasEditPermission(ViewTextPanel.this.getModel());
			}
		});
	}

	private static boolean hasEditPermission(IModel<BrixNode> model)
	{
		return SitePlugin.get().canEditNode(model.getObject(), Context.ADMINISTRATION);
	}
}
