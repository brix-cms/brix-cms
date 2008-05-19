package brix.jcr.wrapper;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.api.wrapper.NodeWrapper;
import brix.plugin.site.node.folder.FolderNodePlugin;
import brix.plugin.site.node.resource.ResourceNodePlugin;
import brix.web.util.validators.NodeNameValidator;

public class BrixNode extends NodeWrapper
{

    public static final String JCR_TYPE_BRIX_NODE = Brix.NS_PREFIX + "node";

    private static final String JCR_PROP_NODE_TYPE = Brix.NS_PREFIX + "nodeType";

    private static final String JCR_PROP_LAST_MODIFIED = Brix.NS_PREFIX + "lastModified";

    private static final String JCR_PROP_LAST_MODIFIED_BY = Brix.NS_PREFIX + "lastModifiedBy";

    private static final String JCR_PROP_CREATED = Brix.NS_PREFIX + "created";

    private static final String JCR_PROP_CREATED_BY = Brix.NS_PREFIX + "createdBy";

    public static final String JCR_MIXIN_BRIX_HIDDEN = Brix.NS_PREFIX + "hidden";

    public BrixNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public String getNodeType()
    {
        return getNodeType(this);
    }

    public void setNodeType(String type)
    {
        if (!isNodeType(JCR_TYPE_BRIX_NODE))
        {
            addMixin(JCR_TYPE_BRIX_NODE);
        }
        setProperty(JCR_PROP_NODE_TYPE, type);
        addMixin(type);
    }

    public String getCreatedBy()
    {
        if (hasProperty(JCR_PROP_CREATED_BY))
        {
            return getProperty(JCR_PROP_CREATED_BY).getString();
        }
        else
        {
            return "Unknown";
        }
    }

    public Date getCreated()
    {
        if (hasProperty(JCR_PROP_CREATED))
        {
            return getProperty(JCR_PROP_CREATED).getDate().getTime();
        }
        else
        {
            return null;
        }
    }

    public String getLastModifiedBy()
    {
        if (hasProperty(JCR_PROP_LAST_MODIFIED_BY))
        {
            return getProperty(JCR_PROP_LAST_MODIFIED_BY).getString();
        }
        else
        {
            return "Unknown";
        }
    };

    public Date getLastModified()
    {
        if (hasProperty(JCR_PROP_LAST_MODIFIED))
        {
            return getProperty(JCR_PROP_LAST_MODIFIED).getDate().getTime();
        }
        else
        {
            return null;
        }
    }

    public boolean isHidden()
    {
        return isNodeType(JCR_MIXIN_BRIX_HIDDEN);
    }

    public void setHidden(boolean hidden)
    {
        if (isHidden() != hidden)
        {
            if (hidden)
            {
                addMixin(JCR_MIXIN_BRIX_HIDDEN);
            }
            else
            {
                removeMixin(JCR_MIXIN_BRIX_HIDDEN);
            }
        }
    }

    public boolean isFolder()
    {
        return isNodeType("nt:folder");
    }

    public void touch()
    {
        if (!isNodeType(JCR_TYPE_BRIX_NODE))
        {
            addMixin(JCR_TYPE_BRIX_NODE);
        }
        String user = getSession().getUserID();
        Calendar now = Calendar.getInstance();

        if (!hasProperty(JCR_PROP_CREATED))
        {
            setProperty(JCR_PROP_CREATED, now);
        }
        if (!hasProperty(JCR_PROP_CREATED_BY))
        {
            setProperty(JCR_PROP_CREATED_BY, user);
        }
        setProperty(JCR_PROP_LAST_MODIFIED, now);
        setProperty(JCR_PROP_LAST_MODIFIED_BY, user);
    }

    public static String getNodeType(JcrNode node)
    {
        if (node.hasProperty(JCR_PROP_NODE_TYPE))
        {
            String type = node.getProperty(JCR_PROP_NODE_TYPE).getString();
            if (!node.isNodeType(type))
            {
                final String msg = "Node has " + JCR_PROP_NODE_TYPE +
                        " set to '{}' but has no such mixin type assigned";
                logger.warn(msg, type);
            }
            return type;
        }
        if (node.isNodeType("nt:file"))
        {
            if (node.hasNode("jcr:content"))
            {
                return ResourceNodePlugin.TYPE;
            }

        }
        else if (node.isNodeType("nt:folder") || node.getDepth() == 0)
        {
            // TODO: Move the constant
            return FolderNodePlugin.TYPE;
        }

        return null;

    }

    public static boolean canHandle(JcrNode node)
    {
        return true;
    }

    public static boolean isValidNodeName(String nodeName)
    {
        if (nodeName == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < nodeName.length(); ++i)
            {
                if (NodeNameValidator.isForbidden(nodeName.charAt(i)))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    public enum Protocol {
    	HTTP,
    	HTTPS,
    	PRESERVE_CURRENT
    }
    
    public Protocol getRequiredProtocol() 
    {
    	return Protocol.PRESERVE_CURRENT;
    }    

    private static final Logger logger = LoggerFactory.getLogger(BrixNode.class);
}
