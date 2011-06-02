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

/**
 * Stateless link component that allows {@link PageParametersAware} components to contribute to it's URL. This link
 * doesn't have a callback method, instead it allows user to override it's {@link #contributeToPageParameters(BrixPageParameters)}
 * method to postprocess the link's URL.
 *
 * @author Matej Knopp
 * @todo it's unclear how this is semantically distinguished from {@link PageParametersCarryingLink}
 */
public class PageParametersLink extends AbstractPageParametersLink {
    public PageParametersLink(String id) {
        super(id);
    }

    public PageParametersLink(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected String constructUrl(BrixPageParameters params) {
        return params.toCallbackURL();
    }
}
