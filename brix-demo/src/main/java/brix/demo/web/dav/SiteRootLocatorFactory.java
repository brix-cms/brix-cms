package brix.demo.web.dav;

import org.apache.jackrabbit.webdav.AbstractLocatorFactory;

/**
 * WebDav resource locator that takes users directly to the site's root html folder as opposed to
 * the root of the jcr repository. This will not allow editing of any content outside the site's
 * html root via WebDav, but is fine for the the purposes of the demo since it does not come with
 * any plugins that store anything outside the site's html root.
 */
public final class SiteRootLocatorFactory extends AbstractLocatorFactory
{

    /** {@inheritDoc} */
    SiteRootLocatorFactory(SimpleServlet simpleServlet, String pathPrefix)
    {
        super(pathPrefix);
    }

    /** {@inheritDoc} */
    @Override
    protected String getRepositoryPath(String resourcePath, String wspPath)
    {

        if (resourcePath == null)
        {
            return resourcePath;
        }

        if (resourcePath.equals(wspPath) || startsWithWorkspace(resourcePath, wspPath))
        {
            String repositoryPath = SimpleServlet.WORKSPACE_ROOT_PATH +
                    resourcePath.substring(wspPath.length());
            return repositoryPath;
        }
        else
        {
            throw new IllegalArgumentException("Unexpected format of resource path.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractLocatorFactory#getResourcePath(String, String)
     */
    protected String getResourcePath(String repositoryPath, String wspPath)
    {
        if (repositoryPath == null)
        {
            throw new IllegalArgumentException(
                    "Cannot build resource path from 'null' repository path");
        }

        String resourcePath = (startsWithWorkspace(repositoryPath, wspPath))
                ? removeRootPrefix(repositoryPath)
                : wspPath + removeRootPrefix(repositoryPath);
        return resourcePath;
    }

    // XXX stupid and brute force for speed - TODO check that the workspace root is
    // ACTUALLY the prefix? Does this matter?
    private String removeRootPrefix(String repositoryPath)
    {
        return repositoryPath.substring(SimpleServlet.WORKSPACE_ROOT_PATH.length());
    }

    private boolean startsWithWorkspace(String repositoryPath, String wspPath)
    {
        if (wspPath == null)
        {
            return true;
        }
        else
        {
            return repositoryPath.startsWith(wspPath + "/");
        }
    }
}