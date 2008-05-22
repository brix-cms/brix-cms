package brix.workspace;

import java.io.InputStream;

public interface WorkspaceNodeTypeManager
{
	public void registerNodeTypes(String workspace, InputStream in, String contentType, boolean reregisterExisting);
}
