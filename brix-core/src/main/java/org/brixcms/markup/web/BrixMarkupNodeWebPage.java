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

package org.brixcms.markup.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.MarkupHelper;
import org.brixcms.markup.MarkupSource;
import org.brixcms.markup.MarkupSourceProvider;
import org.brixcms.web.nodepage.BrixNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;

/**
 * Page that uses {@link MarkupSource} for it's markup and child components.
 *
 * @author Matej Knopp
 */

public abstract class BrixMarkupNodeWebPage extends BrixNodeWebPage implements IMarkupResourceStreamProvider,
        IMarkupCacheKeyProvider, MarkupSourceProvider {
    private transient MarkupHelper markupHelper;

    public BrixMarkupNodeWebPage(IModel<BrixNode> nodeModel) {
        super(nodeModel);
    }

    public BrixMarkupNodeWebPage(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters) {
        super(nodeModel, pageParameters);
    }

    private MarkupHelper getMarkupHelper() {
        if (markupHelper == null) {
            markupHelper = new MarkupHelper(this);
        }
        return markupHelper;
    }


    public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
        return null;
    }


    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream(getMarkupHelper().getMarkup(), "text/html");
    }

    @Override
    protected void onBeforeRender() {
        getMarkupHelper();
        super.onBeforeRender();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        markupHelper = null;
    }
}
