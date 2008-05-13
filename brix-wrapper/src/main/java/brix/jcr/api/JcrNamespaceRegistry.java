package brix.jcr.api;

import javax.jcr.NamespaceRegistry;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrNamespaceRegistry extends NamespaceRegistry
{
	public String getPrefix(String uri);

	public String[] getPrefixes();

	public String getURI(String prefix);

	public String[] getURIs();

	public void registerNamespace(String prefix, String uri);

	public void unregisterNamespace(String prefix);
}
