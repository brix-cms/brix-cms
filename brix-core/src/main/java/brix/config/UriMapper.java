package brix.config;

import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.IRequestCodingStrategy;

import brix.Brix;
import brix.Path;
import brix.workspace.Workspace;

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

    /**
     * Resolves JCR workspace to be used for the specified request.
     * 
     * @param requestCycle
     * @return JCR workspace or <code>null</code> if no suitable one is found
     */
    public Workspace getWorkspaceForRequest(WebRequestCycle requestCycle, Brix brix);

    /**
     * Rewrites relative urls found in static markup to be context-relative.
     * 
     * @see IRequestCodingStrategy#rewriteStaticRelativeUrl(String)
     * 
     * @param url
     *            relative url to rewrite
     * @param contextPrefix
     *            prefix that will make url context-relative, eg <code>../../</code>
     * @return rewritten url
     */
    public String rewriteStaticRelativeUrl(String url, String contextPrefix);
}
