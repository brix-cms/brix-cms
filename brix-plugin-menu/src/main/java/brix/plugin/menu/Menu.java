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

package brix.plugin.menu;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IDetachable;

import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;
import brix.web.reference.Reference;

public class Menu implements IDetachable
{

    public static class Entry implements IDetachable
    {
        private final Entry parent;

        public Entry(Entry parent)
        {
            this.parent = parent;
        }

        public Entry getParent()
        {
            return parent;
        }

        private final List<ChildEntry> children = new ArrayList<ChildEntry>();

        public List<ChildEntry> getChildren()
        {
            return children;
        }

        public void detach()
        {
            for (ChildEntry entry : children)
            {
                entry.detach();
            }
        }
    }

    public static class RootEntry extends Entry
    {
        public RootEntry()
        {
            super(null);
        }

        @Override
        public String toString()
        {
            return "Menu Root";
        }

        public String getTitle()
        {
        	return "Menu Root";
        }

        public Reference getReference()
        {
        	return null;
        }

        public String getCssClass()
        {
        	return null;
        }
    }

    public static class ChildEntry extends Entry
    {

        public ChildEntry(Entry parent)
        {
            super(parent);
        }



        public static enum MenuType {
            REFERENCE, LABEL, CODE
        }

        private MenuType menuType;

        public MenuType getMenuType() {
            if(menuType == null) {
                menuType = MenuType.REFERENCE;
            }
        return menuType;
        }

        public void setMenuType(MenuType menuType) {
            this.menuType = menuType;
        }

        private String title;
        private Reference reference;
        private String labelOrCode;
        private String cssClass;

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public Reference getReference()
        {
            return reference;
        }

        public void setReference(Reference reference)
        {
            this.reference = reference;
        }

        public String getLabelOrCode() {
            return labelOrCode;
        }

        public void setLabelOrCode(String labelOrCode) {
            this.labelOrCode = labelOrCode;
        }

        public String getCssClass()
        {
            return cssClass;
        }

        public void setCssClass(String cssClass)
        {
            this.cssClass = cssClass;
        }

        @Override
        public void detach()
        {
            super.detach();
            if (reference != null)
            {
                reference.detach();
            }
        }

        @Override
        public String toString()
        {
            return getTitle();
        }
    }

    private RootEntry root = new RootEntry();

    public RootEntry getRoot()
    {
        return root;
    }

    public void detach()
    {
        root.detach();
    }

    private String name;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    private void saveEntry(BrixNode node, Entry entry)
    {
        if (entry instanceof ChildEntry)
        {
            ChildEntry childEntry = (ChildEntry)entry;
            node.setProperty("title", childEntry.getTitle());
            node.setProperty("cssClass", childEntry.getCssClass());
            node.setProperty("menuType", childEntry.getMenuType().toString());
            node.setProperty("labelOrCode", childEntry.getLabelOrCode());
            if (childEntry.getReference() != null)
            {
                childEntry.getReference().save(node, "reference");
            }
        }
        for (Entry e : entry.getChildren())
        {
            BrixNode child = (BrixNode)node.addNode("child");
            saveEntry(child, e);
        }
    }

    public void save(BrixNode node)
    {
        if (!node.isNodeType("mix:referenceable"))
        {
            node.addMixin("mix:referenceable");
        }
        node.setProperty("name", getName());
        if (node.hasNode("menu"))
        {
            node.getNode("menu").remove();
        }
        BrixNode menu = (BrixNode)node.addNode("menu", "nt:unstructured");
        saveEntry(menu, getRoot());
    }

    public void loadName(BrixNode node)
    {
        if (node.hasProperty("name"))
        {
            setName(node.getProperty("name").getString());
        }
    }

    private void loadChildEntry(BrixNode node, ChildEntry entry)
    {
        if (node.hasProperty("title"))
        {
            entry.setTitle(node.getProperty("title").getString());
        }

        entry.setReference(Reference.load(node, "reference"));

        if (node.hasProperty("cssClass"))
        {
            entry.setCssClass(node.getProperty("cssClass").getString());
        }

        if (node.hasProperty("labelOrCode")) {
            entry.setLabelOrCode(node.getProperty("labelOrCode").getString());
        }

        if(node.hasProperty("menuType") && ChildEntry.MenuType.valueOf(node.getProperty("menuType").getString()) != null) {
            entry.setMenuType(ChildEntry.MenuType.valueOf(node.getProperty("menuType").getString()));
        }
    }

    private void loadEntry(BrixNode node, Entry entry)
    {
        JcrNodeIterator i = node.getNodes("child");
        while (i.hasNext())
        {
            BrixNode child = (BrixNode)i.nextNode();
            ChildEntry e = new ChildEntry(entry);
            loadChildEntry(child, e);
            loadEntry(child, e);
            entry.getChildren().add(e);
        }
    }

    public void loadMenu(BrixNode node)
    {
        root = new RootEntry();
        if (node.hasNode("menu"))
        {
            loadEntry((BrixNode)node.getNode("menu"), root);
        }
    }

    public void load(BrixNode node)
    {
        loadName(node);
        loadMenu(node);
    }

}
