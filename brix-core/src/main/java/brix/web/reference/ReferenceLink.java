package brix.web.reference;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;

public class ReferenceLink extends AbstractLink
{
	private static final long serialVersionUID = 1L;

	private final Reference reference;
	
	public ReferenceLink(String id, Reference reference)
	{
		super(id);
		this.reference = reference;
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		if (!isLinkEnabled())
		{
			disableLink(tag);
		}
		tag.put("href", reference.getUrl());
	}

	@Override
	protected void onDetach()
	{
		reference.detach();
		super.onDetach();
	}

}
