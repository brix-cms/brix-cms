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

import javax.jcr.NodeIterator;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrVersion;
import brix.jcr.api.JcrVersionHistory;
import brix.jcr.api.JcrVersionIterator;

/**
 * 
 * @author Matej Knopp
 * @author igor.vaynberg
 */
class VersionHistoryWrapper extends NodeWrapper implements JcrVersionHistory
{

    protected VersionHistoryWrapper(VersionHistory delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrVersionHistory wrap(VersionHistory delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new VersionHistoryWrapper(delegate, session);
        }
    }

    @Override
    public VersionHistory getDelegate()
    {
        return (VersionHistory)super.getDelegate();
    }

    public void addVersionLabel(final String versionName, final String label,
            final boolean moveLabel)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().addVersionLabel(versionName, label, moveLabel);
            }
        });
    }

    public JcrVersionIterator getAllVersions()
    {
        return executeCallback(new Callback<JcrVersionIterator>()
        {
            public JcrVersionIterator execute() throws Exception
            {
                return JcrVersionIterator.Wrapper.wrap(getDelegate().getAllVersions(),
                        getJcrSession());
            }
        });
    }

    public JcrVersion getRootVersion()
    {
        return executeCallback(new Callback<JcrVersion>()
        {
            public JcrVersion execute() throws Exception
            {
                return JcrVersion.Wrapper.wrap(getDelegate().getRootVersion(), getJcrSession());
            }
        });
    }

    public JcrVersion getVersion(final String versionName)
    {
        return executeCallback(new Callback<JcrVersion>()
        {
            public JcrVersion execute() throws Exception
            {
                return JcrVersion.Wrapper.wrap(getDelegate().getVersion(versionName),
                        getJcrSession());
            }
        });
    }

    public Version getVersionByLabel(final String label)
    {
        return executeCallback(new Callback<JcrVersion>()
        {
            public JcrVersion execute() throws Exception
            {
                return JcrVersion.Wrapper.wrap(getDelegate().getVersionByLabel(label),
                        getJcrSession());
            }
        });
    }

    public String[] getVersionLabels()
    {
        return executeCallback(new Callback<String[]>()
        {
            public String[] execute() throws Exception
            {
                return getDelegate().getVersionLabels();
            }
        });
    }

    public String[] getVersionLabels(final Version version)
    {
        return executeCallback(new Callback<String[]>()
        {
            public String[] execute() throws Exception
            {
                return getDelegate().getVersionLabels(unwrap(version));
            }
        });
    }

    /** @deprecated */
    @Deprecated
    public String getVersionableUUID()
    {
        return executeCallback(new Callback<String>()
        {
            public String execute() throws Exception
            {
                return getDelegate().getVersionableUUID();
            }
        });
    }

    public boolean hasVersionLabel(final String label)
    {
        return executeCallback(new Callback<Boolean>()
        {
            public Boolean execute() throws Exception
            {
                return getDelegate().hasVersionLabel(label);
            }
        });
    }

    public boolean hasVersionLabel(final Version version, final String label)
    {
        return executeCallback(new Callback<Boolean>()
        {
            public Boolean execute() throws Exception
            {
                return getDelegate().hasVersionLabel(unwrap(version), label);
            }
        });
    }

    public void removeVersion(final String versionName)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                removeVersion(versionName);
            }
        });
    }

    public void removeVersionLabel(final String label)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                removeVersionLabel(label);
            }
        });
    }

    public NodeIterator getAllFrozenNodes()
    {
        return executeCallback(new Callback<NodeIterator>()
        {

            public NodeIterator execute() throws Exception
            {
                return getDelegate().getAllFrozenNodes();
            }
        });
    }

    public NodeIterator getAllLinearFrozenNodes()
    {
        return executeCallback(new Callback<NodeIterator>()
        {

            public NodeIterator execute() throws Exception
            {
                return getDelegate().getAllLinearFrozenNodes();
            }
        });
    }

    public VersionIterator getAllLinearVersions()
    {
        return executeCallback(new Callback<VersionIterator>()
        {

            public VersionIterator execute() throws Exception
            {
                return getDelegate().getAllLinearVersions();
            }
        });
    }

    public String getVersionableIdentifier()
    {
        return executeCallback(new Callback<String>()
        {

            public String execute() throws Exception
            {
                return getDelegate().getVersionableIdentifier();
            }
        });
    }


}
