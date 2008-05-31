package brix.web.nodepage.markup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;

public class MarkupHelper implements Serializable
{
	public MarkupHelper(MarkupContainer<BrixNode> component)
	{
		this.component = component;
		initMarkup();
	}

	private final MarkupContainer<BrixNode> component;

	private final static String COMPONENT_PREFIX = "brix-";

	private String getComponentID(ComponentTag tag)
	{
		UUID uuid = tag.getUUID();
		return COMPONENT_PREFIX + (uuid != null ? uuid.toString() : "");
	}

	private String markup = null;
	
	public String getMarkup()
	{
		return markup;
	}

	private MarkupCache getMarkupCache()
	{
		return SitePlugin.get().getMarkupCache();
	}
	
	private Set<String> getExistingComponents()
	{
		Set<String> result = new HashSet<String>();
		Iterator<Component<?>> i = component.iterator();
		while (i.hasNext())
		{
			Component<?> c = i.next();
			if (c.getId().startsWith(COMPONENT_PREFIX))
			{
				result.add(c.getId());
			}
		}
		return result;
	}
	
	private void initMarkup()
	{
		final Set<String> existingComponents = getExistingComponents();
		final Set<String> components = new HashSet<String>();
		GeneratedMarkup markup = getMarkupCache().getMarkup(component);
		MarkupRenderer renderer = new MarkupRenderer(markup.items)
		{
			@Override
			void postprocessTagAttributes(Tag tag, Map<String, String> attributes)
			{
				if (tag instanceof ComponentTag && tag.getType() != Tag.Type.CLOSE)
				{
					ComponentTag componentTag = (ComponentTag) tag;
					String id = getComponentID(componentTag);
					if (existingComponents.contains(id))
					{
						attributes.put("wicket:id", id);
						components.add(id);
					}
					else
					{
						Component<?> c = componentTag.getComponent(id);
						if (c != null)
						{
							attributes.put("wicket:id", id);
							components.add(id);
							component.add(c);
						}
					}	
				}
			}
		};
		this.markup = renderer.render();
		
		// go through existing components and remove those not present in current markup
		for (String s : existingComponents)
		{
			if (!components.contains(s))
			{
				component.get(s).remove();
			}
		}
	}

}
