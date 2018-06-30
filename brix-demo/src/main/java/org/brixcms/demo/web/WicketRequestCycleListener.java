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

package org.brixcms.demo.web;

import org.apache.wicket.core.request.handler.ComponentNotFoundException;
import org.apache.wicket.core.request.mapper.MapperUtils;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.info.PageComponentInfo;

import javax.jcr.Session;

/**
 * Implemention of {@link IRequestCycleListener} that cleans any open Jcr {@link Session}s at the end of request
 *
 * @author igor.vaynberg
 */
public class WicketRequestCycleListener implements IRequestCycleListener {

    @Override
    public void onBeginRequest(RequestCycle cycle) {
        System.out.println("fo1?");
    }

    @Override
    public void onEndRequest(RequestCycle cycle) {
        AbstractWicketApplication.get().cleanupSessionFactory();
    }

    /**
     *
     * this is an example how one can fix errors that may arise in production when the URLs change over time
     * as the components on a page evolve because id of components change
     *
     * e.g.:
     * ./stockquote.html?2-1.-brix~tile~6-form -> may be ok now
     * ./stockquote.html?2-1.-brix~tile~6-coolform -> may be ok in future
     *
     * -> if not found does a 302 to the base path
     * ./stockquote.html
     *
     * @param cycle
     * @param ex
     * @return
     */
    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
        if(ex instanceof ComponentNotFoundException) {
            Url originalUrl = cycle.getRequest().getUrl();
            PageComponentInfo info = MapperUtils.getPageComponentInfo(originalUrl);
            String _newUrl = originalUrl.toString().replace(info.toString(), "");
            if(_newUrl.endsWith("?")) {
                _newUrl = _newUrl.substring(0, _newUrl.length()-1);
            }
            cycle.replaceAllRequestHandlers(new RedirectRequestHandler("/" + _newUrl));
        }

        return null;
    }

    @Override
    public void onExceptionRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler, Exception exception) {
        if(exception instanceof ComponentNotFoundException) {
           System.out.println("fooo");
        }

    }
}
