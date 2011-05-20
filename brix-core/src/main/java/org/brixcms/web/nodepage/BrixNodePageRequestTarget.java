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

package org.brixcms.web.nodepage;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;

public class BrixNodePageRequestTarget
        implements
        IRequestTarget,
        IPageRequestTarget,
        PageParametersRequestTarget {
// ------------------------------ FIELDS ------------------------------

    private final IModel<BrixNode> node;
    private BrixNodeWebPage page;
    private final PageFactory pageFactory;

// --------------------------- CONSTRUCTORS ---------------------------

    public BrixNodePageRequestTarget(IModel<BrixNode> node, BrixNodeWebPage page) {
        this.node = node;
        this.page = page;
        this.pageFactory = null;
    }

    public BrixNodePageRequestTarget(IModel<BrixNode> node, PageFactory pageFactory) {
        this.node = node;
        this.page = null;
        this.pageFactory = pageFactory;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Page getPage() {
        if (page == null && pageFactory != null) {
            page = pageFactory.newPage();
        }
        return page;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IRequestTarget ---------------------


    public final void respond(RequestCycle requestCycle) {
        if (page == null) {
            page = pageFactory.newPage();
            if (page.initialRedirect()) {
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

    public void detach(RequestCycle requestCycle) {
        if (getPage() != null) {
            getPage().detach();
        }
        node.detach();
    }

// --------------------- Interface PageParametersRequestTarget ---------------------


    public BrixPageParameters getPageParameters() {
        if (pageFactory != null) {
            return pageFactory.getPageParameters();
        } else {
            return page.getBrixPageParameters();
        }
    }

// -------------------------- OTHER METHODS --------------------------

    protected void respondWithInitialRedirectHandled(RequestCycle requestCycle) {
        // check if the listener invocation or something else hasn't changed the
        // request target
        if (RequestCycle.get().getRequestTarget() == this) {
            WebResponse response = (WebResponse) requestCycle.getResponse();

            // TODO figure out how to handle last modified for pages.
            // lastmodified depends on both the page and the tiles, maybe tiles
            // can contribute lastmodified dates and we take the latest...
            // response.setLastModifiedTime(Time.valueOf(node.getObject().getLastModified()));

            getPage().renderPage();

            // must be after render page because Page.configureResponse() sets
            // response.setContentType("text/" + getMarkupType() + "; charset=" + encoding);
            String mimeType = getMimeType(node.getObject());
            String encoding = requestCycle.getApplication().getRequestCycleSettings().getResponseRequestEncoding();
            response.setContentType(mimeType + "; charset=" + encoding);
        }
    }

    protected static String getMimeType(BrixNode brixNode) {
        BrixFileNode brixFileNode = new BrixFileNode(brixNode.getDelegate(), brixNode.getSession());

        String mimeType = null;
        mimeType = brixFileNode.getMimeType();

        if (mimeType == null || mimeType.trim().isEmpty()) {
            mimeType = "text/html";
        }
        return mimeType;
    }

// -------------------------- INNER CLASSES --------------------------

    public static interface PageFactory {
        public BrixNodeWebPage newPage();

        public BrixPageParameters getPageParameters();
    }

    ;
}
