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
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.IPageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;

public class BrixNodeRequestHandler implements IPageRequestHandler {
    private final IModel<BrixNode> nodeModel;
    private final BrixNodeWebPage page;
    private BrixPageParameters parameters;

    public BrixNodeRequestHandler(IModel<BrixNode> nodeModel) {
        this(nodeModel, new BrixPageParameters());
    }

    public BrixNodeRequestHandler(BrixNodeWebPage page) {
        this(page, page.getBrixPageParameters());
    }

    public BrixNodeRequestHandler(IModel<BrixNode> nodeModel, BrixPageParameters parameters) {
        if (nodeModel == null) {
            throw new IllegalArgumentException("Argument 'nodeModel' may not be null.");
        }

        if (parameters == null) {
            throw new IllegalArgumentException("Argument 'parameters' may not be null.");
        }

        this.nodeModel = nodeModel;
        this.parameters = parameters;
        this.page = null;
    }

    public BrixNodeRequestHandler(BrixNodeWebPage page, BrixPageParameters parameters) {
        if (page == null) {
            throw new IllegalArgumentException("Argument 'page' may not be null.");
        }

        if (parameters == null) {
            throw new IllegalArgumentException("Argument 'parameters' may not be null.");
        }

        this.page = page;
        this.nodeModel = page.getModel();
        this.parameters = parameters;
    }

    public BrixNodeWebPage getPage() {
        return page;
    }



    @Override
    public Class<? extends IRequestablePage> getPageClass() {
        return page.getClass();
    }

    public PageParameters getPageParameters() {
        return parameters;
    }

    public void respond(IRequestCycle requestCycle) {
        CharSequence url = ((RequestCycle) requestCycle).urlFor(this);
//        requestCycle.redirect(url.toString());
        throw new UnsupportedOperationException();
    }

    public void detach(IRequestCycle requestCycle) {

    }

    public String getNodeURL() {
        try {
            // BrixRequestCycleProcessor processor = new
            // BrixRequestCycleProcessor(Brix.get());
            // return
            // processor.getUriPathForNode(nodeModel.getObject()).toString();
            return SitePlugin.get().getUriPathForNode(nodeModel.getObject()).toString();
        } finally {
            nodeModel.detach();
        }
    }

    @Override
    public Integer getPageId() {
        if (page != null) {
            return page.getPageId();
        }
        return null;
    }

    @Override
    public boolean isPageInstanceCreated() {
        return page == null;
    }

    @Override
    public Integer getRenderCount() {
        if (page != null) {
            return page.getRenderCount();
        }
        return null;
    }
}