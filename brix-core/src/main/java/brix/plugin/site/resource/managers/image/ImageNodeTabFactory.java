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

package brix.plugin.site.resource.managers.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.ResourceNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

public class ImageNodeTabFactory implements ManageNodeTabFactory
{
	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel)
	{
		List<IBrixTab> result = new ArrayList<IBrixTab>();

		BrixNode node = nodeModel.getObject();
		if (node instanceof ResourceNode && hasViewPermission(nodeModel)) 
		{
			String mime = ((BrixFileNode) node).getMimeType();
			if (canHandleMimeType(mime))
			{
				result.add(getViewTab(nodeModel));
			}
		}

		return result;
	}

	private static boolean canHandleMimeType(String mimeType)
	{
		List<String> types = Arrays.asList(new String[] { "image/jpeg", "image/gif", "image/png" });
		return mimeType != null && types.contains(mimeType.toLowerCase());
	}

	private static IBrixTab getViewTab(final IModel<BrixNode> nodeModel)
	{
		return new CachingAbstractTab(new Model<String>("View"), 100)
		{
			@Override
			public Panel newPanel(String panelId)
			{
				return new ViewImagePanel(panelId, nodeModel);
			}
		};
	}

	private static boolean hasViewPermission(IModel<BrixNode> model)
	{
		return SitePlugin.get().canViewNode(model.getObject(), Context.ADMINISTRATION);
	}

}
