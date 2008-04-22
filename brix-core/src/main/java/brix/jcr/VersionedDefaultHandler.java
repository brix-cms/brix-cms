package brix.jcr;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;

public class VersionedDefaultHandler extends DefaultHandler
{

    public VersionedDefaultHandler()
    {
    }

    public VersionedDefaultHandler(IOManager ioManager)
    {
        super(ioManager);
    }

    public VersionedDefaultHandler(IOManager ioManager, String collectionNodetype,
            String defaultNodetype, String contentNodetype)
    {
        super(ioManager, collectionNodetype, defaultNodetype, contentNodetype);
    }

    private Node getNode(ImportContext context, boolean isCollection) throws RepositoryException
    {
        Node parentNode = (Node)context.getImportRoot();
        String name = context.getSystemId();
        if (parentNode.hasNode(name))
        {
            parentNode = parentNode.getNode(name);
        }
        else
        {
            String ntName = (isCollection) ? getCollectionNodeType() : getNodeType();
            parentNode = parentNode.addNode(name, ntName);
        }
        return parentNode;
    }

    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException
    {

        if (!canImport(context, isCollection))
        {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }

        try
        {

            Node node = getNode(context, isCollection);

            boolean needToCheckIn = false;
            if (node.isNodeType("mix:versionable") && node.isCheckedOut() == false)
            {
                node.checkout();
                needToCheckIn = true;
            }

            boolean result = super.importContent(context, isCollection);

            if (needToCheckIn)
            {
                node.save();
                node.checkin();
            }

            return result;

        }
        catch (RepositoryException e)
        {
            throw new IOException(e.getMessage());
        }


    }
}
