package brix.jcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.PropertyType;
import javax.jcr.nodetype.NodeType;

import org.apache.wicket.util.string.Strings;

import brix.jcr.api.JcrNamespaceRegistry;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;
import brix.jcr.api.JcrValueFactory;
import brix.jcr.api.JcrWorkspace;
import brix.jcr.exception.JcrException;

/**
 * 
 * @author Matej Knopp
 */
public class JcrUtil
{
    /**
     * Returns list of parent nodes for given node. The list is sorted ascending by node depth.
     * 
     * @param node
     * @return
     */
    private static List<JcrNode> getParents(JcrNode node, ParentLimiter parentLimiter)
    {
        List<JcrNode> result = new ArrayList<JcrNode>();
        JcrNode p = node.getParent();
        while (p.getDepth() > 0 && (parentLimiter == null || !parentLimiter.isFinalParent(node, p)))
        {
            result.add(0, p);
            p = p.getParent();
        }
        return result;
    }

    /**
     * Ensures all nodes from originalNode parent hierarchy exist as children of targetRootNode.
     * Missing nodes will be created having same primary node type as original node.
     * <p>
     * Example: If originalNode is /foo/bar/baz/node and targetRootNode is /x/y having child
     * /x/y/foo , this method will create nodes x/y/foo/bar and x/y/foo/bar/baz .
     * 
     * @param originalNode
     * @param targetRootNode
     * @return
     */
    private static JcrNode ensureParentExists(JcrNode originalNode, JcrNode targetRootNode,
            ParentLimiter parentLimiter)
    {
        List<JcrNode> originalParents = getParents(originalNode, parentLimiter);
        for (JcrNode node : originalParents)
        {
            String name = node.getName();
            if (targetRootNode.hasNode(name))
            {
                targetRootNode = targetRootNode.getNode(name);
            }
            else
            {
                targetRootNode = targetRootNode.addNode(name, node.getPrimaryNodeType().getName());
            }
        }
        return targetRootNode;
    }

    /**
     * Creates the XML snippet containing namespace definitions.
     * 
     * @param session
     * @return
     */
    private static String createXMLNS(JcrSession session)
    {
        JcrNamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
        String prefixes[] = registry.getPrefixes();
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixes)
        {
            if (prefix != null && prefix.length() > 0)
            {
                if (result.length() > 0)
                {
                    result.append("\n");
                }
                result.append("xmlns:");
                result.append(prefix);
                result.append("=\"");
                result.append(registry.getURI(prefix));
                result.append("\"");
            }
        }
        return result.toString();
    }

    private static CharSequence escapeMarkup(String s)
    {
        return Strings.escapeMarkup(s, false, false);
    }

    /**
     * Creates a node that is child of parentNode having same name (and possibly uuid - depending on
     * the value of uuidBehavior parameter) as originalNode.
     * 
     * @see ImportUUIDBehavior
     * 
     * @param originalNode
     * @param parentNode
     * @param xmlns
     * @param uuidBehavior
     * @param uuidMap
     * @return
     */
    private static JcrNode createNodeWithUUID(JcrNode originalNode, JcrNode parentNode,
            String xmlns, int uuidBehavior, Map<String, String> uuidMap)
    {
        // construct the import xml snippet
        String uuid = originalNode.getUUID();
        String name = originalNode.getName();
        JcrSession session = parentNode.getSession();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<sv:node ");
        xml.append(xmlns);
        xml.append(" sv:name=\"");
        xml.append(escapeMarkup(name));
        xml.append("\">");
        xml.append("<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>");
        xml.append(escapeMarkup(originalNode.getPrimaryNodeType().getName()));
        xml.append("</sv:value></sv:property>");
        xml.append("<sv:property sv:name=\"jcr:mixinTypes\" sv:type=\"Name\">");
        xml.append("<sv:value>mix:referenceable</sv:value></sv:property>");
        xml.append("<sv:property sv:name=\"jcr:uuid\" sv:type=\"Name\"><sv:value>");
        xml.append(uuid);
        xml.append("</sv:value></sv:property></sv:node>");

        InputStream stream = null;
        try
        {
            stream = new ByteArrayInputStream(xml.toString().getBytes("utf-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            // retarded
        }

        if (uuidBehavior != ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW)
        {
            // simpler alternative - if we replace node or throw error on UUID clash
            session.importXML(parentNode.getPath(), stream, uuidBehavior);
            return session.getNodeByUUID(uuid);
        }
        else
        {
            // more complicated alternative - on uuid clash node gets new uuid and all
            // cloned references should use the new one

            boolean exists = false;
            try
            {
                // there doesn't seem to be a way in JCR to check if there is such node in workspace
                // except trying to get it and then catching the exception
                session.getNodeByUUID(uuid);
                exists = true;
            }
            catch (JcrException e)
            {

            }
            session.importXML(parentNode.getPath(), stream, uuidBehavior);
            if (exists == false)
            {
                // if there was no node with such uuid in target workspace
                return session.getNodeByUUID(uuid);
            }
            else
            {
                // otherwise get the latest child with such name

                JcrNodeIterator iterator = parentNode.getNodes(name);
                iterator.skip(iterator.getSize() - 1);
                JcrNode newNode = iterator.nextNode();
                String newUuid = newNode.getUUID();

                // and if it has uuid other than the existing one (should always be the case)
                if (uuid.equals(newUuid) == false)
                {
                    uuidMap.put(uuid, newUuid);
                }
                return newNode;
            }
        }
    }

    /**
     * Method checks if given value is of type reference and references node with UUID that has been
     * remapped (can happen with {@link ImportUUIDBehavior#IMPORT_UUID_CREATE_NEW} being set.
     * 
     * @param value
     * @param uuidMap
     * @param valueFactory
     * @return
     */
    private static JcrValue remapReference(JcrValue value, Map<String, String> uuidMap,
            JcrValueFactory valueFactory)
    {
        if (value.getType() == PropertyType.REFERENCE)
        {
            String uuid = value.getString();
            String newUuid = uuidMap.get(uuid);
            if (newUuid != null)
            {
                JcrValue newValue = valueFactory.createValue(newUuid, PropertyType.REFERENCE);
                return newValue;
            }
        }
        return value;
    }

    /**
     * Creates copy (without setting the properties) of originalNode and it's children.
     * 
     * @param originalNode
     *            node being cloned
     * @param targetParent
     *            parent of the clone
     * @param xmlns
     *            string containing the xmlns attributes of sv:node element
     * @param uuidBehavior
     * @param uuidMap
     *            map that is used to track mapping from old uuid to new one (in case uuidBehavior
     *            is {@link ImportUUIDBehavior#IMPORT_UUID_CREATE_NEW}.
     * @param nodes
     *            list of pair <originalNode, targetNode). used to track added nodes so that the
     *            properties can be set after all nodes are created.
     */
    private static void createNodeAndChildren(JcrNode originalNode, JcrNode targetParent,
            String xmlns, int uuidBehavior, Map<String, String> uuidMap, List<NodePair> nodes)
    {
        // create node
        JcrNode targetNode;
        if (originalNode.isNodeType("mix:referenceable"))
        {
            targetNode = createNodeWithUUID(originalNode, targetParent, xmlns, uuidBehavior,
                uuidMap);
        }
        else
        {
            targetNode = targetParent.addNode(originalNode.getName(), originalNode
                .getPrimaryNodeType().getName());
        }

        // set mixin types
        NodeType[] mixins = originalNode.getMixinNodeTypes();
        for (NodeType type : mixins)
        {
            if (type.getName().equals("mix:referenceable") == false)
            {
                targetNode.addMixin(type.getName());
            }
        }

        // add to nodes list so that we can set properties later
        NodePair pair = new NodePair();
        pair.originalNode = originalNode;
        pair.targetNode = targetNode;
        nodes.add(pair);

        // go over nodes and call the method recursively
        JcrNodeIterator nodeIterator = originalNode.getNodes();
        while (nodeIterator.hasNext())
        {
            createNodeAndChildren(nodeIterator.nextNode(), targetNode, xmlns, uuidBehavior,
                uuidMap, nodes);
        }
    }

    private static class NodePair
    {
        JcrNode originalNode;
        JcrNode targetNode;
    }

    /**
     * Goes through each pair of the node list and copies the properties from originalNode to
     * targetNode
     * 
     * @param nodes
     * @param uuidMap
     */
    private static void assignProperties(List<NodePair> nodes, Map<String, String> uuidMap)
    {
        for (NodePair current : nodes)
        {
            JcrNode originalNode = current.originalNode;
            JcrNode targetNode = current.targetNode;

            JcrValueFactory vf = targetNode.getSession().getValueFactory();
            JcrPropertyIterator propertyIterator = originalNode.getProperties();
            while (propertyIterator.hasNext())
            {
                JcrProperty property = propertyIterator.nextProperty();
                String name = property.getName();
                if (!property.getDefinition().isProtected())
                {
                    if (!property.getDefinition().isMultiple())
                    {
                        JcrValue value = property.getValue();
                        targetNode.setProperty(name, remapReference(value, uuidMap, vf));
                    }
                    else
                    {
                        JcrValue values[] = property.getValues();
                        for (int i = 0; i < values.length; ++i)
                        {
                            values[i] = remapReference(values[i], uuidMap, vf);
                        }
                        targetNode.setProperty(name, values);
                    }
                }
            }

        }
    }

    /**
     * Creates a copy of originalNode (without setting the properties). The node position will be
     * concatenation of targetRootNode path and originalNode path. If the targetRootNode path
     * doesn't contains the appropriate child nodes, they will be created.
     * 
     * @param originalNode
     * @param targetRootNode
     * @param xmlns
     * @param uuidBehavior
     * @param uuidMap
     * @param nodes
     * @param parentLimiter
     */
    private static void createNode(JcrNode originalNode, JcrNode targetRootNode, String xmlns,
            int uuidBehavior, Map<String, String> uuidMap, List<NodePair> nodes,
            ParentLimiter parentLimiter)
    {
        JcrNode targetParent = ensureParentExists(originalNode, targetRootNode, parentLimiter);

        createNodeAndChildren(originalNode, targetParent, xmlns, uuidBehavior, uuidMap, nodes);
    }

    /**
     * Filters out nodes that are redundantly in the list because their parent nodes are also in the
     * list.
     * 
     * @param nodes
     * @return
     */
    private static List<JcrNode> filterRedundantNodes(List<JcrNode> nodes)
    {
        List<JcrNode> result = new ArrayList<JcrNode>(nodes);

        for (JcrNode n : nodes)
        {
            String pathCurrent = n.getPath();
            for (JcrNode n2 : result)
            {
                String pathExisting = n2.getPath();
                if (pathCurrent.startsWith(pathExisting) && n != n2)
                {
                    result.remove(n);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Clones the given list of nodes. The clones will be located relative to targetRootNode.
     * 
     * @see ImportUUIDBehavior
     * 
     * @param nodes
     *            list of nodes to clone
     * @param targetRootNode
     *            parent for clones
     * @param uuidBehavior
     *            determines behavior on UUID clashes
     */
    public static void cloneNodes(List<JcrNode> nodes, JcrNode targetRootNode, int uuidBehavior)
    {
        cloneNodes(nodes, targetRootNode, uuidBehavior, null);
    }

    /**
     * Interface that allows to limit copy of parent hierarchy for cloned nodes.
     * 
     * @author Matej Knopp
     */
    public static interface ParentLimiter
    {
        public boolean isFinalParent(JcrNode node, JcrNode parent);
    };

    /**
     * Clones the given list of nodes. The clones will be located relative to targetRootNode.
     * 
     * @see ImportUUIDBehavior
     * 
     * @param nodes
     *            list of nodes to clone
     * @param targetRootNode
     *            parent for clones
     * @param uuidBehavior
     *            determines behavior on UUID clashes
     * @param parentLimiter
     *            (non mandatory) allows to skip certain nodes when creating parent hierarchy for
     *            cloned nodes
     */
    public static void cloneNodes(List<JcrNode> nodes, JcrNode targetRootNode, int uuidBehavior,
            ParentLimiter parentLimiter)
    {
        String xmlns = createXMLNS(targetRootNode.getSession());
        Map<String, String> uuidMap = new HashMap<String, String>();

        nodes = filterRedundantNodes(nodes);

        List<NodePair> processedNodes = new ArrayList<NodePair>();

        for (JcrNode node : nodes)
        {
            createNode(node, targetRootNode, xmlns, uuidBehavior, uuidMap, processedNodes,
                parentLimiter);
        }

        assignProperties(processedNodes, uuidMap);
    }

    /**
     * Checks if the node referenced by the value is either a child node of a node from paths or the
     * node exists in targetWorkspace (if targetWorkspace is not null).
     * 
     * @param value
     * @param node
     * @param paths
     * @param targetWorkspace
     * @param result
     */
    private static void checkReferenceValue(JcrValue value, JcrNode node, List<String> paths,
            JcrWorkspace targetWorkspace, Map<JcrNode, List<JcrNode>> result)
    {
        // get the referenced node and it's path
        JcrNode target = node.getSession().getNodeByUUID(value.getString());
        String path = target.getPath();

        // check if the node is child of node form paths
        boolean found = false;
        for (String p : paths)
        {
            if (path.startsWith(p))
            {
                found = true;
                break;
            }
        }

        // in case it is not check if node with same uuid exists in target workspace
        if (found == false && targetWorkspace != null)
        {
            try
            {
                targetWorkspace.getSession().getNodeByUUID(value.getString());
                found = true;
            }
            catch (JcrException ignore)
            {

            }

        }

        // if the node wasn't found add it to result
        if (found == false)
        {
            List<JcrNode> list = result.get(node);
            if (list == null)
            {
                list = new ArrayList<JcrNode>();
                result.put(node, list);
            }
            if (!list.contains(target))
            {
                list.add(target);
            }
        }
    }

    /**
     * Checks for the dependencies of the given node and it's children
     * 
     * @param node
     * @param paths
     * @param targetWorkspace
     * @param result
     */
    private static void checkDependencies(JcrNode node, List<String> paths,
            JcrWorkspace targetWorkspace, Map<JcrNode, List<JcrNode>> result)
    {
        // go through all properties
        JcrPropertyIterator iterator = node.getProperties();
        while (iterator.hasNext())
        {
            JcrProperty property = iterator.nextProperty();

            // if it is a reference property
            if (property.getType() == PropertyType.REFERENCE)
            {
                // if the property has multiple values
                if (property.getDefinition().isMultiple())
                {
                    JcrValue values[] = property.getValues();
                    for (JcrValue value : values)
                    {
                        checkReferenceValue(value, node, paths, targetWorkspace, result);
                    }
                }
                else
                {
                    JcrValue value = property.getValue();
                    checkReferenceValue(value, node, paths, targetWorkspace, result);
                }
            }
        }

        // go through children and do a recursive check for dependencies
        JcrNodeIterator nodes = node.getNodes();
        while (nodes.hasNext())
        {
            JcrNode child = nodes.nextNode();
            checkDependencies(child, paths, targetWorkspace, result);
        }
    }

    /**
     * Scans the given list of nodes and their children for references that target nodes outside
     * subtrees of the nodes in the list. Alternatively, if the referenced node is not part of any
     * subtree and targetWorkspace is not null, the targetWorkspace is checked for node with same
     * uuid as the referenced node.
     * 
     * @param nodes
     * @param targetWorkspace
     * @return Map of Node->List of Referenced Nodes
     */
    public static Map<JcrNode, List<JcrNode>> getUnsatisfiedDependencies(List<JcrNode> nodes,
            JcrWorkspace targetWorkspace)
    {
        List<String> paths = new ArrayList<String>();
        for (JcrNode node : nodes)
        {
            paths.add(node.getPath());
        }
        Map<JcrNode, List<JcrNode>> result = new HashMap<JcrNode, List<JcrNode>>();

        for (JcrNode node : nodes)
        {
            checkDependencies(node, paths, targetWorkspace, result);
        }

        return result;
    }
}
