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

import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.Version;

import org.xml.sax.ContentHandler;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrWorkspace extends Workspace
{

	public static class Wrapper
	{
		public static JcrWorkspace wrap(Workspace delegate, JcrSession session)
		{
			return WrapperAccessor.JcrWorkspaceWrapper.wrap(delegate, session);
		}
	};

	public Workspace getDelegate();

	public void clone(String srcWorkspace, String srcAbsPath, String destAbsPath, boolean removeExisting);

	public void copy(String srcAbsPath, String destAbsPath);

	public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath);

	public String[] getAccessibleWorkspaceNames();

	public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior);

	public String getName();

	public JcrNamespaceRegistry getNamespaceRegistry();

	public NodeTypeManager getNodeTypeManager();

	public ObservationManager getObservationManager();

	public JcrQueryManager getQueryManager();

	public JcrSession getSession();

	public void importXML(String parentAbsPath, InputStream in, int uuidBehavior);

	public void move(String srcAbsPath, String destAbsPath);

	public void restore(Version[] versions, boolean removeExisting);

}