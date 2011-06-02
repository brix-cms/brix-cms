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

import org.brixcms.markup.tag.Item;

/**
 * Simple wrapper for {@link MarkupSource} that forwards all calls to delegate instance.
 *
 * @author Matej Knopp
 */
public class MarkupSourceWrapper implements MarkupSource {
    private final MarkupSource delegate;

    public MarkupSourceWrapper(MarkupSource delegate) {
        this.delegate = delegate;
    }

    public MarkupSource getDelegate() {
        return delegate;
    }



    public String getDoctype() {
        return delegate.getDoctype();
    }

    public Object getExpirationToken() {
        return delegate.getExpirationToken();
    }

    public boolean isMarkupExpired(Object expirationToken) {
        return delegate.isMarkupExpired(expirationToken);
    }

    public Item nextMarkupItem() {
        return delegate.nextMarkupItem();
    }
}
