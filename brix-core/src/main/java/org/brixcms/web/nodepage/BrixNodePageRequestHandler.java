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

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.IPageProvider;
import org.apache.wicket.request.handler.IPageRequestHandler;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.jcr.wrapper.BrixNode;

public class BrixNodePageRequestHandler extends RenderPageRequestHandler implements IRequestHandler, IPageRequestHandler,
        PageParametersRequestHandler {
    // ------------------------------ FIELDS ------------------------------

    private final IModel<BrixNode> node;
    private final PageFactory pageFactory;

    // --------------------------- CONSTRUCTORS ---------------------------

    public BrixNodePageRequestHandler(IModel<BrixNode> node, BrixNodeWebPage page) {
        super(new PageProviderAdapter(page), RedirectPolicy.NEVER_REDIRECT);
        this.node = node;
        this.pageFactory = null;
    }

    public BrixNodePageRequestHandler(IModel<BrixNode> node, PageFactory pageFactory) {
        super(new PageProviderAdapter(pageFactory), RedirectPolicy.NEVER_REDIRECT);
        this.node = node;
        this.pageFactory = pageFactory;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public BrixNodeWebPage getPage() {
        return (BrixNodeWebPage) super.getPage();
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface IRequestHandler ---------------------

    public final void respond(IRequestCycle requestCycle) {
        // if (page == null) {
        // page = pageFactory.newPage();
        // if (page.initialRedirect()) {
        // // if the page is newly created and initial redirect is set, we
        // // need to redirect to a hybrid URL
        // page.setStatelessHint(false);
        // Session.get().bind();
        // // Session.get().touch(page);
        // requestCycle.scheduleRequestHandlerAfterCurrent(new
        // BrixNodeRequestHandler(page));
        // return;
        // }
        // }

        respondWithInitialRedirectHandled(requestCycle);
    }

    public void detach(IRequestCycle requestCycle) {
        super.detach(requestCycle);
        node.detach();
    }

    // --------------------- Interface PageParametersRequestTarget
    // ---------------------

    public BrixPageParameters getPageParameters() {
        if (pageFactory != null) {
            return pageFactory.getPageParameters();
        } else {
            return getPage().getBrixPageParameters();
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    protected void respondWithInitialRedirectHandled(IRequestCycle requestCycle) {
        // check if the listener invocation or something else hasn't changed the
        // request target
        if (RequestCycle.get().getActiveRequestHandler() == this) {

            super.respond(requestCycle);

        }
    }



    // -------------------------- INNER CLASSES --------------------------

    public static interface PageFactory {
        public BrixNodeWebPage newPage();

        public BrixPageParameters getPageParameters();
    }


    private static class PageProviderAdapter implements IPageProvider {
        private BrixNodeWebPage page;
        private PageFactory factory;

        public PageProviderAdapter(PageFactory factory) {
            this.factory = factory;
        }

        public PageProviderAdapter(BrixNodeWebPage page) {
            this.page = page;
        }

        @Override
        public IRequestablePage getPageInstance() {
            if (page == null) {
                page = factory.newPage();
            }
            return page;
        }

        @Override
        public PageParameters getPageParameters() {
            if (page != null) {
                return page.getPageParameters();
            } else {
                return factory.getPageParameters();
            }
        }

        @Override
        public boolean isNewPageInstance() {
            return page == null;
        }

        @Override
        public Class<? extends IRequestablePage> getPageClass() {
            return BrixNodeWebPage.class;
        }

        @Override
        public void detach() {
            if (page != null)
                page.detach();
        }

        @Override
        public Integer getPageId() {
            if (page != null) {
                return page.getPageId();
            }
            return null;
        }

        @Override
        public Integer getRenderCount() {
            if (page != null) {
                return page.getRenderCount();
            }
            return null;
        }
    }
}