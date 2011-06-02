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

package org.brixcms.markup;

import org.brixcms.markup.web.BrixMarkupNodePanel;
import org.brixcms.markup.web.BrixMarkupNodeWebPage;

/**
 * Implemented by components that can provide {@link MarkupSource}.
 *
 * @author Matej Knopp
 * @see BrixMarkupNodePanel
 * @see BrixMarkupNodeWebPage
 * @see MarkupCache
 */
public interface MarkupSourceProvider {
    public MarkupSource getMarkupSource();
}
