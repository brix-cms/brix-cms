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

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.WebResponse;
import org.brixcms.Brix;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.IGenericComponent;

public class BrixNodeWebPage extends WebPage implements IGenericComponent<BrixNode> {

    public BrixNodeWebPage(IModel<BrixNode> nodeModel) {
        super(nodeModel);
    }

    public BrixNodeWebPage(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters) {
        super(nodeModel);
        if (pageParameters != null) {
            getPageParameters().overwriteWith(pageParameters);
        }
    }


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

    public BrixPageParameters getBrixPageParameters() {
        return new BrixPageParameters(getPageParameters());
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

    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        String mimeType = getMimeType(getModelObject());
        String encoding = Application.get().getRequestCycleSettings().getResponseRequestEncoding();
        ((WebResponse) getResponse()).setContentType(mimeType + "; charset=" + encoding);

        // TODO figure out how to handle last modified for pages.
        // lastmodified depends on both the page and the tiles, maybe tiles
        // can contribute lastmodified dates and we take the latest...
        // response.setLastModifiedTime(Time.valueOf(node.getObject().getLastModified()));
    }

    protected static String getMimeType(BrixNode brixNode) {
        BrixFileNode brixFileNode = new BrixFileNode(brixNode.getDelegate(), brixNode.getSession());

        String mimeType = null;
        mimeType = brixFileNode.getMimeType();

        if (mimeType == null || mimeType.trim().isEmpty()) {
            mimeType = "text/html";
        }
        return mimeType;
    }
}