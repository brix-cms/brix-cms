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

package brix.plugin.site.resource.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.BrixNode.Protocol;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.resource.ResourceRequestTarget;
import brix.web.generic.BrixGenericPanel;

public class ViewPropertiesTab extends BrixGenericPanel<BrixNode>
{

	public ViewPropertiesTab(String id, final IModel<BrixNode> nodeModel)
	{
		super(id, nodeModel);

		add(new Label("mimeType", new Model<String>()
		{
			@Override
			public String getObject()
			{
				BrixFileNode node = (BrixFileNode) nodeModel.getObject();
				return node.getMimeType();
			}
		}));

		add(new Label("size", new Model<String>()
		{
			@Override
			public String getObject()
			{
				BrixFileNode node = (BrixFileNode) nodeModel.getObject();
				return node.getContentLength() + " bytes";
			}
		}));

		add(new Label("requiredProtocol", new Model<String>()
		{
			@Override
			public String getObject()
			{
				Protocol protocol = nodeModel.getObject().getRequiredProtocol();
				return getString(protocol.toString());
			}
		}));

		add(new Link<Void>("download")
		{
			@Override
			public void onClick()
			{
				getRequestCycle().setRequestTarget(new ResourceRequestTarget(nodeModel, true));
			}
		});

		add(new Link<Void>("edit")
		{
			@Override
			public void onClick()
			{
				EditPropertiesPanel panel = new EditPropertiesPanel(ViewPropertiesTab.this.getId(),
						ViewPropertiesTab.this.getModel())
				{
					@Override
					void goBack()
					{
						replaceWith(ViewPropertiesTab.this);
					}
				};
				ViewPropertiesTab.this.replaceWith(panel);
			}
			@Override
			public boolean isVisible()
			{
				return hasEditPermission(ViewPropertiesTab.this.getModel());
			}
		});

		/*
		 * List<Protocol> protocols = Arrays.asList(Protocol.values());
		 * 
		 * final ModelBuffer model = new ModelBuffer(nodeModel); Form<?> form =
		 * new Form<Void>("form");
		 * 
		 * IModel<Protocol> protocolModel =
		 * model.forProperty("requiredProtocol"); form.add(new DropDownChoice<Protocol>("protocol",
		 * protocolModel, protocols).setNullValid(false));
		 * 
		 * form.add(new Button<Void>("save") { @Override public void onSubmit() {
		 * BrixNode node = nodeModel.getObject(); node.checkout();
		 * model.apply(); node.checkin(); node.save(); } });
		 * 
		 * add(form);
		 */
	}

	private static boolean hasEditPermission(IModel<BrixNode> model)
	{
		return SitePlugin.get().canEditNode(model.getObject(), Context.ADMINISTRATION);
	}

}
