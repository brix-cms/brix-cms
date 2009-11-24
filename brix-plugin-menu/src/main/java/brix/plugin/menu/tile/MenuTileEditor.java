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

package brix.plugin.menu.tile;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.site.page.tile.admin.GenericTileEditorPanel;
import brix.web.util.AbstractModel;

public class MenuTileEditor extends GenericTileEditorPanel<BrixNode>
{

    public MenuTileEditor(String id, IModel<BrixNode> containerNode)
    {
        super(id, containerNode);

        IModel<List<BrixNode>> listViewModel = new LoadableDetachableModel<List<BrixNode>>()
        {
            @Override
            protected List<BrixNode> load()
            {
                return MenuPlugin.get().getMenuNodes(
                    MenuTileEditor.this.getModelObject().getSession().getWorkspace().getName());
            }
        };

        add(new MenuListView("listView", listViewModel));

        Form<MenuContainer> form;
        add(form = new Form<MenuContainer>("form", new CompoundPropertyModel<MenuContainer>(
            new PropertyModel<MenuContainer>(this, "currentEntry"))));

        form.add(new TextField<String>("outerContainerStyleClass"));
        form.add(new TextField<String>("innerContainerStyleClass"));
        form.add(new TextField<String>("selectedItemStyleClass"));
        form.add(new TextField<String>("itemWithSelectedChildStyleClass"));
        form.add(new TextField<Integer>("startAtLevel"));
        form.add(new TextField<Integer>("renderLevels"));
    }

    @Override
    public void load(BrixNode node)
    {
        currentEntry = new MenuContainer();
        currentEntry.load(node);
    }

    @Override
    public void save(BrixNode node)
    {
        currentEntry.save(node);
    }

    private MenuContainer currentEntry = new MenuContainer();

    private class MenuListView extends ListView<BrixNode>
    {
        public MenuListView(String id, IModel<List<BrixNode>> model)
        {
            super(id, model);

        }

        @Override
        protected void populateItem(final ListItem<BrixNode> item)
        {
            Link<Object> select = new Link<Object>("select")
            {
                @Override
                public void onClick()
                {
                    currentEntry.setMenuNode(item.getModelObject());
                }

                @Override
                public boolean isEnabled()
                {
                    BrixNode current = currentEntry.getMenuNode();
                    return current == null || !item.getModelObject().isSame(current);
                }
            };
            IModel<String> labelModel = new AbstractModel<String>()
            {
                @Override
                public String getObject()
                {
                    BrixNode node = item.getModelObject();
                    Menu menu = new Menu();
                    menu.loadName(node);
                    return menu.getName();
                }
            };
            select.add(new Label("label", labelModel));
            item.add(select);
        }

        @Override
        protected IModel<BrixNode> getListItemModel(IModel<? extends List<BrixNode>> listViewModel, int index)
        {
            List<BrixNode> nodes = listViewModel.getObject();
            return new BrixNodeModel(nodes.get(index));
        }
    };

    @Override
    protected void onDetach()
    {
        if (currentEntry != null)
        {
            currentEntry.detach();
        }
        super.onDetach();
    }
}
