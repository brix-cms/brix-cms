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

package org.brixcms.plugin.menu.tile.subtree;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.Menu.ChildEntry;
import org.brixcms.plugin.menu.Menu.Entry;
import org.brixcms.plugin.menu.tile.AbstractMenuRenderer;
import org.brixcms.plugin.site.SitePlugin;

import java.util.Set;

/**
 * Component used to render the menu
 *
 * @author igor.vaynberg
 */
class MenuRenderer extends AbstractMenuRenderer {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param id
     * @param model
     */
    public MenuRenderer(String id, IModel<BrixNode> model) {
        super(id, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        MenuContainer container = new MenuContainer();
        container.load(getModelObject());

        Set<ChildEntry> selected = getSelectedItems(container.getMenu());

        // how many levels to skip to start rendering
        int skipLevels = container.getStartAtLevel() != null ? container.getStartAtLevel() : 0;

        // how many levels should be rendered
        int renderLevels = container.getRenderLevels() != null
                ? container.getRenderLevels()
                : Integer.MAX_VALUE;

        Response response = getResponse();
        renderEntry(container, container.getMenu().getRoot(), response, selected, skipLevels,
                renderLevels);
    }

    private void renderEntry(MenuContainer container, Entry entry, Response response,
                             Set<ChildEntry> selected, int skipLevels, int renderLevels) {
        if (renderLevels <= 0) {
            return;
        }

        if (skipLevels <= 0) {
            boolean outer = skipLevels == 0;
            String klass = "";
            if (outer && !Strings.isEmpty(container.getOuterContainerStyleClass())) {
                klass = " class='" + container.getOuterContainerStyleClass() + "'";
            } else if (!outer && !Strings.isEmpty(container.getInnerContainerStyleClass())) {
                klass = " class='" + container.getInnerContainerStyleClass() + "'";
            }
            response.write("\n<ul");
            response.write(klass);
            response.write(">\n");
        }

        for (ChildEntry e : entry.getChildren()) {
            BrixNode node = getNode(e);
            if (node == null || SitePlugin.get().canViewNode(node, Context.PRESENTATION)) {
                renderChild(container, e, response, selected, skipLevels, renderLevels);
            }
        }

        if (skipLevels <= 0) {
            response.write("</ul>\n");
        }
    }

    private void renderChild(MenuContainer container, ChildEntry entry, Response response,
                             Set<ChildEntry> selectedSet, int skipLevels, int renderLevels) {
        boolean selected = selectedSet.contains(entry);

        boolean anyChildren = selected && anyChildren(entry);

        if (skipLevels <= 0) {
            String listItemCssClass = "";
            String anchorCssClass = "";
            if (selected && !Strings.isEmpty(container.getSelectedItemStyleClass())) {
                listItemCssClass = container.getSelectedItemStyleClass();
                anchorCssClass = container.getSelectedItemStyleClass();
            }

            if (anyChildren && selected && anyChildSelected(entry, selectedSet)
                    && !Strings.isEmpty(container.getItemWithSelectedChildStyleClass())) {
                listItemCssClass = container.getItemWithSelectedChildStyleClass();
            }

            if (!Strings.isEmpty(entry.getCssClass())) {
                if (!Strings.isEmpty(listItemCssClass)) {
                    listItemCssClass += " ";
                }
                listItemCssClass += entry.getCssClass();
            }

            response.write("\n<li");

            if (!Strings.isEmpty(listItemCssClass)) {
                response.write(" class=\"");
                response.write(listItemCssClass);
                response.write("\"");
            }

            response.write(">");


            //Rendering for REFERENCE
            if (entry.getMenuType() == ChildEntry.MenuType.REFERENCE) {
                final String url = getUrl(entry);

                response.write("<a");
                if (!Strings.isEmpty(anchorCssClass)) {
                    response.write(" class=\"");
                    response.write(anchorCssClass);
                    response.write("\"");
                }
                response.write(" href=\"");
                response.write(url);
                response.write("\"><span>");

                // TODO. escape or not (probably a property would be nice?
                response.write(entry.getTitle());
                response.write("</span></a>");
            }

            //Rendering for CODE
            else if (entry.getMenuType() == ChildEntry.MenuType.CODE) {
                response.write(entry.getLabelOrCode());
            }
            //Rendering for LABEL
            else if (entry.getMenuType() == ChildEntry.MenuType.LABEL) {
                response.write(Strings.escapeMarkup(entry.getLabelOrCode(), false, true));
            }
        }

        // only decrement skip levels for child if current is begger than 0
        int childSkipLevels = skipLevels - 1;

        // only decrement render levels when we are already rendering
        int childRenderLevels = skipLevels <= 0 ? renderLevels - 1 : renderLevels;

        if (anyChildren) {
            renderEntry(container, entry, response, selectedSet, childSkipLevels, childRenderLevels);
        }

        if (skipLevels == 0) {
            response.write("</li>\n");
        }
    }
}
