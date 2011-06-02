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

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrRow;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrValue;

import javax.jcr.query.Row;

/**
 * @author Matej Knopp
 */
class RowWrapper extends AbstractWrapper implements JcrRow {
    public static JcrRow wrap(Row delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new RowWrapper(delegate, session);
        }
    }

    protected RowWrapper(Row delegate, JcrSession session) {
        super(delegate, session);
    }


    @Override
    public Row getDelegate() {
        return (Row) super.getDelegate();
    }


    public JcrValue[] getValues() {
        return executeCallback(new Callback<JcrValue[]>() {
            public JcrValue[] execute() throws Exception {
                return JcrValue.Wrapper.wrap(getDelegate().getValues(), getJcrSession());
            }
        });
    }

    public JcrValue getValue(final String propertyName) {
        return executeCallback(new Callback<JcrValue>() {
            public JcrValue execute() throws Exception {
                return JcrValue.Wrapper.wrap(getDelegate().getValue(propertyName), getJcrSession());
            }
        });
    }

    public JcrNode getNode() {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getNode(), getJcrSession());
            }
        });
    }

    public JcrNode getNode(final String selectorName) {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getNode(selectorName), getJcrSession());
            }
        });
    }

    public String getPath() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getPath();
            }
        });
    }

    public String getPath(final String selectorName) {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getPath(selectorName);
            }
        });
    }

    public double getScore() {
        return executeCallback(new Callback<Double>() {
            public Double execute() throws Exception {
                return getDelegate().getScore();
            }
        });
    }

    public double getScore(final String selectorName) {
        return executeCallback(new Callback<Double>() {
            public Double execute() throws Exception {
                return getDelegate().getScore(selectorName);
            }
        });
    }
}
