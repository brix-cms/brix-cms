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

package brix.jcr.api;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Query;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrQuery extends Query
{

    public static class Wrapper
    {
        public static JcrQuery wrap(Query delegate, JcrSession session)
        {
            return WrapperAccessor.JcrQueryWrapper.wrap(delegate, session);
        }
    };

    public Query getDelegate();

    public JcrQueryResult execute();

    public String getLanguage();

    public String getStatement();

    public String getStoredQueryPath();

    public JcrNode storeAsNode(String absPath);
    
    /**
     * Binds the given <code>value</code> to the variable named
     * <code>varName</code>.
     *
     * @param varName name of variable in query
     * @param value   value to bind
     * @throws IllegalArgumentException      if <code>varName</code> is not a valid
     *                                       variable in this query.
     * @throws javax.jcr.RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public void bindValue(String varName, Value value);

    /**
     * Returns the names of the bind variables in this query. If this query does
     * not contains any bind variables then an empty array is returned.
     *
     * @return the names of the bind variables in this query.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public String[] getBindVariableNames();

}