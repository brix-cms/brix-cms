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
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrRow extends Row {


    public Value[] getValues();

    public JcrValue getValue(String propertyName);

    /**
     * Returns the <code>Node</code> corresponding to this <code>Row</code>.
     *
     * @return a <code>Node</code>
     * @throws RepositoryException if this query has more than one selector (and therefore, this <code>Row</code>
     *                             corresponds to more than one <code>Node</code>) or if another error occurs.
     * @since JCR 2.0
     */
    public Node getNode();

    /**
     * Returns the <code>Node</code> corresponding to this <code>Row</code> and the specified selector. If this
     * <code>Row</code> is from a result involving outer joins, it may have no <code>Node</code> corresponding to the
     * specified selector. In such a case this method returns <code>null</code>.
     *
     * @param selectorName a <code>String</code>
     * @return a <code>Node</code>
     * @throws RepositoryException if <code>selectorName</code> is not the alias of a selector in this query or if
     *                             another error occurs.
     * @since JCR 2.0
     */
    public Node getNode(String selectorName);

    /**
     * Equivalent to <code>Row.getNode().getPath()</code>. However, some implementations may be able gain efficiency by
     * not resolving the actual <code>Node</code>.
     *
     * @return a <code>String</code>
     * @throws RepositoryException if this query has more than one selector (and therefore, this <code>Row</code>
     *                             corresponds to more than one <code>Node</code>) or if another error occurs.
     * @since JCR 2.0
     */
    public String getPath();

    /**
     * Equivalent to <code>Row.getNode(selectorName).getPath()</code>. However, some implementations may be able gain
     * efficiency by not resolving the actual <code>Node</code>. If this <code>Row</code> is from a result involving
     * outer joins, it may have no <code>Node</code> corresponding to the specified selector. In such a case this method
     * returns <code>null</code> .
     *
     * @param selectorName a <code>String</code>
     * @return a <code>String</code>
     * @throws RepositoryException if <code>selectorName</code> is not the alias of a selector in this query or if
     *                             another error occurs.
     * @since JCR 2.0
     */
    public String getPath(String selectorName);

    /**
     * Returns the full text search score for this row associated with the default selector. This corresponds to the
     * score of a particular node.
     * <p>
     * If no <code>FullTextSearchScore</code> AQM object is associated with the default selector this method will still
     * return a value. However, in that case the returned value may not be meaningful or may simply reflect the minimum
     * possible relevance level (for example, in some systems this might be a score of 0).
     * <p>
     * Note, in JCR-SQL2 a <code>FullTextSearchScore</code> AQM object is represented by a <code>SCORE()</code>
     * function. In JCR-JQOM it is represented by a Java object of type <code>javax.jcr.query.qom.FullTextSearchScore</code>.
     *
     * @return a <code>double</code>
     * @throws RepositoryException if this query has more than one selector (and therefore, this <code>Row</code>
     *                             corresponds to more than one <code>Node</code>) or if another error occurs.
     * @since JCR 2.0
     */
    public double getScore();

    /**
     * Returns the full text search score for this row associated with the specified selector. This corresponds to the
     * score of a particular node.
     * <p>
     * If no <code>FullTextSearchScore</code> AQM object is associated with the selector <code>selectorName</code> this
     * method will still return a value. However, in that case the returned value may not be meaningful or may simply
     * reflect the minimum possible relevance level (for example, in some systems this might be a score of 0).
     * <p>
     * Note, in JCR-SQL2 a <code>FullTextSearchScore</code> AQM object is represented by a <code>SCORE()</code>
     * function. In JCR-JQOM it is represented by a Java object of type <code>javax.jcr.query.qom.FullTextSearchScore</code>.
     * <p>
     * If this <code>Row</code> is from a result involving outer joins, it may have no <code>Node</code> corresponding
     * to the specified selector. In such a case this method returns an implementation selected value, as it would if
     * there were no <code>FullTextSearchScore</code> associated with the selector.
     *
     * @param selectorName a <code>String</code>
     * @return a <code>double</code>
     * @throws RepositoryException if <code>selectorName</code> is not the alias of a selector in this query or if
     *                             another error occurs.
     * @since JCR 2.0
     */
    public double getScore(String selectorName);

// -------------------------- OTHER METHODS --------------------------
    public Row getDelegate();

    public static class Wrapper {
        public static JcrRow wrap(Row delegate, JcrSession session) {
            return WrapperAccessor.JcrRowWrapper.wrap(delegate, session);
        }
    }
}