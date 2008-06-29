package brix.plugin.webdavurl;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;

import brix.web.generic.BrixGenericPanel;

public class WebdavUrlPanel extends BrixGenericPanel<String>
{

	private static final long serialVersionUID = 1L;

	public WebdavUrlPanel(String id, IModel<String> model)
	{
		super(id, model);

		add(new WebMarkupContainer("webdav")
		{
			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				tag.put("href", getWorkspaceUrl("webdav", getWorkspaceId()));
			}
		});

		add(new WebMarkupContainer("jcrwebdav")
		{
			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				tag.put("href", getWorkspaceUrl("jcrwebdav", getWorkspaceId()));
			}
		});

	}

	private String getWorkspaceUrl(String type, String workspaceId)
	{
		HttpServletRequest request = ((WebRequest) getRequest()).getHttpServletRequest();
		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(request.getServerName());
		if (request.getServerPort() != 80)
		{
			url.append(":");
			url.append(request.getServerPort());
		}
		url.append("/");
		url.append(type);
		url.append("/");
		url.append(workspaceId);

		return url.toString();
	}

	private String getWorkspaceId()
	{
		return getModelObject();
	}
}
