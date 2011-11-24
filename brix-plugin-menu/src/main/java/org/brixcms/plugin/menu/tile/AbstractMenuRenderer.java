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

package org.brixcms.plugin.menu.tile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.Menu;
import org.brixcms.plugin.menu.Menu.ChildEntry;
import org.brixcms.plugin.menu.Menu.Entry;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.IGenericComponent;
import org.brixcms.web.nodepage.BrixNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.reference.Reference;
import org.brixcms.web.reference.Reference.Type;

public abstract class AbstractMenuRenderer extends WebComponent implements IGenericComponent<BrixNode> {
    /**
     * Constructor
     *
     * @param id
     * @param tileNodeModel
     */
    public AbstractMenuRenderer(String id, IModel<BrixNode> tileNodeModel) {
        super(id, tileNodeModel);
    }


    @SuppressWarnings("unchecked")
    public IModel<BrixNode> getModel() {
        return (IModel<BrixNode>) getDefaultModel();
    }

    public BrixNode getModelObject() {
        return (BrixNode) getDefaultModelObject();
    }

    public void setModel(IModel<BrixNode> model) {
        setDefaultModel(model);
    }

    public void setModelObject(BrixNode object) {
        setDefaultModelObject(object);
    }

    protected boolean anyChildSelected(ChildEntry entry, Set<ChildEntry> selectedSet) {
        if (entry.getChildren() != null) {
            for (ChildEntry e : entry.getChildren()) {
                if (selectedSet.contains(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean anyChildren(ChildEntry entry) {
        if (entry.getChildren() != null) {
            for (ChildEntry e : entry.getChildren()) {
                BrixNode node = getNode(e);
                if (node == null || SitePlugin.get().canViewNode(node, Context.PRESENTATION)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected BrixNode getNode(ChildEntry entry) {
        if (entry.getReference() != null && !entry.getReference().isEmpty() &&
                entry.getReference().getType() == Type.NODE) {
            return entry.getReference().getNodeModel().getObject();
        } else {
            List<ChildEntry> children = entry.getChildren();
            if (children != null && !children.isEmpty()) {
                return getNode(children.iterator().next());
            } else {
                return null;
            }
        }
    }

    protected Set<ChildEntry> getSelectedItems(Menu menu) {
        Set<ChildEntry> result = new HashSet<ChildEntry>();

        for (ChildEntry e : menu.getRoot().getChildren()) {
            checkSelected(e, result);
        }

        return result;
    }

    protected void checkSelected(ChildEntry entry, Set<ChildEntry> selectedSet) {
        if (isSelected(entry)) {
            for (Entry e = entry; e instanceof ChildEntry; e = e.getParent()) {
                selectedSet.add((ChildEntry) e);
            }
        }
        for (ChildEntry e : entry.getChildren()) {
            checkSelected(e, selectedSet);
        }
    }

    protected boolean isSelected(ChildEntry entry) {
        final String url = "/" + getRequest().getContextPath();
        Reference ref = entry.getReference();
        if (ref == null) {
            return false;
        } else {
            return isSelected(ref, url);
        }
    }

    protected String getUrl(ChildEntry entry) {
        if (entry.getReference() != null && !entry.getReference().isEmpty()) {
            return entry.getReference().generateUrl();
        } else {
            List<ChildEntry> children = entry.getChildren();
            if (children != null && !children.isEmpty()) {
                return getUrl(children.iterator().next());
            } else {
                return "#";
            }
        }
    }

    protected boolean isSelected(Reference reference, String url) {
        boolean eq = false;

        if (getPage() instanceof BrixNodeWebPage) {
            BrixNodeWebPage page = (BrixNodeWebPage) getPage();

            if (reference.getType() == Type.NODE) {
                eq = page.getModel().equals(reference.getNodeModel()) &&
                        comparePageParameters(page.getBrixPageParameters(), reference
                                .getParameters());
            } else {
                eq = url.equals(reference.getUrl()) &&
                        comparePageParameters(page.getBrixPageParameters(), reference
                                .getParameters());
            }
        }
        return eq;
    }

    protected boolean comparePageParameters(BrixPageParameters page, BrixPageParameters fromReference) {
        if (fromReference == null ||
                (fromReference.getIndexedCount() == 0 && fromReference.getNamedKeys()
                        .isEmpty())) {
            return true;
        } else {
            return BrixPageParameters.equals(page, fromReference);
        }
    }
}
