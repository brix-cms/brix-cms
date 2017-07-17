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

package org.brixcms.jcr;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.brixcms.Brix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.StringReader;

/**
 * @author igor.vaynberg
 */
public class RepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(Brix.class);

    /**
     * register a brix basic node as a unstructured node type, reason is: accoring to JCR spec 2,
     * a mixin may only have other mixins as supertypes
     *
     * @param workspace
     */
    public static void registerBrixUnstructuredMixin(Workspace workspace) {
        String cnd = "[brix:unstructured] mixin" +
                "  - * (undefined) multiple " +
                "  - * (undefined) " +
                "  + * (nt:base) sns version ";

        try {
            NodeTypeManager manager = workspace.getNodeTypeManager();

            if (manager.hasNodeType("brix:unstructured") == false) {
                CndImporter.registerNodeTypes(new StringReader(cnd), workspace.getSession());
            }

        } catch (Exception e) {
            throw new RuntimeException("JCR error - could not create the brix:unstructured mixin", e);
        }


    }

    public static void registerNodeType(Workspace workspace, String typeName,
                                        boolean referenceable, boolean orderable, boolean mixin) {
        try {
            NodeTypeManager manager = workspace.getNodeTypeManager();

            if (manager.hasNodeType(typeName) == false) {
                logger.info("Registering node type: {} in workspace {}", typeName, workspace
                        .getName());

                String type;
                //todo: find a version that satisfies jackrabbit and modeshape as well
                if(manager.getClass().toString().contains("jackrabbit")) {
                    type  = "[" + typeName + "] > nt:unstructured ";
                } else {
                    type  = "[" + typeName + "] > brix:unstructured ";
                }


                if (referenceable) {
                    type += ", mix:referenceable ";
                }

                if (orderable) {
                    type += "orderable ";
                }

                if (mixin) {
                    type += " mixin";
                }

                CndImporter.registerNodeTypes(new StringReader(type), workspace.getSession());
            } else {
                logger.info("Type: {} already registered in workspace {}", typeName, workspace
                        .getName());
            }
        }
        catch (Exception e) {
            // TODO should use a well know exception subclass
            throw new RuntimeException("Could not register type: " + typeName, e);
        }
    }
}
