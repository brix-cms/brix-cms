package brix.web.tile.menu;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.Menu.ChildEntry;
import brix.plugin.menu.Menu.Entry;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.reference.Reference;
import brix.web.reference.Reference.Type;

public class MenuRenderer extends WebComponent<BrixNode>
{

    public MenuRenderer(String id, IModel<BrixNode> model)
    {
        super(id, model);
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
    {
        MenuContainer container = new MenuContainer();
        container.load(getModelObject());

        Set<ChildEntry> selected = getSelectedItems(container.getMenu());
        
        System.out.println(selected.size());

        Response response = getResponse();
        renderEntry(container, container.getMenu().getRoot(), response, selected);
    }

    private void renderEntry(MenuContainer container, Entry entry, Response response,
            Set<ChildEntry> selected)
    {
        boolean outer = entry.getParent() == null;

        String klass = "";
        if (outer && !Strings.isEmpty(container.getOuterContainerStyleClass()))
        {
            klass = " class='" + container.getOuterContainerStyleClass() + "'";
        }
        else if (!outer && !Strings.isEmpty(container.getInnerContainerStyleClass()))
        {
            klass = " class='" + container.getInnerContainerStyleClass() + "'";
        }


        response.write("\n<ul");
        response.write(klass);
        response.write(">\n");
        
        for (ChildEntry e : entry.getChildren())
        {
            renderChild(container, e, response, selected);
        }
        
        response.write("</ul>\n");

    }
    
    private void renderChild(MenuContainer container, ChildEntry entry, Response response, Set<ChildEntry> selectedSet)
    {
        boolean selected = selectedSet.contains(entry);
        
        String klass="";
        
        if (selected && !Strings.isEmpty(container.getSelectedItemStyleClass()))
        {
            klass = " class='" + container.getSelectedItemStyleClass() + "'";
        } 
        else if (!selected && !Strings.isEmpty(container.getItemStyleClass()))
        {
            klass = " class='" + container.getItemStyleClass() + "'";
        }
        
        response.write("\n<li");
        response.write(klass);
        response.write(">");
        
        final String url = entry.getReference() != null ? entry.getReference().generateUrl() : "#";
            
        response.write("<a href='");
        response.write(url);
        response.write("'>");
        
        // TODO. escape or not (probably a property would be nice?
        
        response.write(entry.getTitle());
        
        response.write("</a>");
        
        if (selected && !entry.getChildren().isEmpty())
        {
            renderEntry(container, entry, response, selectedSet);
        }
        
        response.write("</li>\n");
    }

    private boolean isSelected(Reference reference, String url)
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

    private boolean isSelected(ChildEntry entry)
    {
        final String url = "/" + getRequest().getPath();
        Reference ref = entry.getReference();
        if (ref == null)
        {
            return false;
        }
        else
        {
            return isSelected(ref, url);
        }
    }

    private void checkSelected(ChildEntry entry, Set<ChildEntry> selectedSet)
    {
        if (isSelected(entry))
        {
            for (Entry e = entry; e instanceof ChildEntry; e = e.getParent())
            {
                selectedSet.add((ChildEntry)e);
            }
        }
        for (ChildEntry e : entry.getChildren())
        {
            checkSelected(e, selectedSet);
        }
    }

    Set<ChildEntry> getSelectedItems(Menu menu)
    {
        Set<ChildEntry> result = new HashSet<ChildEntry>();

        for (ChildEntry e : menu.getRoot().getChildren())
        {
            checkSelected(e, result);
        }

        return result;
    }

}
