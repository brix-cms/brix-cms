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
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * Constructs a link to another page including all parameters from the current page. This link makes it easy to link
 * together tiles located on different pages.
 * <p/>
 * To use this to create a link in a BrixPanel to another BrixNode, try <code> BrixNode brixNode = (BrixNode)
 * tileNode.getObject().getNode("/path/to/node"); BrixNodeModel brixNodeModel = new BrixNodeModel(brixNode); add(new
 * PageParametersCarryingLink("markupId", brixNodeModel)); </code>
 *
 * @author igor.vaynberg
 * @todo it's unclear how this is semantically distinguished from {@link PageParametersLink}
 */
public class PageParametersCarryingLink extends AbstractPageParametersLink {
    private final IModel<BrixNode> page;

    /**
     * Constructor
     *
     * @param id
     * @param page
     */
    public PageParametersCarryingLink(String id, IModel<BrixNode> page) {
        this(id, null, page);
    }

    /**
     * Constructor
     *
     * @param id
     * @param model
     * @param page
     */
    public PageParametersCarryingLink(String id, IModel<?> model, IModel<BrixNode> page) {
        super(id, model);
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String constructUrl(BrixPageParameters params) {
        return params.urlFor(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetach() {
        page.detach();
        super.onDetach();
    }
}
