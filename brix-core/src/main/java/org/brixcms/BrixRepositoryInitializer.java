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

package org.brixcms;

import org.brixcms.jcr.JcrEventListener;
import org.brixcms.jcr.RepositoryInitializer;
import org.brixcms.jcr.RepositoryUtil;
import org.brixcms.jcr.base.EventUtil;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.folder.FolderNodePlugin;
import org.brixcms.plugin.site.page.tile.TileContainerFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

/**
 * Initializes JCR Repository to be compatible with Brix
 *
 * @author igor.vaynberg
 */
public class BrixRepositoryInitializer implements RepositoryInitializer {
    private static final Logger logger = LoggerFactory.getLogger(BrixRepositoryInitializer.class);

    public BrixRepositoryInitializer() {

    }


    /**
     * {@inheritDoc}
     */
    public void initializeRepository(Brix brix, Session session) throws RepositoryException {
        final Workspace w = session.getWorkspace();
        NamespaceRegistry nr = w.getNamespaceRegistry();

        try {
            logger.info("Registering Brix JCR Namespace: {}", Brix.NS);
            nr.registerNamespace(Brix.NS, "http://brix-cms.googlecode.com");
        }
        catch (Exception ignore) {
            // logger.warn("Error registering brix namespace, may already be registered",
            // ignore);
        }

        EventUtil.registerSaveEventListener(new JcrEventListener());

        RepositoryUtil.registerNodeType(w, BrixNode.JCR_TYPE_BRIX_NODE, true, true, true);

        // the following three have always brix:node mixin too
        RepositoryUtil.registerNodeType(w, FolderNodePlugin.TYPE, false, false, true);

        RepositoryUtil.registerNodeType(w, TileContainerFacet.JCR_TYPE_BRIX_TILE, false, true, false);

        RepositoryUtil.registerNodeType(w, BrixNode.JCR_MIXIN_BRIX_HIDDEN, false, false, true);
    }
}
