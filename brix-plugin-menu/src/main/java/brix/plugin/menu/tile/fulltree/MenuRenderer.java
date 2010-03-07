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

package brix.plugin.menu.tile.fulltree;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.Menu.ChildEntry;
import brix.plugin.menu.tile.AbstractMenuRenderer;
import brix.plugin.site.SitePlugin;
import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import java.util.Set;

/**
 * Component used to render the menu
 * 
 * @author igor.vaynberg
 * 
 */
public class MenuRenderer extends AbstractMenuRenderer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param model
	 */
	public MenuRenderer(String id, IModel<BrixNode> model)
	{
		super(id, model);
	}

	/** {@inheritDoc} */
	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
	{
		NodeAdapter adapter = new NodeAdapter(getModelObject());
		Menu menu = new Menu();
		menu.load(adapter.getMenuNode());

		final Response response = getResponse();
		response.write("<ul");
		if (!Strings.isEmpty(adapter.getOuterUlCssClass()))
		{
			response.write(" class=\"");
			response.write(adapter.getOuterUlCssClass());
			response.write("\"");
		}
		response.write(">");
		for (ChildEntry entry : menu.getRoot().getChildren())
		{
			renderEntry(entry, adapter, response, getSelectedItems(menu));
		}
		response.write("</ul>");
	}

	private void renderEntry(ChildEntry entry, NodeAdapter adapter, Response response, Set<ChildEntry> selectedItems)
	{
		// build css classes string
		StringBuilder cssClasses = new StringBuilder();
		if ((!Strings.isEmpty(adapter.getSelectedLiCssClass()) && isSelected(entry)) ||
                (adapter.getSelectAllParentLi() && anyChildSelected(entry, selectedItems)))
		{
			cssClasses.append(adapter.getSelectedLiCssClass()).append(" ");
		}
		if (!Strings.isEmpty(entry.getCssClass()))
		{
			cssClasses.append(entry.getCssClass()).append(" ");
		}
		if (cssClasses.length() > 0)
		{
			cssClasses.deleteCharAt(cssClasses.length() - 1);
		}

		response.write("<li");
		if (cssClasses.length() > 0)
		{
			response.write(" class=\"");
			response.write(cssClasses);
			response.write("\"");
		}
		response.write(">");
		response.write("<a");
		if (cssClasses.length() > 0)
		{
			response.write(" class=\"");
			response.write(cssClasses);
			response.write("\"");
		}
		response.write(" href=\"");
		response.write(getUrl(entry));
		response.write("\">");
		response.write(entry.getTitle());
		response.write("</a>");

		if (anyChildren(entry))
		{
			response.write("<ul>");
			for (ChildEntry e : entry.getChildren())
			{
				BrixNode node = getNode(e);
				if (node == null || SitePlugin.get().canViewNode(node, Context.PRESENTATION))
				{
					renderEntry(e, adapter, response, selectedItems);
				}
			}
			response.write("</ul>");
		}
		response.write("</li>");
	}
}
