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

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.Menu;

class MenuContainer implements IDetachable {
    private static final String PROP_OUTER_CONTAINER_STYLE_CLASS = "outerContainerStyleClass";

    private static final String PROP_INNER_CONTAINER_STYLE_CLASS = "innerContainerStyleClass";

    private static final String PROP_SELECTED_ITEM_STYLE_CLASS = "selectedItemStyleClass";

    private static final String PROP_ITEM_WITH_SELECTED_CHILD_STYLE_CLASS = "itemWithSelectedChildStyleClass";

    private static final String PROP_MENU = "menu";

    private static final String PROP_START_AT_LEVEL = "startAtLevel";

    private static final String PROP_RENDER_LEVELS = "renderLevels";
    IModel<BrixNode> menuNodeModel = new BrixNodeModel();
    private Menu cachedMenu;

    private String outerContainerStyleClass;

    private String innerContainerStyleClass;

    private String selectedItemStyleClass;

    private String itemWithSelectedChildStyleClass;

    private Integer startAtLevel;

    private Integer renderLevels;

    public String getInnerContainerStyleClass() {
        return innerContainerStyleClass;
    }

    public void setInnerContainerStyleClass(String innerContainerStyleClass) {
        this.innerContainerStyleClass = innerContainerStyleClass;
    }

    public String getItemWithSelectedChildStyleClass() {
        return itemWithSelectedChildStyleClass;
    }

    public void setItemWithSelectedChildStyleClass(String itemWithSelecteChildStyleClass) {
        this.itemWithSelectedChildStyleClass = itemWithSelecteChildStyleClass;
    }

    public String getOuterContainerStyleClass() {
        return outerContainerStyleClass;
    }

    public void setOuterContainerStyleClass(String outerContainerStyleClass) {
        this.outerContainerStyleClass = outerContainerStyleClass;
    }

    public Integer getRenderLevels() {
        return renderLevels;
    }

    public void setRenderLevels(Integer renderLevels) {
        this.renderLevels = renderLevels;
    }

    public String getSelectedItemStyleClass() {
        return selectedItemStyleClass;
    }

    public void setSelectedItemStyleClass(String selectedItemStyleClass) {
        this.selectedItemStyleClass = selectedItemStyleClass;
    }

    public Integer getStartAtLevel() {
        return startAtLevel;
    }

    public void setStartAtLevel(Integer startAtLevel) {
        this.startAtLevel = startAtLevel;
    }

    public Menu getMenu() {
        if (cachedMenu == null && getMenuNode() != null) {
            cachedMenu = new Menu();
            cachedMenu.load(getMenuNode());
        }
        return cachedMenu;
    }

    public BrixNode getMenuNode() {
        return menuNodeModel.getObject();
    }

    public void load(BrixNode node) {
        detach();
        if (node.hasProperty(PROP_INNER_CONTAINER_STYLE_CLASS)) {
            setInnerContainerStyleClass(node.getProperty(PROP_INNER_CONTAINER_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_OUTER_CONTAINER_STYLE_CLASS)) {
            setOuterContainerStyleClass(node.getProperty(PROP_OUTER_CONTAINER_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_SELECTED_ITEM_STYLE_CLASS)) {
            setSelectedItemStyleClass(node.getProperty(PROP_SELECTED_ITEM_STYLE_CLASS).getString());
        }
        if (node.hasProperty(PROP_ITEM_WITH_SELECTED_CHILD_STYLE_CLASS)) {
            setItemWithSelectedChildStyleClass(node.getProperty(PROP_ITEM_WITH_SELECTED_CHILD_STYLE_CLASS)
                    .getString());
        }
        if (node.hasProperty(PROP_MENU)) {
            setMenuNode((BrixNode) node.getProperty(PROP_MENU).getNode());
        }
        if (node.hasProperty(PROP_START_AT_LEVEL)) {
            setStartAtLevel((int) node.getProperty(PROP_START_AT_LEVEL).getLong());
        }
        if (node.hasProperty(PROP_RENDER_LEVELS)) {
            setRenderLevels((int) node.getProperty(PROP_RENDER_LEVELS).getLong());
        }
    }

    public void detach() {
        menuNodeModel.detach();
        cachedMenu = null;
    }

    public void setMenuNode(BrixNode node) {
        this.cachedMenu = null;
        this.menuNodeModel.setObject(node);
    }

    public void save(BrixNode node) {
        node.setProperty(PROP_INNER_CONTAINER_STYLE_CLASS, getInnerContainerStyleClass());
        node.setProperty(PROP_OUTER_CONTAINER_STYLE_CLASS, getOuterContainerStyleClass());
        node.setProperty(PROP_SELECTED_ITEM_STYLE_CLASS, getSelectedItemStyleClass());
        node.setProperty(PROP_ITEM_WITH_SELECTED_CHILD_STYLE_CLASS, getItemWithSelectedChildStyleClass());
        node.setProperty(PROP_MENU, getMenuNode());

        if (getStartAtLevel() == null) {
            node.setProperty(PROP_START_AT_LEVEL, (String) null);
        } else {
            node.setProperty(PROP_START_AT_LEVEL, getStartAtLevel());
        }

        if (getRenderLevels() == null) {
            node.setProperty(PROP_RENDER_LEVELS, (String) null);
        } else {
            node.setProperty(PROP_RENDER_LEVELS, getRenderLevels());
        }
    }
}
