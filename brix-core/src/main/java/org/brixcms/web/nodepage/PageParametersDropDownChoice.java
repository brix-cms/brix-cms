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
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Special kind of {@link DropDownChoice} that checks for {@link PageParametersAware} components when constructing URL
 * to redriect after {@link #onSelectionChanged()}.
 *
 * @param <T>
 * @author Matej Knopp
 */
public class PageParametersDropDownChoice<T> extends DropDownChoice<T> {
// --------------------------- CONSTRUCTORS ---------------------------

    public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> choices) {
        super(id, model, choices);
    }

    public PageParametersDropDownChoice(String id, IModel<T> model, IModel<List<? extends T>> choices) {
        super(id, model, choices);
    }

    public PageParametersDropDownChoice(String id, IModel<T> model, List<? extends T> data, IChoiceRenderer<T> renderer) {
        super(id, model, data, renderer);
    }

    public PageParametersDropDownChoice(String id, IModel<T> model, IModel<List<? extends T>> choices,
                                        IChoiceRenderer<T> renderer) {
        super(id, model, choices, renderer);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected boolean getStatelessHint() {
        return true;
    }

    @Override
    protected void onSelectionChanged(Object newSelection) {
        getRequestCycle().setRequestTarget(getRequestTarget());
    }

    protected IRequestTarget getRequestTarget() {
        final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
        getPage().visitChildren(PageParametersAware.class, new Component.IVisitor<Component>() {
            public Object component(Component component) {
                ((PageParametersAware) component).contributeToPageParameters(parameters);
                return Component.IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        contributeToPageParameters(parameters);
        IRequestTarget target = new BrixNodeRequestTarget((BrixNodeWebPage) getPage(), parameters);
        return target;
    }

    /**
     * Returns the initial {@link BrixPageParameters} used to build the URL after selection change.
     *
     * @return
     */
    protected BrixPageParameters getInitialParameters() {
        return new BrixPageParameters();
    }

    /**
     * Allows to change {@link BrixPageParameters} after all other components have contributed their state to it. This
     * method can be used to postprocess the URL after selection change.
     */
    // Note, it is intentional that this class doesn't implement
    // PageParametersAware. This method needs to be invoked after all other
    // components have contributed their state. Also it should be only used
    // when this DropDownChoice is constructing the URL.
    protected void contributeToPageParameters(BrixPageParameters parameters) {

    }
}
