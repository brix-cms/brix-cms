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

package brix.plugin.site.folder;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.PageRequestTarget;

import brix.Brix;
import brix.Path;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.web.BrixRequestCycleProcessor;
import brix.web.nodepage.ForbiddenPage;
import brix.web.reference.Reference;
import org.apache.wicket.model.ResourceModel;

public class FolderNodePlugin implements SiteNodePlugin
{

	public static final String TYPE = Brix.NS_PREFIX + "folder";

	public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters)
	{
		BrixNode node = nodeModel.getObject();

		String path = requestParameters.getPath();
		if (!path.startsWith("/"))
			path = "/" + path;

		BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) RequestCycle.get().getProcessor();
		Path uriPath = processor.getUriPathForNode(node);

		// check if the exact request path matches the node path
		if (new Path(path).equals(uriPath) == false)
		{
			return null;
		}

		FolderNode folder = (FolderNode) node;
		Reference redirect = folder.getRedirectReference();

		if (redirect != null && !redirect.isEmpty())
		{
			IRequestTarget target = redirect.getRequestTarget();
			final CharSequence url = RequestCycle.get().urlFor(target);
			return new IRequestTarget()
			{
				public void detach(RequestCycle requestCycle)
				{
				
				}

				public void respond(RequestCycle requestCycle)
				{
					requestCycle.getResponse().redirect(url.toString());
				}
			};
		}
		else
		{
			return new PageRequestTarget(new ForbiddenPage(path));
		}
	}

	public Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack)
	{
		return new CreateFolderPanel(id, parentNode, goBack);
	}

	public NodeConverter getConverterForNode(BrixNode node)
	{
		return null;
	}

	public String getNodeType()
	{
		return TYPE;
	}

	public String getName()
	{
		return "Folder";
	}

	public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode)
	{
		return new ResourceModel("createFolder", "Create New Folder");
	}

	public FolderNodePlugin(SitePlugin sp)
	{
		sp.registerManageNodeTabFactory(new ManageFolderNodeTabFactory());
	}
}
