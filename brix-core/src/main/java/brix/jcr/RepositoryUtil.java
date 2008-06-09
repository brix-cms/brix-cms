package brix.jcr;

import javax.jcr.Workspace;

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.util.StringInputStream;

/**
 * 
 * @author igor.vaynberg
 * 
 */
public class RepositoryUtil
{
    private static final Logger logger = LoggerFactory.getLogger(Brix.class);

    public static void registerMixinType(Workspace workspace, String typeName,
            boolean referenceable, boolean orderable)
    {
        try
        {
            JackrabbitNodeTypeManager manager = (JackrabbitNodeTypeManager)workspace
                .getNodeTypeManager();

            if (manager.hasNodeType(typeName) == false)
            {
                logger.info("Registering node type: {} in workspace {}", typeName, workspace
                    .getName());

                String type = "[" + typeName + "] > nt:unstructured ";

                if (referenceable)
                    type += ", mix:referenceable ";

                if (orderable)
                    type += "orderable ";

                type += " mixin";

                manager.registerNodeTypes(new StringInputStream(type),
                    JackrabbitNodeTypeManager.TEXT_X_JCR_CND);
            }
            else
            {
                logger.info("Type: {} already registered in workspace {}", typeName, workspace
                    .getName());
            }
        }
        catch (Exception e)
        {
            // TODO should use a well know exception subclass
            throw new RuntimeException("Could not register type: " + typeName, e);
        }
    }

}
