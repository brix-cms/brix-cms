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

package brix.workspace.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class ClientWorkspaceManager implements WorkspaceManager
{
	private final RemoteWorkspaceManager delegate;

	public ClientWorkspaceManager(String url)
	{
		this(lookup(url));

	}

	private static RemoteWorkspaceManager lookup(String url)
	{
		try
		{
			return (RemoteWorkspaceManager) Naming.lookup(url);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not connect to remote url: " + url, e);
		}
	}

	public ClientWorkspaceManager(RemoteWorkspaceManager delegate)
	{
		this.delegate = delegate;
	}

	public Workspace createWorkspace()
	{
		try
		{
			return new ClientWorkspace(delegate.createWorkspace());
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	public Workspace getWorkspace(String workspaceId)
	{
		return new ClientWorkspace(workspaceId, delegate);
	}

	public List<Workspace> getWorkspaces()
	{
		try
		{
			return remoteToLocal(delegate.getWorkspaces());
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}

	}

	public boolean workspaceExists(String workspaceId)
	{
		try
		{
			return delegate.workspaceExists(workspaceId);
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	public List<Workspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
	{
		try
		{
			return remoteToLocal(delegate.getWorkspacesFiltered(workspaceAttributes));
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	private static List<Workspace> remoteToLocal(List<RemoteWorkspace> remote)
	{
		ArrayList<Workspace> local = new ArrayList<Workspace>(remote.size());
		for (RemoteWorkspace workspace : remote)
		{
			local.add(new ClientWorkspace(workspace));
		}
		return local;
	}
}
