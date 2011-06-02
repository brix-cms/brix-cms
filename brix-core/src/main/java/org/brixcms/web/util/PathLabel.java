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

package org.brixcms.web.util;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.util.string.PrependingStringBuffer;
import org.apache.wicket.util.value.ValueMap;
import org.brixcms.Path;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericWebMarkupContainer;

public abstract class PathLabel extends BrixGenericWebMarkupContainer<BrixNode> implements ILinkListener {
    private final String rootPath;

    public PathLabel(String id, IModel<BrixNode> model, String rootPath) {
        super(id, model);
        this.rootPath = rootPath;
    }


    public final void onLinkClicked() {
        String path = getRequest().getRequestParameters().getParameterValue("path").toString();
//        if (path == null) {
//            path = getRequestCycle().getPageParameters().getString("path");
//        }
        path = UrlDecoder.QUERY_INSTANCE.decode(path, getRequest().getCharset());
        onPathClicked(new Path(path));
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        PrependingStringBuffer b = new PrependingStringBuffer();
        BrixNode current = getModelObject();

        while (true) {
            StringBuilder builder = new StringBuilder();
            writePath(current, builder, current.equals(getModelObject()));
            if (b.length() > 0) {
                b.prepend("&nbsp;/&nbsp;");
            }
            b.prepend(builder.toString());
            if (current.getDepth() == 0 || current.getPath().equals(rootPath)) {
                break;
            }
            current = (BrixNode) current.getParent();
        }

        final Response r = getResponse();
        r.write(b.toString());
    }

    private void writePath(BrixNode node, StringBuilder builder, boolean last) {
        builder.append("<a href=\"");
        builder.append(createCallbackUrl(node.getPath()));
        builder.append("\"><span");
        if (last) {
            builder.append(" class=\"brix-node-path-last\"");
        }
        builder.append(">");
        builder.append(node.getUserVisibleName());
        builder.append("</span></a>");
    }

    private CharSequence createCallbackUrl(String subpath) {
        Url url=Url.parse(urlFor(ILinkListener.INTERFACE).toString());
        url.addQueryParameter("path", subpath);
        return url.toString(getRequest().getCharset());
    }

    protected abstract void onPathClicked(Path path);
}
