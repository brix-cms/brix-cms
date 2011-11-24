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
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.IPageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.exception.BrixException;
import org.brixcms.jcr.wrapper.BrixNode;

public class BrixPageParameters extends PageParameters {
    private static final long serialVersionUID = 1L;


    public static BrixPageParameters getCurrent() {
        IRequestHandler target = RequestCycle.get().getActiveRequestHandler();
        // this is required for getting current page parameters from page
        // constructor
        // (the actual page instance is not constructed yet.
        if (target instanceof PageParametersRequestHandler) {
            return ((PageParametersRequestHandler) target).getPageParameters();
        } else {
            return getCurrentPage().getBrixPageParameters();
        }
    }

    public BrixPageParameters() {

    }

    public BrixPageParameters(PageParameters params) {
        super(params);
    }

    public BrixPageParameters(IRequestParameters params) {
        // TODO implement
    }

    void assign(BrixPageParameters other) {

        if (this != other) {
            for (int i = 0; i < other.getIndexedCount(); i++) {
                set(i, other.get(i));
            }
            for (NamedPair namedPair : other.getAllNamed()) {
                set(namedPair.getKey(), namedPair.getValue());
            }
        }
    }

    public String toCallbackURL() {
        return urlFor(getCurrentPage());
    }

    /**
     * Constructs a url to the specified page appending these page parameters
     * 
     * @param page
     * @return url
     */
    public String urlFor(BrixNodeWebPage page) {
        IRequestHandler target = new BrixNodeRequestHandler(page, this);
        return RequestCycle.get().urlFor(target).toString();
    }

    static BrixNodeWebPage getCurrentPage() {
        IRequestHandler target = RequestCycle.get().getActiveRequestHandler();
        BrixNodeWebPage page = null;
        if (target != null) {
            IRequestablePage p = ((IPageRequestHandler) target).getPage();
            if (p instanceof BrixNodeWebPage) {
                page = (BrixNodeWebPage) p;
            }
        }
        if (page == null) {
            throw new BrixException("Couldn't obtain the BrixNodeWebPage instance from RequestTarget.");
        }
        return page;
    }

    /**
     * Constructs a url to the specified page appending these page parameters
     * 
     * @param
     * @return url
     */
    public String urlFor(IModel<BrixNode> node) {
        IRequestHandler target = new BrixNodeRequestHandler(node, this);
        return RequestCycle.get().urlFor(target).toString();
    }

}
