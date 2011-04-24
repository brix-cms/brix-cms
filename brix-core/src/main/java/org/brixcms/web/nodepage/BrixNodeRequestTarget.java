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

import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.BrixRequestCycleProcessor;

public class BrixNodeRequestTarget implements IPageRequestTarget {
// ------------------------------ FIELDS ------------------------------

    private final IModel<BrixNode> nodeModel;
    private final BrixNodeWebPage page;
    private BrixPageParameters parameters;

// --------------------------- CONSTRUCTORS ---------------------------

    public BrixNodeRequestTarget(IModel<BrixNode> nodeModel) {
        this(nodeModel, new BrixPageParameters());
    }

    public BrixNodeRequestTarget(BrixNodeWebPage page) {
        this(page, page.getBrixPageParameters());
    }

    public BrixNodeRequestTarget(IModel<BrixNode> nodeModel, BrixPageParameters parameters) {
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

    public BrixNodeRequestTarget(BrixNodeWebPage page, BrixPageParameters parameters) {
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

// --------------------- GETTER / SETTER METHODS ---------------------

    public BrixNodeWebPage getPage() {
        return page;
    }

    public BrixPageParameters getParameters() {
        return parameters;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IRequestTarget ---------------------


    public void respond(RequestCycle requestCycle) {
        CharSequence url = requestCycle.urlFor(this);
        requestCycle.getResponse().redirect(url.toString());
    }

    public void detach(RequestCycle requestCycle) {

    }

// -------------------------- OTHER METHODS --------------------------

    public String getNodeURL() {
        try {
            return ((BrixRequestCycleProcessor) RequestCycle.get().getProcessor())
                    .getUriPathForNode(

                            nodeModel.getObject()).toString();
        } finally {
            nodeModel.detach();
        }
    }
}
