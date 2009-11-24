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

package brix.web.nodepage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * Base class that knows how to recursively collect parameters from child nodes that implement {@link PageParametersAware}.
 * Child nodes contribute parameters that they need for future instantiation, allowing a stateful URL to be composed from
 * the dynamic closure of the page and it's tiles.
 *
 * @author Igor Vaynberg
 */
public abstract class AbstractPageParametersLink extends AbstractLink
{

    public AbstractPageParametersLink(String id, IModel< ? > model)
    {
        super(id, model);
    }

    public AbstractPageParametersLink(String id)
    {
        super(id);
    }

    protected abstract String constructUrl(BrixPageParameters params);

    protected String buildUrl()
    {
        final BrixPageParameters parameters = new BrixPageParameters(getInitialParameters());
        getPage().visitChildren(PageParametersAware.class, new IVisitor<Component>()
        {
            public Object component(Component component)
            {
                ((PageParametersAware)component).contributeToPageParameters(parameters);
                return IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        contributeToPageParameters(parameters);
        return constructUrl(parameters);
    }

    /**
     * Allows to change {@link BrixPageParameters} after all other components have contributed their
     * state to it. This method can be used to postprocess the link URL.
     */
    protected void contributeToPageParameters(BrixPageParameters parameters)
    {

    }

    /**
     * Returns the initial {@link BrixPageParameters} used to build the URL.
     *
     * @return
     */
    protected BrixPageParameters getInitialParameters()
    {
        return new BrixPageParameters();
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);

        // If we're disabled
        if (!isLinkEnabled())
        {
            disableLink(tag);
        }
        else
        {
            String url = buildUrl();

            if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link") ||
                    tag.getName().equalsIgnoreCase("area"))
            {
                // generate the href attribute
                tag.put("href", Strings.replaceAll(url, "&", "&amp;"));
            }
            else
            {
                // or generate an onclick JS handler directly
                // in firefox when the element is quickly clicked 3 times a
                // second request is
                // generated during page load. This check ensures that the click
                // is ignored
                tag
                        .put(
                                "onclick",
                                "var win = this.ownerDocument.defaultView || this.ownerDocument.parentWindow; " +
                                        "if (win == window) { window.location.href='" +
                                        Strings.replaceAll(url, "&", "&amp;") +
                                        "'; } ;return false");
            }
        }

    }

}