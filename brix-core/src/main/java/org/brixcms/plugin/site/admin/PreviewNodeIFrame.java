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

package org.brixcms.plugin.site.admin;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.web.generic.BrixGenericWebMarkupContainer;
import org.brixcms.web.nodepage.BrixNodeRequestHandler;
import org.brixcms.web.nodepage.BrixPageParameters;

public class PreviewNodeIFrame extends BrixGenericWebMarkupContainer<BrixNode> {
    private static final String PREVIEW_PARAM = Brix.NS_PREFIX + "preview";

    public static boolean isPreview() {
        BrixPageParameters params = BrixPageParameters.getCurrent();
        if (params != null) {
            if (params.get(PREVIEW_PARAM).toBoolean(false)) {
                return true;
            }
        }
        return false;
    }

    public PreviewNodeIFrame(String id, IModel<BrixNode> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    private CharSequence getUrl() {
        BrixPageParameters parameters = new BrixPageParameters();
        IModel<BrixNode> nodeModel = getModel();
        String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
        parameters.set(BrixRequestCycleProcessor.WORKSPACE_PARAM, workspace);
        parameters.set(PREVIEW_PARAM, "true");
        StringBuilder url = new StringBuilder(getRequestCycle()
                .urlFor(new BrixNodeRequestHandler(nodeModel, parameters)));
        return url;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("src", getUrl());
    }
}
