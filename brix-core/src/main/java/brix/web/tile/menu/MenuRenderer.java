package brix.web.tile.menu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.Menu;
import brix.plugin.menu.Menu.ChildEntry;
import brix.plugin.menu.Menu.Entry;
import brix.plugin.site.SitePlugin;
import brix.web.generic.IGenericComponent;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.reference.Reference;
import brix.web.reference.Reference.Type;

public class MenuRenderer extends WebComponent implements IGenericComponent<BrixNode>
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
		
		// how many levels to skip to start rendering 
		int skipLevels = container.getStartAtLevel() != null ? container.getStartAtLevel() : 0;
		
		// how many levels should be rendered
		int renderLevels = container.getRenderLevels() != null ? container.getRenderLevels() : Integer.MAX_VALUE;
		
		Response response = getResponse();
		renderEntry(container, container.getMenu().getRoot(), response, selected, skipLevels, renderLevels);
	}	

	private void renderEntry(MenuContainer container, Entry entry, Response response, Set<ChildEntry> selected, int skipLevels, int renderLevels)
	{
		if (renderLevels <= 0)
		{
			return;
		}
		
		if (skipLevels <= 0)
		{
			boolean outer = skipLevels == 0;
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
		}

		for (ChildEntry e : entry.getChildren())
		{
			BrixNode node = getNode(e);
			if (SitePlugin.get().canViewNode(node, Context.PRESENTATION))
			{
				renderChild(container, e, response, selected, skipLevels, renderLevels);	
			}			
		}

		if (skipLevels <= 0)
		{
			response.write("</ul>\n");
		}		
	}
	
	private BrixNode getNode(ChildEntry entry)
	{
		if (entry.getReference() != null && !entry.getReference().isEmpty() && entry.getReference().getType() == Type.NODE)
		{
			return entry.getReference().getNodeModel().getObject();
		}
		else
		{
			List<ChildEntry> children = entry.getChildren();
			if (children != null && !children.isEmpty())
			{
				return getNode(children.iterator().next());
			}
			else
			{
				return null;
			}
		}
	}
	
	private String getUrl(ChildEntry entry)
	{
		if (entry.getReference() != null && !entry.getReference().isEmpty())
		{
			return entry.getReference().generateUrl();
		}
		else 
		{
			List<ChildEntry> children = entry.getChildren();
			if (children != null && !children.isEmpty())
			{
				return getUrl(children.iterator().next());
			}
			else
			{
				return "#";
			}	
		}		
	}

	private void renderChild(MenuContainer container, ChildEntry entry, Response response, Set<ChildEntry> selectedSet, int skipLevels, int renderLevels)
	{	
		boolean selected = selectedSet.contains(entry);

		if (skipLevels <= 0)
		{		
			String klass = "";
	
			if (selected && !Strings.isEmpty(container.getSelectedItemStyleClass()))
			{
				klass = container.getSelectedItemStyleClass();
			}
	
			if (!Strings.isEmpty(entry.getCssClass()))
			{
				if (!Strings.isEmpty(klass))
				{
					klass += " ";
				}
				klass += entry.getCssClass();
			}
	
			response.write("\n<li");
	
			if (!Strings.isEmpty(klass))
			{
				response.write(" class=\"");
				response.write(klass);
				response.write("\"");
			}
	
			response.write(">");
	
			final String url = getUrl(entry);
	
			response.write("<a href='");
			response.write(url);
			response.write("'>");
	
			// TODO. escape or not (probably a property would be nice?
	
			response.write(entry.getTitle());
	
			response.write("</a>");
		}

		// only decrement skip levels for child if current is begger than 0
		int childSkipLevels = skipLevels - 1;
		
		// only decrement render levels when we are already rendering
		int childRenderLevels = skipLevels <= 0 ? renderLevels - 1 : renderLevels;
		
		if (selected && !entry.getChildren().isEmpty())
		{
			renderEntry(container, entry, response, selectedSet, childSkipLevels, childRenderLevels);
		}

		if (skipLevels == 0)
		{
			response.write("</li>\n");
		}
	}

	private boolean isSelected(Reference reference, String url)
	{
		boolean eq = false;

		BrixNodeWebPage page = (BrixNodeWebPage) getPage();

		if (reference.getType() == Type.NODE)
		{
			eq = page.getModel().equals(reference.getNodeModel())
					&& comparePageParameters(page.getBrixPageParameters(), reference.getParameters());

		}
		else
		{
			eq = url.equals(reference.getUrl())
					&& comparePageParameters(page.getBrixPageParameters(), reference.getParameters());
		}

		return eq;
	}
	
	private boolean comparePageParameters(BrixPageParameters page, BrixPageParameters fromReference)
	{
		if (fromReference == null || (fromReference.getIndexedParamsCount() == 0 && fromReference.getQueryParamKeys().isEmpty()))
		{
			return true;
		}
		else
		{
			return BrixPageParameters.equals(page, fromReference);
		}
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
				selectedSet.add((ChildEntry) e);
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

	@SuppressWarnings("unchecked")
	public IModel<BrixNode> getModel()
	{
		return (IModel<BrixNode>) getDefaultModel();
	}

	public BrixNode getModelObject()
	{
		return (BrixNode) getDefaultModelObject();
	}

	public void setModel(IModel<BrixNode> model)
	{
		setDefaultModel(model);
	}

	public void setModelObject(BrixNode object)
	{
		setDefaultModelObject(object);
	}

}
