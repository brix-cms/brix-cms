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

/**
 *
 */
package org.brixcms;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;

public class BrixNodeModel implements IModel<BrixNode> {
    private String id;
    private String workspaceName;
    private transient BrixNode node;

    public BrixNodeModel() {
        this((BrixNode) null);
    }

    public BrixNodeModel(BrixNode node) {
        this.node = node;
        if (node != null) {
            this.id = getId(node);
            this.workspaceName = node.getSession().getWorkspace().getName();
        }
    }

    private String getId(JcrNode node) {
        if (node.isNodeType("mix:referenceable")) {
            return node.getIdentifier();
        } else {
            return node.getPath();
        }
    }

    public BrixNodeModel(BrixNodeModel other) {
        if (other == null) {
            throw new IllegalArgumentException("Argument 'other' may not be null.");
        }
        this.id = other.id;
        this.workspaceName = other.workspaceName;
        this.node = other.node;
    }

    public BrixNodeModel(String id, String workspaceName) {
        this.id = id;
        this.node = null;
        this.workspaceName = workspaceName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BrixNodeModel == false) {
            return false;
        }

        BrixNodeModel that = (BrixNodeModel) obj;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.workspaceName, that.workspaceName);
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0) + 33 *
                (workspaceName != null ? workspaceName.hashCode() : 0);
    }



    public void detach() {
        node = null;
    }

    public BrixNode getObject() {
        if (node == null) {
            node = loadNode(id);
        }
        return node;
    }

    public void setObject(BrixNode node) {
        if (node == null) {
            id = null;
            workspaceName = null;
            this.node = null;
        } else {
            this.node = node;
            this.id = getId(node);
            this.workspaceName = node.getSession().getWorkspace().getName();
        }
    }

    private BrixNode loadNode(String id) {
        if (id != null) {
            JcrSession session = Brix.get().getCurrentSession(workspaceName);
            if (id.startsWith("/")) {
                return (BrixNode) session.getItem(id);
            } else {
                return (BrixNode) session.getNodeByIdentifier(id);
            }
        } else {
            return null;
        }
    }
}