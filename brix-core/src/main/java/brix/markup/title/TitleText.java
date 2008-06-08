package brix.markup.title;

import brix.BrixNodeModel;
import brix.markup.tag.Text;
import brix.plugin.site.page.AbstractContainer;

public class TitleText implements Text
{
	private final BrixNodeModel nodeModel;

	public TitleText(AbstractContainer container)
	{
		nodeModel = new BrixNodeModel(container);
		nodeModel.detach();
	}

	public String getText()
	{
		AbstractContainer container = (AbstractContainer) new BrixNodeModel(nodeModel).getObject();

		String title = null;
		while (title == null && container != null)
		{
			title = container.getTitle();
			container = container.getTemplate();
		}
		
		return title != null ? title : "";
	}

}
