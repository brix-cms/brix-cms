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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.collections.MicroMap;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.ResourceNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;
import brix.web.model.ModelBuffer;
import brix.web.util.validators.NodeNameValidator;

public class CreateTextResourcePanel extends BrixGenericPanel<BrixNode>
{
	private String fileName;

	public CreateTextResourcePanel(String id, IModel<BrixNode> container, final SimpleCallback back)
	{
		super(id, container);

		add(new FeedbackPanel("feedback"));

		Form<?> form = new Form<Void>("form");
		add(form);

		form.add(new TextField<String>("fileName", new PropertyModel<String>(this, "fileName"))
				.setRequired(true).add(NodeNameValidator.getInstance()).setLabel(
						new ResourceModel("fileName")));

		final ModelBuffer model = new ModelBuffer();

		form.add(new TextResourceEditor("editor", model));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				if (getContainer().hasNode(fileName))
				{
					error(getString("fileExists", Model.ofMap(new MicroMap<String, String>(
							"fileName", fileName))));
					return;
				}

				// create initial node skeleton
				BrixNode node = (BrixNode)getContainer().addNode(fileName, "nt:file");
				BrixFileNode file = BrixFileNode.initialize(node, "text"); // temp-mime

				// save the node so brix assigns the correct jcr type to it
				getContainer().save();

				// populate node
				ResourceNode resource = (ResourceNode)getContainer().getSession().getItem(
						node.getPath());
				model.setObject(new BrixNodeModel(resource));
				model.apply();

				getContainer().save();

				// done
				getSession().info(getString("saved"));
				SitePlugin.get().selectNode(this, resource, true);
			}
		});

		form.add(new Link<Void>("cancel")
		{
			@Override
			public void onClick()
			{
				getSession().info(getString("cancelled"));
				back.execute();
			}
		});


	}


	protected BrixNode getContainer()
	{
		return getModelObject();
	}
}
