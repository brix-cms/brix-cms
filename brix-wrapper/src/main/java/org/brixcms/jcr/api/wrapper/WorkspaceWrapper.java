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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrNamespaceRegistry;
import org.brixcms.jcr.api.JcrQueryManager;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrWorkspace;
import org.xml.sax.ContentHandler;

import javax.jcr.Workspace;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.InputStream;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
class WorkspaceWrapper extends AbstractWrapper implements JcrWorkspace {
    public static JcrWorkspace wrap(Workspace delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new WorkspaceWrapper(delegate, session);
        }
    }

    protected WorkspaceWrapper(Workspace delegate, JcrSession session) {
        super(delegate, session);
    }


    @Override
    public Workspace getDelegate() {
        return (Workspace) super.getDelegate();
    }


    public JcrSession getSession() {
        return getJcrSession();
    }

    public String getName() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getName();
            }
        });
    }

    public void copy(final String srcAbsPath, final String destAbsPath) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().copy(srcAbsPath, destAbsPath);
            }
        });
    }

    public void copy(final String srcWorkspace, final String srcAbsPath, final String destAbsPath) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().copy(srcWorkspace, srcAbsPath, destAbsPath);
            }
        });
    }

    public void clone(final String srcWorkspace, final String srcAbsPath, final String destAbsPath,
                      final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().clone(srcWorkspace, srcAbsPath, destAbsPath, removeExisting);
            }
        });
    }

    public void move(final String srcAbsPath, final String destAbsPath) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().move(srcAbsPath, destAbsPath);
            }
        });
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void restore(final Version[] versions, final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().restore(versions, removeExisting);
            }
        });
    }

    public LockManager getLockManager() {
        return executeCallback(new Callback<LockManager>() {
            public LockManager execute() throws Exception {
                return getDelegate().getLockManager();
            }
        });
    }

    public JcrQueryManager getQueryManager() {
        return executeCallback(new Callback<JcrQueryManager>() {
            public JcrQueryManager execute() throws Exception {
                return JcrQueryManager.Wrapper.wrap(getDelegate().getQueryManager(),
                        getJcrSession());
            }
        });
    }

    public JcrNamespaceRegistry getNamespaceRegistry() {
        return executeCallback(new Callback<JcrNamespaceRegistry>() {
            public JcrNamespaceRegistry execute() throws Exception {
                return WrapperAccessor.JcrNamespaceRegistryWrapper.wrap(getDelegate()
                        .getNamespaceRegistry(), getJcrSession());
            }
        });
    }

    public NodeTypeManager getNodeTypeManager() {
        return executeCallback(new Callback<NodeTypeManager>() {
            public NodeTypeManager execute() throws Exception {
                return getDelegate().getNodeTypeManager();
            }
        });
    }

    public ObservationManager getObservationManager() {
        return executeCallback(new Callback<ObservationManager>() {
            public ObservationManager execute() throws Exception {
                return getDelegate().getObservationManager();
            }
        });
    }

    public VersionManager getVersionManager() {
        return executeCallback(new Callback<VersionManager>() {
            public VersionManager execute() throws Exception {
                return getDelegate().getVersionManager();
            }
        });
    }

    public String[] getAccessibleWorkspaceNames() {
        return executeCallback(new Callback<String[]>() {
            public String[] execute() throws Exception {
                return getDelegate().getAccessibleWorkspaceNames();
            }
        });
    }

    public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior) {
        return executeCallback(new Callback<ContentHandler>() {
            public ContentHandler execute() throws Exception {
                return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
            }
        });
    }

    public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().importXML(parentAbsPath, in, uuidBehavior);
            }
        });
    }

    public void createWorkspace(final String name) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().createWorkspace(name);
            }
        });
    }

    public void createWorkspace(final String name, final String srcWorkspace) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().createWorkspace(name, srcWorkspace);
            }
        });
    }

    public void deleteWorkspace(final String name) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().deleteWorkspace(name);
            }
        });
    }
}
