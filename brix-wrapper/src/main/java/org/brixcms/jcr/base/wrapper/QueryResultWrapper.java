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

package org.brixcms.jcr.base.wrapper;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

class QueryResultWrapper extends BaseWrapper<QueryResult> implements QueryResult {
    public static QueryResultWrapper wrap(QueryResult delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new QueryResultWrapper(delegate, session);
        }
    }

    private QueryResultWrapper(QueryResult delegate, SessionWrapper session) {
        super(delegate, session);
    }


    public String[] getColumnNames() throws RepositoryException {
        return getDelegate().getColumnNames();
    }

    public RowIterator getRows() throws RepositoryException {
        return getDelegate().getRows();
    }

    public NodeIterator getNodes() throws RepositoryException {
        return NodeIteratorWrapper.wrap(getDelegate().getNodes(), getSessionWrapper());
    }

    public String[] getSelectorNames() throws RepositoryException {
        return getDelegate().getSelectorNames();
    }
}
