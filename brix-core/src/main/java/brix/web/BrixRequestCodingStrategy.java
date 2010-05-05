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

/**
 * 
 */
package brix.web;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.apache.wicket.util.string.UrlUtils;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.PageRenderingPage;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;

public class BrixRequestCodingStrategy extends WebRequestCodingStrategy
{
    private final Brix brix;
    private final BrixUrlCodingStrategy urlCodingStrategy;

    public BrixRequestCodingStrategy(Brix brix, BrixUrlCodingStrategy urlCodingStrategy)
    {
        this.brix = brix;
        this.urlCodingStrategy = urlCodingStrategy;
    }

    @Override
    public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath(String path)
    {

        IRequestTargetUrlCodingStrategy target = super.urlCodingStrategyForPath(path);
        if (target == null)
        {
            target = this.urlCodingStrategy;
        }
        return target;
    }

    @Override
    public String rewriteStaticRelativeUrl(String url)
    {
        boolean insideBrixPage = false;
        IRequestTarget target = RequestCycle.get().getRequestTarget();
        if (target instanceof IPageRequestTarget)
        {
            IPageRequestTarget pageTarget = (IPageRequestTarget)target;
            Page page = pageTarget.getPage();
            if (page instanceof BrixNodeWebPage)
            {
                insideBrixPage = true;
            }
        }

        if (insideBrixPage && UrlUtils.isRelative(url))
        {
            final String prefix = RequestCycle.get().getRequest()
                    .getRelativePathPrefixToContextRoot();

            return this.brix.getConfig().getMapper().rewriteStaticRelativeUrl(url, prefix);
        }
        else
        {
            return super.rewriteStaticRelativeUrl(url);
        }
    }

    @Override
    protected CharSequence encode(RequestCycle rc, IBookmarkablePageRequestTarget target)
    {
        boolean selfReferentialBookmarkableUrl = false;

        if (PageRenderingPage.class.equals(target.getPageClass()))
        {
            // target of the url is brix's internal page rendering page, check if we are in a brix
            // page right now...
            IRequestTarget crt = rc.getRequestTarget();
            if (crt instanceof IPageRequestTarget)
            {
                IPageRequestTarget cprt = (IPageRequestTarget)crt;
                if (cprt.getPage() instanceof PageRenderingPage)
                {
                    // we are currently on the page rendering page
                    selfReferentialBookmarkableUrl = true;
                }
            }
        }

        if (!selfReferentialBookmarkableUrl)
        {
            return super.encode(rc, target);
        }

        // we are on a self referential bookmarkable url, rewrite it

        PageRenderingPage currentPage = (PageRenderingPage)((IPageRequestTarget)rc
                .getRequestTarget()).getPage();

        BrixPageParameters params = new BrixPageParameters(target.getPageParameters());

        return params.urlFor(currentPage);
    }
}