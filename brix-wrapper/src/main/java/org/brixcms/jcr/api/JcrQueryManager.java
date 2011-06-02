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

package org.brixcms.jcr.api;

import org.brixcms.jcr.api.wrapper.WrapperAccessor;

import javax.jcr.Node;
import javax.jcr.query.QueryManager;
import javax.jcr.query.qom.QueryObjectModelFactory;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrQueryManager extends QueryManager {

    public JcrQuery createQuery(String statement, String language);

    /**
     * Returns a <code>QueryObjectModelFactory</code> with which a JCR-JQOM query can be built programmatically.
     *
     * @return a <code>QueryObjectModelFactory</code> object
     * @since JCR 2.0
     */
    public QueryObjectModelFactory getQOMFactory();

    public JcrQuery getQuery(Node node);

    public String[] getSupportedQueryLanguages();

    public QueryManager getDelegate();

    public static class Wrapper {
        public static JcrQueryManager wrap(QueryManager delegate, JcrSession session) {
            return WrapperAccessor.JcrQueryManagerWrapper.wrap(delegate, session);
        }
    }
}