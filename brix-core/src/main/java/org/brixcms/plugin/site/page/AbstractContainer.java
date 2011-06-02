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

package org.brixcms.plugin.site.page;

import org.brixcms.Brix;
import org.brixcms.exception.NodeNotFoundException;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrPropertyIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.markup.tag.Item;
import org.brixcms.markup.variable.VariableKeyProvider;
import org.brixcms.markup.variable.VariableTransformer;
import org.brixcms.markup.variable.VariableValueProvider;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.tile.TileContainerFacet;
import org.brixcms.plugin.site.page.tile.TileTag;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractContainer extends BrixFileNode
        implements
        VariableValueProvider,
        VariableKeyProvider {
    /**
     * Name of markup attribute used to identify tile id inside brix:tile tag
     */
    public static final String MARKUP_TILE_ID = "id";

    private static final String VARIABLES_NODE_NAME = Brix.NS_PREFIX + "variables";

    private final TileContainerFacet tileManager;

    public AbstractContainer(Node delegate, JcrSession session) {
        super(delegate, session);
        tileManager = new TileContainerFacet(this);
    }



    /**
     * Returns collection of possible variable keys for this node.
     */
    public Collection<String> getVariableKeys() {
        Set<String> keys = new HashSet<String>();
        PageMarkupSource source = new PageMarkupSource(this);
        VariableTransformer transfomer = new VariableTransformer(source, this);
        Item i = transfomer.nextMarkupItem();
        while (i != null) {
            if (i instanceof VariableKeyProvider) {
                Collection<String> k = ((VariableKeyProvider) i).getVariableKeys();
                if (k != null) {
                    keys.addAll(k);
                }
            }
            i = transfomer.nextMarkupItem();
        }

        keys.addAll(SitePlugin.get().getGlobalVariableKeys(getSession()));

        return keys;
    }

    public String getVariableValue(String key) {
        return getVariableValue(key, true);
    }

    public AbstractSitePagePlugin getNodePlugin() {
        return (AbstractSitePagePlugin) SitePlugin.get().getNodePluginForNode(this);
    }

    @Override
    public Protocol getRequiredProtocol() {
        // requiring SSL takes precedence
        if (requiresSSL()) {
            return Protocol.HTTPS;
        } else if (requiresNonSSL()) {
            return Protocol.HTTP;
        } else {
            return Protocol.PRESERVE_CURRENT;
        }
    }

    public boolean requiresSSL() {
        Boolean requiresSSL = isRequiresSSL();
        return (requiresSSL != null && requiresSSL.booleanValue()) || tileManager.anyTileRequiresSSL() || (getTemplate() != null && getTemplate().requiresSSL());
    }

    public Boolean isRequiresSSL() {
        if (hasProperty(Properties.REQUIRES_SSL)) {
            return getProperty(Properties.REQUIRES_SSL).getBoolean();
        } else {
            return null;
        }
    }

    public boolean requiresNonSSL() {
        // ignore tiles assuming that tiles which don't require SSL are equivalent to Protocol.PRESERVE_CURRENT
        // in future could add method: Protocol getRequiredProtocol() to Tile
        Boolean requiresSSL = isRequiresSSL();
        return (requiresSSL != null && !requiresSSL.booleanValue()) || (getTemplate() != null && getTemplate().requiresNonSSL());
    }

    public List<String> getSavedVariableKeys() {
        if (hasNode(VARIABLES_NODE_NAME)) {
            JcrNode node = getNode(VARIABLES_NODE_NAME);
            List<String> result = new ArrayList<String>();
            JcrPropertyIterator i = node.getProperties();
            while (i.hasNext()) {
                String name = i.nextProperty().getName();
                // filter out jcr: properties (or other possible brix properties)
                if (!name.contains(":")) {
                    result.add(name);
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public String getTemplatePath() {
        BrixNode template = getTemplate();
        return template != null ? SitePlugin.get().pathForNode(template) : null;
    }

    public TemplateNode getTemplate() {
        if (hasProperty(Properties.TEMPLATE)) {
            return (TemplateNode) getProperty(Properties.TEMPLATE).getNode();
        } else {
            return null;
        }
    }

    public Collection<String> getTileIDs() {
        Set<String> keys = new HashSet<String>();
        PageMarkupSource source = new PageMarkupSource(this);
        Item i = source.nextMarkupItem();
        while (i != null) {
            if (i instanceof TileTag) {
                keys.add(((TileTag) i).getTileName());
            }
            i = source.nextMarkupItem();
        }

        keys.addAll(SitePlugin.get().getGlobalTileIDs(getSession()));

        return keys;
    }

    public BrixNode getTileNode(String id) {
        BrixNode node = null;
        AbstractContainer container = this;
        while (node == null && container != null) {
            node = container.tiles().getTile(id);
            container = container.getTemplate();
        }
        if (node == null) {
            container = SitePlugin.get().getGlobalContainer(getSession());
            if (container != null) {
                node = container.tiles().getTile(id);
            }
        }
        return node;
    }

    public String getTitle() {
        if (hasProperty(Properties.TITLE)) {
            return getProperty(Properties.TITLE).getString();
        } else {
            return null;
        }
    }

    public String getVariableValue(String key, boolean followTemplate) {
        if (hasNode(VARIABLES_NODE_NAME)) {
            JcrNode node = getNode(VARIABLES_NODE_NAME);
            if (node.hasProperty(key)) {
                return node.getProperty(key).getString();
            }
        }
        if (followTemplate) {
            TemplateNode template = getTemplate();
            if (template != null) {
                return template.getVariableValue(key);
            } else {
                return SitePlugin.get().getGlobalVariableValue(getSession(), key);
            }
        }
        return null;
    }

    public void setRequiresSSL(Boolean value) {
        if (value == null) {
            setProperty(Properties.REQUIRES_SSL, (String) null);
        } else if (value == false) {
            setProperty(Properties.REQUIRES_SSL, false);
        } else {
            setProperty(Properties.REQUIRES_SSL, true);
        }
    }

    public void setTemplatePath(String path) {
        if (path == null) {
            setTemplate(null);
        } else {
            BrixNode node = (BrixNode) SitePlugin.get().nodeForPath(this, path);

            if (node == null) {
                throw new NodeNotFoundException("No node found on path '" + path + "'.");
            }

            setTemplate((BrixNode) node);
        }
    }

    public void setTemplate(BrixNode node) {
        setProperty(Properties.TEMPLATE, node);
    }

    public void setTitle(String title) {
        setProperty(Properties.TITLE, title);
    }

    public void setVariableValue(String key, String value) {
        final JcrNode node;
        if (hasNode(VARIABLES_NODE_NAME)) {
            node = getNode(VARIABLES_NODE_NAME);
        } else {
            node = addNode(VARIABLES_NODE_NAME, "nt:unstructured");
        }
        node.setProperty(key, value);
    }

    public TileContainerFacet tiles() {
        return tileManager;
    }

    private static class Properties {
        public static final String TITLE = Brix.NS_PREFIX + "title";
        public static final String TEMPLATE = Brix.NS_PREFIX + "template";
        public static final String REQUIRES_SSL = Brix.NS_PREFIX + "requiresSSL";
    }
}
