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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.tile.MenuPicker;
import brix.plugin.site.page.tile.admin.TileEditorPanel;

class FullTreeMenuEditor extends TileEditorPanel
{
	private BrixNodeModel menu = new BrixNodeModel();
	private String selectedLiCssClass;
	private String outerUlCssClass;

	public FullTreeMenuEditor(String id, IModel<BrixNode> tileParent)
	{
		super(id);
		add(new MenuPicker("menuPicker", menu, tileParent));
		Form<?> form = new Form<Void>("form");
		add(form);
		form.add(new TextField<String>("selectedLiCssClass", new PropertyModel<String>(this,
				"selectedLiCssClass")));
		form.add(new TextField<String>("outerUlCssClass", new PropertyModel<String>(this,
				"outerUlCssClass")));
	}

	@Override
	public void load(BrixNode node)
	{
		NodeAdapter adapter = new NodeAdapter(node);
		menu.setObject(adapter.getMenuNode());
		selectedLiCssClass = adapter.getSelectedLiCssClass();
		outerUlCssClass = adapter.getOuterUlCssClass();
	}

	@Override
	public void save(BrixNode node)
	{
		NodeAdapter adapter = new NodeAdapter(node);
		adapter.setMenuNode(menu.getObject());
		adapter.setSelectedLiCssClass(selectedLiCssClass);
		adapter.setOuterUlCssClass(outerUlCssClass);
	}


}
