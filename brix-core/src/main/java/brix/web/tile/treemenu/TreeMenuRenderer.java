package brix.web.tile.treemenu;

import java.util.Stack;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import brix.jcr.api.JcrNode;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.reference.Reference;
import brix.web.reference.Reference.Type;
import brix.web.tile.treemenu.TreeMenuTile.Item;
import brix.web.tile.treemenu.TreeMenuTile.RootItem;

public class TreeMenuRenderer extends WebComponent
{

    public TreeMenuRenderer(String id, IModel<JcrNode> tileNode)
    {
        super(id, tileNode);
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
        final Response r = getResponse();

        RootItem item = new RootItem();
        TreeMenuTile.load(item, (JcrNode)getModelObject());

        String url = "/" + getRequest().getPath();

        Selection selection = new Selection(item, url);
        renderItem(item, item, selection, r);
        item.detach();
    }

    private void renderItem(RootItem root, Item item, Selection selection, Response r)
    {
        r.write("\n<ul");
        if (!Strings.isEmpty(item.getContainerCssId()))
        {
            r.write(" id=\"");
            r.write(item.getContainerCssId());
            r.write("\"");
        }
        r.write(">\n");

        for (Item child : item.getChildren())
        {
            final boolean selected = selection.isSelected(child);
            r.write("<li");
            if (!Strings.isEmpty(child.getItemCssId()))
            {
                r.write(" class=\"");
                r.write(child.getItemCssId());
                r.write("\"");
            }
            r.write("><a href=\"");
            r.write(child.getReference().generateUrl());
            r.write("\"");
            if (selected && !Strings.isEmpty(root.getSelectedCssClass()))
            {
                r.write(" class=\"");
                r.write(root.getSelectedCssClass());
                r.write("\"");
            }
            r.write(">");
            r.write(child.getName());
            r.write("</a>");
            if (selected)
            {
                renderItem(root, child, selection, r);
            }
            r.write("</li>\n");
        }

        r.write("</ul>\n");
    }

    private class Selection
    {
        private Stack<Item> selection = new Stack<Item>();

        public Selection(RootItem item, String url)
        {
            process(item, url);
        }

        public boolean isSelected(Item item)
        {
            return selection.contains(item);
        }


        private boolean selected(Reference reference, String url)
        {

            boolean eq = false;

            BrixNodeWebPage page = (BrixNodeWebPage)getPage();

            if (reference.getType() == Type.NODE)
            {
                eq = page.getNodeModel().equals(reference.getNodeModel()) &&
                        page.getBrixPageParameters().equals(reference.getParameters());
            }
            else
            {
                eq = url.equals(reference.getUrl()) &&
                        page.getBrixPageParameters().equals(reference.getParameters());
            }

            return eq;
        }

        private boolean process(Item item, String url)
        {

            selection.push(item);


            if (selected(item.getReference(), url))
            {
                // we hit a selected url, check if there is also a child under
                // this item that is the "default" next-level selected child

                for (Item child : item.getChildren())
                {
                    if (selected(child.getReference(), url))
                    {
                        selection.push(child);
                    }
                }

                return true;
            }

            for (Item child : item.getChildren())
            {
                if (process(child, url) == true)
                {
                    return true;
                }
            }

            selection.pop();
            return false;
        }
    }
}
