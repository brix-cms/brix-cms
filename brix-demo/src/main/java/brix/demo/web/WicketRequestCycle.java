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

package brix.demo.web;

import javax.jcr.Session;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

/**
 * Subclass of {@link WebRequestCycle} that cleans any open Jcr {@link Session}s at the end of
 * request
 * 
 * @author igor.vaynberg
 * 
 */
public class WicketRequestCycle extends WebRequestCycle
{
    /**
     * Constructor
     * 
     * @param application
     * @param request
     * @param response
     */
    public WicketRequestCycle(WebApplication application, WebRequest request, Response response)
    {
        super(application, request, response);
    }

    /** {@inheritDoc} */
    @Override
    protected void onEndRequest()
    {
        // clean up sessions
        AbstractWicketApplication.get().cleanupSessionFactory();
    }
}
