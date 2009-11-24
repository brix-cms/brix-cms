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

import java.util.Calendar;

import javax.jcr.version.Version;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrVersion extends Version, JcrNode
{

    public static class Wrapper
    {
        public static JcrVersion wrap(Version delegate, JcrSession session)
        {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }

        public static JcrVersion[] wrap(Version delegate[], JcrSession session)
        {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }
    };

    public Version getDelegate();

    public JcrVersionHistory getContainingHistory();

    public Calendar getCreated();

    public JcrVersion[] getPredecessors();

    public JcrVersion[] getSuccessors();

}