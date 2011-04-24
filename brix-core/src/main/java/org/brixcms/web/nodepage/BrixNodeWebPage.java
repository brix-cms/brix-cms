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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.IGenericComponent;

public class BrixNodeWebPage extends WebPage implements IGenericComponent<BrixNode> {
// ------------------------------ FIELDS ------------------------------

    private BrixPageParameters pageParameters;

// --------------------------- CONSTRUCTORS ---------------------------

    public BrixNodeWebPage(IModel<BrixNode> nodeModel) {
        super(nodeModel);
    }

    public BrixNodeWebPage(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters) {
        super(nodeModel);
        this.pageParameters = pageParameters;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IGenericComponent ---------------------

    @SuppressWarnings("unchecked")
    public IModel<BrixNode> getModel() {
        return (IModel<BrixNode>) getDefaultModel();
    }

    public void setModel(IModel<BrixNode> model) {
        setDefaultModel(model);
    }

    public void setModelObject(BrixNode object) {
        setDefaultModelObject(object);
    }

// -------------------------- OTHER METHODS --------------------------

    public BrixPageParameters getBrixPageParameters() {
        if (pageParameters == null) {
            pageParameters = new BrixPageParameters();
        }
        return pageParameters;
    }

    public BrixNode getPageNode() {
        return getModelObject();
    }

    public boolean initialRedirect() {
        return false;
    }

    @Override
    public boolean isBookmarkable() {
        return true;
    }

    @Override
    protected void onBeforeRender() {
        checkAccess();
        super.onBeforeRender();
    }

    protected void checkAccess() {
        BrixNode node = getModelObject();
        if (!SitePlugin.get().canViewNode(node, Context.PRESENTATION)) {
            throw Brix.get().getForbiddenException();
        }
    }

    public BrixNode getModelObject() {
        return (BrixNode) getDefaultModelObject();
    }
}
