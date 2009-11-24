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

package brix.jcr.api.wrapper;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;

import brix.jcr.api.JcrRow;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;

/**
 * 
 * @author Matej Knopp
 */
class RowWrapper extends AbstractWrapper implements JcrRow
{

    protected RowWrapper(Row delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrRow wrap(Row delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new RowWrapper(delegate, session);
        }
    }

    @Override
    public Row getDelegate()
    {
        return (Row)super.getDelegate();
    }

    public JcrValue getValue(final String propertyName) throws ItemNotFoundException,
            RepositoryException
    {
        return executeCallback(new Callback<JcrValue>()
        {
            public JcrValue execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValue(propertyName), getJcrSession());
            }
        });
    }

    public Value[] getValues() throws RepositoryException
    {
        return executeCallback(new Callback<JcrValue[]>()
        {
            public JcrValue[] execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValues(), getJcrSession());
            }
        });
    }

}
