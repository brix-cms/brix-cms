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

package brix.markup.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupHelper;
import brix.markup.MarkupSource;
import brix.markup.MarkupSourceProvider;
import brix.web.generic.BrixGenericPanel;

/**
 * Panel that uses {@link MarkupSource} for it's markup and child components.
 * 
 * @author Matej Knopp
 */
public abstract class BrixMarkupNodePanel extends BrixGenericPanel<BrixNode> implements IMarkupResourceStreamProvider,
		IMarkupCacheKeyProvider, MarkupSourceProvider
{
	public BrixMarkupNodePanel(String id, IModel<BrixNode> model)
	{
		super(id, model);
	}

	public BrixMarkupNodePanel(String id)
	{
		super(id);
	}

	public String getCacheKey(MarkupContainer container, Class<?> containerClass)
	{
		return null;
	}

	@Override
	protected void onBeforeRender()
	{
		this.markupHelper = new MarkupHelper(this);
		super.onBeforeRender();
	}

	public MarkupHelper getMarkupHelper() {
		return markupHelper;
	}

	private MarkupHelper markupHelper;

	@Override
	protected void onDetach()
	{
		super.onDetach();
		markupHelper = null;
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass)
	{
		return new StringResourceStream(markupHelper.getMarkup(), "text/html");
	}
}
