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
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.qom.QueryObjectModelFactory;

class QueryManagerWrapper extends BaseWrapper<QueryManager> implements QueryManager {
    public static QueryManagerWrapper wrap(QueryManager delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new QueryManagerWrapper(delegate, session);
        }
    }

    private QueryManagerWrapper(QueryManager delegate, SessionWrapper session) {
        super(delegate, session);
    }


    public Query createQuery(String statement, String language) throws RepositoryException {
        return QueryWrapper.wrap(getDelegate().createQuery(statement, language),
                getSessionWrapper());
    }

    public QueryObjectModelFactory getQOMFactory() {
        return getDelegate().getQOMFactory();
    }

    public Query getQuery(Node node) throws RepositoryException {
        return QueryWrapper.wrap(getDelegate().getQuery(unwrap(node)), getSessionWrapper());
    }

    public String[] getSupportedQueryLanguages() throws RepositoryException {
        return getDelegate().getSupportedQueryLanguages();
    }
}
