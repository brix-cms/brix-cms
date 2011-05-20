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

package org.brixcms.web;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.brixcms.jcr.wrapper.BrixNode.Protocol;

import javax.servlet.http.HttpServletRequest;

public class SwitchProtocolRequestTarget implements IRequestTarget {
// ------------------------------ FIELDS ------------------------------

    private final Protocol protocol;

// -------------------------- STATIC METHODS --------------------------

    public static IRequestTarget requireProtocol(Protocol protocol) {
        RequestCycle requestCycle = RequestCycle.get();
        WebRequest webRequest = (WebRequest) requestCycle.getRequest();
        HttpServletRequest request = webRequest.getHttpServletRequest();
        if (protocol == null || protocol == Protocol.PRESERVE_CURRENT ||
                request.getScheme().equals(protocol.toString().toLowerCase())) {
            return null;
        } else {
            return new SwitchProtocolRequestTarget(protocol);
        }
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public SwitchProtocolRequestTarget(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("Argument 'protocol' may not be null.");
        }
        if (protocol == Protocol.PRESERVE_CURRENT) {
            throw new IllegalArgumentException("Argument 'protocol' may not have value '" + Protocol.PRESERVE_CURRENT.toString() + "'.");
        }
        this.protocol = protocol;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IRequestTarget ---------------------


    public void respond(RequestCycle requestCycle) {
        WebRequest webRequest = (WebRequest) requestCycle.getRequest();
        HttpServletRequest request = webRequest.getHttpServletRequest();

        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor) requestCycle
                .getProcessor();
        Integer port = null;
        if (protocol == Protocol.HTTP) {
            if (processor.getHttpPort() != 80) {
                port = processor.getHttpPort();
            }
        } else if (protocol == Protocol.HTTPS) {
            if (processor.getHttpsPort() != 443) {
                port = processor.getHttpsPort();
            }
        }

        String url = getUrl(protocol.toString().toLowerCase(), port, request);

        requestCycle.getResponse().redirect(url);
    }

    public void detach(RequestCycle requestCycle) {

    }

// -------------------------- OTHER METHODS --------------------------

    private String getUrl(String protocol, Integer port, HttpServletRequest request) {
        StringBuilder result = new StringBuilder();
        result.append(protocol);
        result.append("://");
        result.append(request.getServerName());
        if (port != null) {
            result.append(":");
            result.append(port);
        }
        result.append(request.getRequestURI());
        if (request.getQueryString() != null) {
            result.append("?");
            result.append(request.getQueryString());
        }
        return result.toString();
    }
}
