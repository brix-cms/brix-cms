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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

class QueryWrapper extends BaseWrapper<Query> implements Query {
    public static QueryWrapper wrap(Query delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new QueryWrapper(delegate, session);
        }
    }

    private QueryWrapper(Query delegate, SessionWrapper session) {
        super(delegate, session);
    }


    public QueryResult execute() throws RepositoryException {
        return QueryResultWrapper.wrap(getDelegate().execute(), getSessionWrapper());
    }

    public void setLimit(long limit) {
        getDelegate().setLimit(limit);
    }

    public void setOffset(long offset) {
        getDelegate().setOffset(offset);
    }

    public String getStatement() {
        return getDelegate().getStatement();
    }

    public String getLanguage() {
        return getDelegate().getLanguage();
    }

    public String getStoredQueryPath() throws RepositoryException {
        return getDelegate().getStoredQueryPath();
    }

    public Node storeAsNode(String absPath) throws RepositoryException {
        return NodeWrapper.wrap(getDelegate().storeAsNode(absPath), getSessionWrapper());
    }

    public void bindValue(String varName, Value value) throws IllegalArgumentException,
            RepositoryException {
        getDelegate().bindValue(varName, value);
    }

    public String[] getBindVariableNames() throws RepositoryException {
        return getDelegate().getBindVariableNames();
    }
}
