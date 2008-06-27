package brix.config;

import brix.Brix;
import brix.Path;

/**
 * Used to translate between the web's URI space and jcr's node space
 * 
 * @author ivaynberg
 * 
 */
public interface UriMapper
{
    /**
     * Translates a uri path into a node path. This method is used to resolve a jcr node from a url.
     * 
     * @param uriPath
     *            uri path
     * @param brix
     *            brix instance
     * @return absolute path of node this uri maps to, or null if none
     */
    public Path getNodePathForUriPath(Path uriPath, Brix brix);

    /**
     * Translates a node path into uri path. This method is used to generate urls that map to a
     * specific node.
     * 
     * @param nodePath
     *            node path
     * @param brix
     *            brix instance
     * @return uri path that represents node path
     */
    public Path getUriPathForNode(Path nodePath, Brix brix);
}
