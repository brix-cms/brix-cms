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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;

public abstract class EditTextPanel extends BrixGenericPanel<BrixNode>
{

	public EditTextPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);

		Form<Void> form = new Form<Void>("form");
		add(form);

		content = getFileNode().getDataAsString();

		form.add(new TextArea<String>("text", new PropertyModel<String>(this, "content")));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				BrixFileNode node = getFileNode();
				node.setData(content);
				node.save();
				getSession().info(getString("textSaved"));
				goBack();
			}
		});

		form.add(new Link<Void>("cancel")
		{
			@Override
			public void onClick()
			{
				getSession().info(getString("editingCanceled"));
				goBack();
			}
		});

	}

	private String content;

	private BrixFileNode getFileNode()
	{
		return (BrixFileNode) getModelObject();
	}

	protected abstract void goBack();

}
