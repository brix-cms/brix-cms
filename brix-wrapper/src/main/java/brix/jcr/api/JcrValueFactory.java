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

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.ValueFactory;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrValueFactory extends ValueFactory
{

    public static class Wrapper
    {
        public static JcrValueFactory wrap(ValueFactory delegate, JcrSession session)
        {
            return WrapperAccessor.JcrValueFactoryWrapper.wrap(delegate, session);
        }
    };

    public ValueFactory getDelegate();

    public JcrValue createValue(String value);

    public JcrValue createValue(long value);

    public JcrValue createValue(double value);

    public JcrValue createValue(boolean value);

    public JcrValue createValue(Calendar value);

    public JcrValue createValue(InputStream value);

    public JcrValue createValue(Node value);

    public JcrValue createValue(String value, int type);

}