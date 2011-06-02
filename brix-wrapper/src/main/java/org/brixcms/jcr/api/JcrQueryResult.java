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

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrQueryResult extends QueryResult {

    public String[] getColumnNames();

    public JcrRowIterator getRows();

    public JcrNodeIterator getNodes();

    /**
     * Returns an array of all the selector names that were used in the query that created this result. If the query did
     * not have a selector name then an empty array is returned.
     *
     * @return a <code>String</code> array holding the selector names.
     * @throws RepositoryException if an error occurs.
     */
    public String[] getSelectorNames();

// -------------------------- OTHER METHODS --------------------------
    public QueryResult getDelegate();

    public static class Wrapper {
        public static JcrQueryResult wrap(QueryResult delegate, JcrSession session) {
            return WrapperAccessor.JcrQueryResultWrapper.wrap(delegate, session);
        }
    }
}