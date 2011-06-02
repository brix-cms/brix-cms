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

package org.brixcms.plugin.menu.tile.fulltree;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.Menu;
import org.brixcms.plugin.menu.Menu.ChildEntry;
import org.brixcms.plugin.menu.tile.AbstractMenuRenderer;
import org.brixcms.plugin.site.SitePlugin;

import java.util.List;
import java.util.Set;

/**
 * Component used to render the menu
 *
 * @author igor.vaynberg
 */
public class MenuRenderer extends AbstractMenuRenderer {
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
        NodeAdapter adapter = new NodeAdapter(getModelObject());
        Menu menu = new Menu();
        menu.load(adapter.getMenuNode());

        final Response response = getResponse();
        response.write("<ul");
        if (!Strings.isEmpty(adapter.getOuterUlCssClass())) {
            response.write(" class=\"");
            response.write(adapter.getOuterUlCssClass());
            response.write("\"");
        }
        response.write(">");
        List<ChildEntry> childEntryList = menu.getRoot().getChildren();
        ChildEntry firstEntry = childEntryList.get(0);
        ChildEntry lastEntry = childEntryList.get(childEntryList.size() - 1);
        for (ChildEntry entry : childEntryList) {
            renderEntry(entry, adapter, response, getSelectedItems(menu), firstEntry.equals(entry), lastEntry.equals(entry));
        }
        response.write("</ul>");
    }

    private void renderEntry(ChildEntry entry, NodeAdapter adapter, Response response, Set<ChildEntry> selectedItems, boolean isFirst, boolean isLast) {
        // build css classes string
        StringBuilder cssClasses = new StringBuilder();
        if ((!Strings.isEmpty(adapter.getSelectedLiCssClass()) && isSelected(entry)) ||
                (adapter.getSelectAllParentLi() && anyChildSelected(entry, selectedItems))) {
            cssClasses.append(adapter.getSelectedLiCssClass()).append(" ");
        }
        if (isFirst && !Strings.isEmpty(adapter.getFirstLiCssClass())) {
            cssClasses.append(adapter.getFirstLiCssClass()).append(" ");
        }
        if (isLast && !Strings.isEmpty(adapter.getLastLiCssClass())) {
            cssClasses.append(adapter.getLastLiCssClass()).append(" ");
        }
        if (!Strings.isEmpty(entry.getCssClass())) {
            cssClasses.append(entry.getCssClass()).append(" ");
        }
        if (cssClasses.length() > 0) {
            cssClasses.deleteCharAt(cssClasses.length() - 1);
        }

        response.write("<li");
        if (cssClasses.length() > 0) {
            response.write(" class=\"");
            response.write(cssClasses);
            response.write("\"");
        }
        response.write(">");

        //Rendering for REFERENCE
        if (entry.getMenuType() == ChildEntry.MenuType.REFERENCE) {
            response.write("<a");
            if (cssClasses.length() > 0) {
                response.write(" class=\"");
                response.write(cssClasses);
                response.write("\"");
            }
            response.write(" href=\"");
            response.write(getUrl(entry));
            response.write("\">");
            response.write(entry.getTitle());
            response.write("</a>");
        }

        //Rendering for CODE
        else if (entry.getMenuType() == ChildEntry.MenuType.CODE) {
            response.write(entry.getLabelOrCode());
        }
        //Rendering for LABEL
        else if (entry.getMenuType() == ChildEntry.MenuType.LABEL) {
            response.write(Strings.escapeMarkup(entry.getLabelOrCode(), false, true));
        }


        if (anyChildren(entry)) {
            response.write("<ul");
            if (!Strings.isEmpty(adapter.getInnerUlCssClass())) {
                response.write(" class=\"");
                response.write(adapter.getInnerUlCssClass());
                response.write("\"");
            }
            response.write(">");
            List<ChildEntry> childEntryList = entry.getChildren();
            ChildEntry firstEntry = childEntryList.get(0);
            ChildEntry lastEntry = childEntryList.get(childEntryList.size() - 1);
            for (ChildEntry e : childEntryList) {
                BrixNode node = getNode(e);
                if (node == null || SitePlugin.get().canViewNode(node, Context.PRESENTATION)) {
                    renderEntry(e, adapter, response, selectedItems, firstEntry.equals(e), lastEntry.equals(e));
                }
            }
            response.write("</ul>");
        }
        response.write("</li>");
    }
}
