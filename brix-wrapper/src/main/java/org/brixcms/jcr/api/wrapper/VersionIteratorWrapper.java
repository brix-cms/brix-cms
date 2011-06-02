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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrVersion;
import org.brixcms.jcr.api.JcrVersionIterator;

import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

/**
 * @author Matej Knopp
 */
class VersionIteratorWrapper extends RangeIteratorWrapper implements JcrVersionIterator {
    public static JcrVersionIterator wrap(VersionIterator delegate, JcrSession session) {
        if (delegate == null)
            return null;
        else
            return new VersionIteratorWrapper(delegate, session);
    }

    public VersionIteratorWrapper(VersionIterator delegate, JcrSession session) {
        super(delegate, session);
    }



    @Override
    public Object next() {
        return JcrVersion.Wrapper.wrap((Version) getDelegate().next(), getJcrSession());
    }

    @Override
    public VersionIterator getDelegate() {
        return (VersionIterator) super.getDelegate();
    }


    public JcrVersion nextVersion() {
        return JcrVersion.Wrapper.wrap(getDelegate().nextVersion(), getJcrSession());
    }
}
