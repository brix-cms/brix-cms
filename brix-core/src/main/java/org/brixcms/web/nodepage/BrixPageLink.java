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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.brixcms.jcr.wrapper.BrixNode;

/**
 * A component that links to a page with specified page parameters.
 * <p/>
 * This component is different from {@link PageParametersLink} because it does not allow other components to contribute
 * their page parameters to the generated url and therefore does not propagate state from one page to the next.
 * <p/>
 * TODO expand to support tags other then anchor
 *
 * @author igor.vaynberg
 */
public class BrixPageLink extends AbstractLink {
    // TODO optimize into minimap just like BookmarkablePageLink
    private final BrixPageParameters params;

    public BrixPageLink(String id, IModel<BrixNode> destination) {
        super(id, destination);
        this.params = null;
    }

    public BrixPageLink(String id, IModel<BrixNode> destination, BrixPageParameters params) {
        super(id, destination);
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "a");

        if (!isLinkEnabled()) {
            disableLink(tag);
        } else {
            BrixPageParameters params = this.params;
            if (params == null) {
                params = new BrixPageParameters();
            }
            String url = params.urlFor((IModel<BrixNode>) getDefaultModel());
            tag.put("href", Strings.replaceAll(url, "&", "&amp;"));
        }
    }
}
