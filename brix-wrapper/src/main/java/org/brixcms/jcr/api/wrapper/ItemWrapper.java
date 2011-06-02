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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrItem;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.Item;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

/**
 * @author Matej Knopp
 * @author Igor Vaynberg
 */
abstract class ItemWrapper extends AbstractWrapper implements JcrItem {
    public static JcrItem wrap(Item delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else if (delegate instanceof Version) {
            return VersionWrapper.wrap((Version) delegate, session);
        } else if (delegate instanceof VersionHistory) {
            return VersionHistoryWrapper.wrap((VersionHistory) delegate, session);
        } else if (delegate instanceof Node) {
            return NodeWrapper.wrap((Node) delegate, session);
        } else if (delegate instanceof Property) {
            return PropertyWrapper.wrap((Property) delegate, session);
        } else {
            throw new IllegalStateException("Unknown Item subclass.");
        }
    }

    protected ItemWrapper(Item delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public String toString() {
        return getPath();
    }

    public String getPath() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getPath();
            }
        });
    }



    public String getName() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getName();
            }
        });
    }

    public JcrItem getAncestor(final int depth) {
        return executeCallback(new Callback<JcrItem>() {
            public JcrItem execute() throws Exception {
                return JcrItem.Wrapper.wrap(getDelegate().getAncestor(depth), getJcrSession());
            }
        });
    }

    public JcrNode getParent() {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getParent(), getJcrSession());
            }
        });
    }

    public int getDepth() {
        return executeCallback(new Callback<Integer>() {
            public Integer execute() throws Exception {
                return getDelegate().getDepth();
            }
        });
    }

    public JcrSession getSession() {
        return getJcrSession();
    }

    public boolean isNode() {
        return getDelegate().isNode();
    }

    public boolean isNew() {
        return getDelegate().isNew();
    }

    public boolean isModified() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isModified();
            }
        });
    }

    public boolean isSame(final Item otherItem) {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isSame(unwrap(otherItem));
            }
        });
    }

    public void accept(final ItemVisitor visitor) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().accept(visitor);
            }
        });
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void save() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().getSession().save();
            }
        });
    }

    public void refresh(final boolean keepChanges) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().refresh(keepChanges);
            }
        });
    }

    public void remove() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().remove();
            }
        });
    }

    public Item getDelegate() {
        return (Item) super.getDelegate();
    }
}
