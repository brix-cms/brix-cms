/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.jcr.wrapper;

import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.wrapper.NodeWrapper;
import org.brixcms.plugin.site.folder.FolderNodePlugin;
import org.brixcms.plugin.site.resource.ResourceNodePlugin;
import org.brixcms.web.util.validators.NodeNameValidator;

import javax.jcr.Node;
import java.util.Calendar;
import java.util.Date;

/**
 * Base wrapper that all other wrappers in Brix must extend. Contains convenience methods for setting/getting node type
 * and common properties.
 * <p/>
 * Every node obtained from {@link JcrSession} in {@link Brix} is guaranteed to extend {@link BrixNode}.
 *
 * @author Matej Knopp
 */
public class BrixNode extends NodeWrapper {
    /**
     * Mixin for brix:node
     */
    public static final String JCR_TYPE_BRIX_NODE = Brix.NS_PREFIX + "node";

    /**
     * Mixin for hidden nodes.
     */
    public static final String JCR_MIXIN_BRIX_HIDDEN = Brix.NS_PREFIX + "hidden";

    /**
     * Property for storing node type
     */
    private static final String JCR_PROP_NODE_TYPE = Brix.NS_PREFIX + "nodeType";

    /**
     * Property for storing last modified date
     */
    private static final String JCR_PROP_LAST_MODIFIED = Brix.NS_PREFIX + "lastModified";

    /**
     * Property for storing id of the last user that has modified this node
     */
    private static final String JCR_PROP_LAST_MODIFIED_BY = Brix.NS_PREFIX + "lastModifiedBy";

    /**
     * Property for storing date when node was first time saved
     */
    private static final String JCR_PROP_CREATED = Brix.NS_PREFIX + "created";

    /**
     * Property for storing user id for user that has created this node
     */
    private static final String JCR_PROP_CREATED_BY = Brix.NS_PREFIX + "createdBy";

    /**
     * Returns the type of given node.
     * <p/>
     * TODO: There are two special cases (resource, type) that create special dependency on the site plugin. The folder
     * and Resource nodes should be moved away from site plugin.
     *
     * @param node
     * @return
     */
    public static String getNodeType(JcrNode node) {
        if (node.hasProperty(JCR_PROP_NODE_TYPE)) {
            return node.getProperty(JCR_PROP_NODE_TYPE).getString();
        }
        if (node.isNodeType("nt:file")) {
            if (node.hasNode("jcr:content")) {
                return ResourceNodePlugin.TYPE;
            }
        } else if (node.isNodeType("nt:folder") || node.getDepth() == 0) {
            // TODO: Move the constant
            return FolderNodePlugin.TYPE;
        }

        return null;
    }

    /**
     * Convenience method that checks if the node name is a valid JCR node name.
     *
     * @param nodeName
     * @return
     */
    public static boolean isValidNodeName(String nodeName) {
        if (nodeName == null) {
            return false;
        } else {
            for (int i = 0; i < nodeName.length(); ++i) {
                if (NodeNameValidator.isForbidden(nodeName.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Wrapper constructor. Wraps the delegate node.
     *
     * @param delegate
     * @param session
     */
    public BrixNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    /**
     * Convenience method for obtaining Brix instance. This is the preferred way to obtain the instance.
     *
     * @return brix instance
     */
    public Brix getBrix() {
        return Brix.get();
    }

    /**
     * Returns the date of node creation.
     *
     * @return
     * @see #touch()
     */
    public Date getCreated() {
        if (hasProperty(JCR_PROP_CREATED)) {
            return getProperty(JCR_PROP_CREATED).getDate().getTime();
        } else {
            return null;
        }
    }

    /**
     * Returns the user id of use that has created this node.
     *
     * @return
     * @see #touch()
     */
    public String getCreatedBy() {
        if (hasProperty(JCR_PROP_CREATED_BY)) {
            return getProperty(JCR_PROP_CREATED_BY).getString();
        } else {
            return "Unknown";
        }
    }

    /**
     * Returns the date of last modification.
     *
     * @return
     * @see #touch()
     */
    public Date getLastModified() {
        if (hasProperty(JCR_PROP_LAST_MODIFIED)) {
            return getProperty(JCR_PROP_LAST_MODIFIED).getDate().getTime();
        } else {
            return null;
        }
    }

    /**
     * Returns the user id of the last user that has modified this node.
     *
     * @return
     * @see #touch()
     */
    public String getLastModifiedBy() {
        if (hasProperty(JCR_PROP_LAST_MODIFIED_BY)) {
            return getProperty(JCR_PROP_LAST_MODIFIED_BY).getString();
        } else {
            return "Unknown";
        }
    }

    /**
     * Returns the type of this node if it has any type assigned.
     *
     * @return node type or <code>null</code> if the node has no type assigned
     */
    public String getNodeType() {
        return getNodeType(this);
    }

    /**
     * Returns the required protocol for this node. If the required protocol is not {@link Protocol#PRESERVE_CURRENT}
     * and current page protocol is different, Brix will redirect to required protocol.
     *
     * @return
     */
    public Protocol getRequiredProtocol() {
        return Protocol.PRESERVE_CURRENT;
    }

    /**
     * Returns the user visible node name. In most cases, this would be the same as node name, but it can be overridden
     * by the wrapper.
     *
     * @return user visible node name
     */
    public String getUserVisibleName() {
        return getName();
    }

    /**
     * Returns the user visible node type. By default returns empty string, can be overridden by the wrapper.
     *
     * @return user visible node type
     */
    public String getUserVisibleType() {
        return "";
    }

    public boolean isFolder() {
        return isNodeType("nt:folder");
    }

    /**
     * Sets the hidden state of this node. Hidden node is usually considered part of parent node and should not be
     * visible to user on it's own.
     *
     * @param hidden
     */
    public void setHidden(boolean hidden) {
        if (isHidden() != hidden) {
            if (hidden) {
                addMixin(JCR_MIXIN_BRIX_HIDDEN);
            } else {
                removeMixin(JCR_MIXIN_BRIX_HIDDEN);
            }
        }
    }

    /**
     * Returns whether this node is hidden.
     *
     * @return
     * @see #setHidden(boolean)
     */
    public boolean isHidden() {
        return isNodeType(JCR_MIXIN_BRIX_HIDDEN);
    }

    /**
     * Sets the node type for this node.
     *
     * @param type
     */
    public void setNodeType(String type) {
        if (!isNodeType(JCR_TYPE_BRIX_NODE)) {
            addMixin(JCR_TYPE_BRIX_NODE);
        }
        setProperty(JCR_PROP_NODE_TYPE, type);
    }

    /**
     * Touches this node. Touch updates the created, createdBy, lastModified, lastModifiedBy properties. In order for
     * these properties to reflect the actual state {@link #touch()} must be invoked every time node is being saved.
     * This happens automatically when {@link Node#save()} is invoked, however, if only session is saved {@link
     * #touch()} must be called explicitly.
     */
    public void touch() {
        if (!isNodeType(JCR_TYPE_BRIX_NODE)) {
            addMixin(JCR_TYPE_BRIX_NODE);
        }
        String user = getSession().getUserID();
        Calendar now = Calendar.getInstance();

        if (!hasProperty(JCR_PROP_CREATED)) {
            setProperty(JCR_PROP_CREATED, now);
        }
        if (!hasProperty(JCR_PROP_CREATED_BY)) {
            setProperty(JCR_PROP_CREATED_BY, user);
        }
        setProperty(JCR_PROP_LAST_MODIFIED, now);
        setProperty(JCR_PROP_LAST_MODIFIED_BY, user);
    }

    public enum Protocol {
        HTTP, HTTPS, PRESERVE_CURRENT
    }
}
