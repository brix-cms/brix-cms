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

import javax.jcr.query.RowIterator;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrRowIterator extends RowIterator
{

    public static class Wrapper
    {
        public static JcrRowIterator wrap(RowIterator delegate, JcrSession session)
        {
            return WrapperAccessor.JcrRowIteratorWrapper.wrap(delegate, session);
        };
    };

    public RowIterator getDelegate();

    public JcrRow nextRow();

}