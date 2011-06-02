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

/**
 * Interface implemented by components that want to store certain state in page URL.
 * <p/>
 * Used by the PageParameters* components when generating URL. All components implementing this interface are visited
 * and asked to contribute their state to {@link BrixPageParameters}.
 *
 * @author Matej Knopp
 * @see PageParametersDropDownChoice
 * @see PageParametersForm
 * @see PageParametersLink
 */
public interface PageParametersAware {
    /**
     * Called when new URL is being constructed. Component should contribute the state it wants to store in URL to the
     * <code>params</code> object
     *
     * @param params
     */
    public void contributeToPageParameters(BrixPageParameters params);

    /**
     * Called before component's onBeforeRender. This method allows component get state from the given <code>params</code>
     * object.
     *
     * @param params
     */
    public void initializeFromPageParameters(BrixPageParameters params);
}
