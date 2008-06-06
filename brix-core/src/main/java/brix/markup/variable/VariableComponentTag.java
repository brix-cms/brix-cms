package brix.markup.variable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.markup.tag.ComponentTag;

public class VariableComponentTag extends VariableTag implements ComponentTag
{

	public VariableComponentTag(BrixNode pageNode, ComponentTag delegate)
	{
		super(pageNode, delegate);
	}
	
	@Override
	public ComponentTag getDelegate()
	{
		return (ComponentTag) super.getDelegate();
	}

	public Component<?> getComponent(String id, IModel<BrixNode> pageNodeModel)
	{
		return getDelegate().getComponent(id, pageNodeModel);
	}

	public String getUniqueTagId()
	{
		return getDelegate().getUniqueTagId();
	}

}
