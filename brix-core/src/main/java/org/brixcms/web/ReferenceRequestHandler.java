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

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.util.string.Strings;
import org.brixcms.web.reference.Reference;

public class ReferenceRequestHandler extends RedirectRequestHandler {
    public ReferenceRequestHandler(Reference reference) {
        super(referenceToUrl(reference));
    }

    private static String referenceToUrl(Reference reference) {
        if (reference == null) {
            throw new IllegalArgumentException("reference cannot be null");
        }
        String url = reference.generateUrl();
        if (Strings.isEmpty(url)) {
            url = "/";
        }
        return url;
    }
}
