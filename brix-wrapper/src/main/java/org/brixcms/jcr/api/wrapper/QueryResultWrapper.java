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

import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrQueryResult;
import org.brixcms.jcr.api.JcrRowIterator;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.query.QueryResult;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
class QueryResultWrapper extends AbstractWrapper implements JcrQueryResult {
    public static JcrQueryResult wrap(QueryResult delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new QueryResultWrapper(delegate, session);
        }
    }

    protected QueryResultWrapper(QueryResult delegate, JcrSession session) {
        super(delegate, session);
    }


    @Override
    public QueryResult getDelegate() {
        return (QueryResult) super.getDelegate();
    }


    public String[] getColumnNames() {
        return executeCallback(new Callback<String[]>() {
            public String[] execute() throws Exception {
                return getDelegate().getColumnNames();
            }
        });
    }

    public JcrRowIterator getRows() {
        return executeCallback(new Callback<JcrRowIterator>() {
            public JcrRowIterator execute() throws Exception {
                return JcrRowIterator.Wrapper.wrap(getDelegate().getRows(), getJcrSession());
            }
        });
    }

    public JcrNodeIterator getNodes() {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(), getJcrSession());
            }
        });
    }

    public String[] getSelectorNames() {
        return executeCallback(new Callback<String[]>() {
            public String[] execute() throws Exception {
                return getDelegate().getSelectorNames();
            }
        });
    }
}
