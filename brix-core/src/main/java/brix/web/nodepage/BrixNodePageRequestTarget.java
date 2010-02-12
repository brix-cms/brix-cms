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

package brix.web.nodepage;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.component.IPageRequestTarget;

public class BrixNodePageRequestTarget
		implements
			IRequestTarget,
			IPageRequestTarget,
			PageParametersRequestTarget
{
	private final IModel<?> node;
	private BrixNodeWebPage page;
	private final PageFactory pageFactory;


	public BrixNodePageRequestTarget(IModel<?> node, BrixNodeWebPage page)
	{
		this.node = node;
		this.page = page;
		this.pageFactory = null;
	}

	public BrixNodePageRequestTarget(IModel<?> node, PageFactory pageFactory)
	{
		this.node = node;
		this.page = null;
		this.pageFactory = pageFactory;
	}


	public Page getPage()
	{
		if (page == null && pageFactory != null)
		{
			page = pageFactory.newPage();
		}
		return page;
	}


	public void detach(RequestCycle requestCycle)
	{
		if (getPage() != null)
		{
			getPage().detach();
		}
		node.detach();

	}

	public final void respond(RequestCycle requestCycle)
	{
		if (page == null)
		{
			page = pageFactory.newPage();
			if (page.initialRedirect())
			{
				// if the page is newly created and initial redirect is set, we
				// need to redirect to a hybrid URL
				page.setStatelessHint(false);
				Session.get().bind();
				Session.get().touch(page);
				requestCycle.setRequestTarget(new BrixNodeRequestTarget(page));
				return;
			}
		}

		respondWithInitialRedirectHandled(requestCycle);
	}

	public BrixPageParameters getPageParameters()
	{
		if (pageFactory != null)
		{
			return pageFactory.getPageParameters();
		}
		else
		{
			return page.getBrixPageParameters();
		}
	}

	protected void respondWithInitialRedirectHandled(RequestCycle requestCycle)
	{
		// check if the listener invocation or something else hasn't changed the
		// request target
		if (RequestCycle.get().getRequestTarget() == this)
		{
			getPage().renderPage();
		}
	}


	public static interface PageFactory
	{
		public BrixNodeWebPage newPage();

		public BrixPageParameters getPageParameters();
	};


}
