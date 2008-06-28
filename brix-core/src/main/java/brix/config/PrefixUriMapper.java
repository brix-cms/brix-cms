package brix.config;

import brix.Brix;
import brix.Path;

/**
 * Uri mapper that mounts cms urls on a certai prefix. Eg
 * <code>new PrefixUriMapper(new Path("/docs/cms"))</code> will mount all cms urls under the
 * <code>/docs/cms/*</code> url space.
 * 
 * @author ivaynberg
 * 
 */
public class PrefixUriMapper implements UriMapper
{
    private final Path prefix;

    /**
     * Constructor
     * 
     * @param prefix
     *            absolute path to mount the cms uri space on
     */
    public PrefixUriMapper(Path prefix)
    {
        if (!prefix.isAbsolute())
        {
            throw new IllegalArgumentException("Prefix must be an absolute path");
        }

        this.prefix = prefix;
    }

    /** {@inheritDoc} */
    public Path getNodePathForUriPath(Path uriPath, Brix brix)
    {
        if (prefix.isAncestorOf(uriPath))
        {
            // strip prefix from path
            return uriPath.toRelative(prefix).toAbsolute();
        }
        else if (prefix.equals(uriPath))
        {
            // path is same as prefix, which equates to root
            return Path.ROOT;
        }
        else
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    public Path getUriPathForNode(Path nodePath, Brix brix)
    {
        Path uriPath = prefix;

        if (!nodePath.isRoot())
        {
            // nodePath is not root, we have to append it to prefix
            uriPath = prefix.append(nodePath.toRelative(Path.ROOT));
        }

        return uriPath;

    }

}
