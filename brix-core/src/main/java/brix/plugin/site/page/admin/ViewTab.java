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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.PreviewNodeIFrame;
import brix.web.generic.BrixGenericPanel;
import brix.web.tab.BrixTabbedPanel;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

public class ViewTab extends BrixGenericPanel<BrixNode> {

	public ViewTab(String id, IModel<BrixNode> model) {
		super(id, model);

		add(new Label("title", new PropertyModel<String>(model, "title")));
		add(new Label("template", new PropertyModel<String>(model, "templatePath")));
		add(new Label("requiresSSL", new PropertyModel<Boolean>(model, "requiresSSL")));

		// add(new Label("content", new PropertyModel(model, "dataAsString")));

		List<IBrixTab> tabs = new ArrayList<IBrixTab>();
		tabs.add(new CachingAbstractTab(new ResourceModel("textPreview")) {
			@Override
			public Panel newPanel(String panelId)
			{
				return new TextPreviewPanel(panelId);
			}
		});
		
		tabs.add(new CachingAbstractTab(new ResourceModel("pagePreview")) {
			@Override
			public Panel newPanel(String panelId)
			{
				return new IframePreviewPanel(panelId);
			}
		});
		
		add(new BrixTabbedPanel("previewTabbedPanel", tabs));
		
		add(new Link<Void>("edit") {
			@Override
			public void onClick()
			{				
				EditTab edit = new EditTab(ViewTab.this.getId(), ViewTab.this.getModel())
				{
					@Override
					void goBack()
					{
						replaceWith(ViewTab.this);
					}
				};
				ViewTab.this.replaceWith(edit);
			}
			@Override
			public boolean isVisible()
			{
				BrixNode node = ViewTab.this.getModelObject();
				return SitePlugin.get().canEditNode(node, Context.ADMINISTRATION);
			}
		});
	}
	
	private class TextPreviewPanel extends Panel
	{

		public TextPreviewPanel(String id)
		{
			super(id);

			IModel<String> labelModel = new Model<String>()
			{
				@Override
				public String getObject()
				{
					BrixFileNode node = (BrixFileNode) ViewTab.this.getModel().getObject();
					return node.getDataAsString();
				}
			};

			add(new Label("label", labelModel));
		}
	}
	
	private class IframePreviewPanel extends Panel
	{
		public IframePreviewPanel(String id)
		{
			super(id);
			add(new PreviewNodeIFrame("preview", ViewTab.this.getModel()));
		}		
	};
	
	
}
