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
