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

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * Stateless form that allows {@link PageParametersAware} components to contribute state to URL after form submission.
 *
 * @param <T>
 * @author Matej Knopp
 */
public class PageParametersForm<T> extends StatelessForm<T> {
    public PageParametersForm(String id) {
        super(id);
    }

    public PageParametersForm(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected boolean encodeUrlInHiddenFields() {
        return true;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        BrixNodeWebPage page = (BrixNodeWebPage) getPage();
        BrixPageParameters parameters = new BrixPageParameters(page.getBrixPageParameters());
        for (String s : parameters.getNamedKeys()) {
            if (s.startsWith("brix:") || s.equals("0")) {
                parameters.remove(s);
            }
        }
        tag.put("action", RequestCycle.get().urlFor(new BrixNodeRequestHandler(page, parameters)));
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        getRequestCycle().replaceAllRequestHandlers(getRequestHandler());
    }

    protected IRequestHandler getRequestHandler() {
        final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
        getPage().visitChildren(PageParametersAware.class, new IVisitor<Component, PageParametersAware>() {
            public void component(Component component, IVisit<PageParametersAware> pageParametersAwareIVisit) {
                throw new UnsupportedOperationException();
//                component.contributeToPageParameters(parameters);
            }
        });
        contributeToPageParameters(parameters);
        IRequestHandler target = new BrixNodeRequestHandler((BrixNodeWebPage) getPage(), parameters);
        return target;
    }

    /**
     * Returns the initial {@link BrixPageParameters} used to build the URL after form submission.
     *
     * @return
     */
    protected BrixPageParameters getInitialParameters() {
        return new BrixPageParameters();
    }

    /**
     * Allows to change {@link BrixPageParameters} after all other components have contributed their state to it. This
     * method can be used to postprocess the URL after form submission.
     */
    // Note, it is intentional that this class doesn't implement
    // PageParametersAware. This method needs to be invoked after all other
    // components have contributed their state. Also it should be only used
    // when this Form is constructing the URL.
    protected void contributeToPageParameters(BrixPageParameters parameters) {

    }

    @Override
    protected void writeParamsAsHiddenFields(String[] params, AppendingStringBuffer buffer) {
        for (int j = 0; j < params.length; j++) {
            String[] pair = params[j].split("=");

            if (pair[0].startsWith(getInputNamePrefix())) {
                buffer.append("<input type=\"hidden\" name=\"").append(pair[0]).append("\" value=\"").append(
                        pair.length > 1 ? pair[1] : "").append("\" />");
            }
        }
    }

    @Override
    protected String getInputNamePrefix() {
        return "brix:";
    }
}
