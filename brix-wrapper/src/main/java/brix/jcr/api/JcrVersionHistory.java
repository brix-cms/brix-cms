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