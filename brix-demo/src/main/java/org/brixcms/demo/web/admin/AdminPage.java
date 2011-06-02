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

package org.brixcms.demo.web.admin;

import org.apache.wicket.markup.html.WebPage;
import org.brixcms.web.admin.AdminPanel;

/**
 * This page hosts Brix's {@link AdminPanel}
 *
 * @author igor.vaynberg
 */
public class AdminPage extends WebPage {
    /**
     * Constructor
     */
    public AdminPage() {
        add(new AdminPanel("admin", null));
    }
}
