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

package brix.plugin.site.admin;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.web.BrixRequestCycleProcessor;
import brix.web.generic.BrixGenericWebMarkupContainer;
import brix.web.nodepage.BrixNodeRequestTarget;
import brix.web.nodepage.BrixPageParameters;

public class PreviewNodeIFrame extends BrixGenericWebMarkupContainer<BrixNode>
{

	private static final String PREVIEW_PARAM = Brix.NS_PREFIX + "preview";

	public static boolean isPreview()
	{
		BrixPageParameters params = BrixPageParameters.getCurrent();
		if (params != null)
		{
			if (params.getQueryParam(PREVIEW_PARAM).toBoolean(false))
			{
				return true;
			}
		}
		return false;
	}

	public PreviewNodeIFrame(String id, IModel<BrixNode> model)
	{
		super(id, model);
		setOutputMarkupId(true);
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		tag.put("src", getUrl());
	}

	private CharSequence getUrl()
	{
		BrixPageParameters parameters = new BrixPageParameters();
		IModel<BrixNode> nodeModel = getModel();
		String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
		parameters.setQueryParam(BrixRequestCycleProcessor.WORKSPACE_PARAM, workspace);
		parameters.setQueryParam(PREVIEW_PARAM, "true");
		StringBuilder url = new StringBuilder(getRequestCycle()
				.urlFor(new BrixNodeRequestTarget(nodeModel, parameters)));
		return url;
	}
}
