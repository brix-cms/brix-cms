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

import javax.jcr.NamespaceRegistry;

import brix.jcr.api.JcrNamespaceRegistry;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class NamespaceRegistryWrapper extends AbstractWrapper implements
		JcrNamespaceRegistry {

	private NamespaceRegistryWrapper(NamespaceRegistry delegate,
			JcrSession session) {
		super(delegate, session);
	}

	@Override
	public NamespaceRegistry getDelegate() {
		return (NamespaceRegistry) super.getDelegate();
	}

	public static JcrNamespaceRegistry wrap(NamespaceRegistry delegate,
			JcrSession session) {
		if (delegate == null) {
			return null;
		} else {
			return new NamespaceRegistryWrapper(delegate, session);
		}
	}

	public String getPrefix(final String uri) {
		return executeCallback(new Callback<String>() {
			public String execute() throws Exception {
				return getDelegate().getPrefix(uri);
			}
		});
	}

	public String[] getPrefixes() {
		return executeCallback(new Callback<String[]>() {
			public String[] execute() throws Exception {
				return getDelegate().getPrefixes();
			}
		});
	}

	public String getURI(final String prefix) {
		return executeCallback(new Callback<String>() {
			public String execute() throws Exception {
				return getDelegate().getURI(prefix);
			}
		});
	}

	public String[] getURIs() {
		return executeCallback(new Callback<String[]>() {
			public String[] execute() throws Exception {
				return getDelegate().getURIs();
			}
		});
	}

	public void registerNamespace(final String prefix, final String uri) {
		executeCallback(new VoidCallback() {
			public void execute() throws Exception {
				getDelegate().registerNamespace(prefix, uri);
			}
		});
	}

	public void unregisterNamespace(final String prefix) {
		executeCallback(new VoidCallback() {
			public void execute() throws Exception {
				getDelegate().unregisterNamespace(prefix);
			}
		});
	}
}
