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

import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrVersionHistory extends VersionHistory, JcrNode
{

    public static class Wrapper
    {
        public static JcrVersionHistory wrap(VersionHistory delegate, JcrSession session)
        {
            return WrapperAccessor.JcrVersionHistoryWrapper.wrap(delegate, session);
        }
    };

    public VersionHistory getDelegate();

    public void addVersionLabel(String versionName, String label, boolean moveLabel);

    public JcrVersionIterator getAllVersions();

    public JcrVersion getRootVersion();

    public JcrVersion getVersion(String versionName);

    public Version getVersionByLabel(String label);

    public String[] getVersionLabels();

    public String[] getVersionLabels(Version version);

    public String getVersionableUUID();

    public boolean hasVersionLabel(String label);

    public boolean hasVersionLabel(Version version, String label);

    public void removeVersion(String versionName);

    public void removeVersionLabel(String label);

}