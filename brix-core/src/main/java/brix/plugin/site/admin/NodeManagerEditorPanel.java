package brix.plugin.site.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.ReferentialIntegrityException;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.Path;
import brix.auth.Action.Context;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;
import brix.web.util.PathLabel;

public class NodeManagerEditorPanel extends BrixGenericPanel<BrixNode>
{

	public NodeManagerEditorPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);

		String root = SitePlugin.get().getSiteRootPath();
		add(new PathLabel("path2", model, root)
		{
			@Override
			protected void onPathClicked(Path path)
			{
				BrixNode node = (BrixNode) getNode().getSession().getItem(path.toString());
				selectNode(node, false);
			}
		});

		add(new Link<Void>("rename")
		{
			@Override
			public void onClick()
			{
				String id = NodeManagerEditorPanel.this.getId();
				Panel renamePanel = new RenamePanel(id, NodeManagerEditorPanel.this.getModel())
				{
					@Override
					protected void onLeave()
					{
						SitePlugin.get().refreshNavigationTree(this);
						replaceWith(NodeManagerEditorPanel.this);
					}
				};
				NodeManagerEditorPanel.this.replaceWith(renamePanel);
			}

			@Override
			public boolean isVisible()
			{				
				BrixNode node = NodeManagerEditorPanel.this.getModelObject();
				String path = node.getPath();
				String web = SitePlugin.get().getSiteRootPath();
				Brix brix = node.getBrix();
				return SitePlugin.get().canRenameNode(node, Context.ADMINISTRATION) && path.length() > web.length()
						&& path.startsWith(web);
			}
		});

		add(new Link<Void>("makeVersionable")
		{
			@Override
			public void onClick()
			{
				if (!getNode().isNodeType("mix:versionable"))
				{
					getNode().addMixin("mix:versionable");
					getNode().save();
					getNode().checkin();
				}
			}

			@Override
			public boolean isVisible()
			{
				if (true)
				{
					// TODO: Implement proper versioning support!
					return false;
				}				

				return getNode() != null && getNode().isNodeType("nt:file") && !getNode().isNodeType("mix:versionable")
						&& SitePlugin.get().canEditNode(getNode(), Context.ADMINISTRATION);
			}
		});

		add(new Link<Void>("delete")
		{

			@Override
			public void onClick()
			{
				BrixNode node = getNode();
				BrixNode parent = (BrixNode) node.getParent();

				node.remove();
				try
				{
					parent.save();
					selectNode(parent, true);
				}
				catch (JcrException e)
				{
					if (e.getCause() instanceof ReferentialIntegrityException)
					{
						parent.getSession().refresh(false);
						NodeManagerEditorPanel.this.getModel().detach();
						// parent.refresh(false);
						selectNode(NodeManagerEditorPanel.this.getModelObject(), true);
						getSession().error(NodeManagerEditorPanel.this.getString("referenceIntegrityError"));
					}
					else
					{
						throw e;
					}
				}
			}

			@Override
			public boolean isVisible()
			{
				return SitePlugin.get().canDeleteNode(getNode(), Context.ADMINISTRATION);
			}

		});

		add(new SessionFeedbackPanel("sessionFeedback"));

		add(new NodeManagerTabbedPanel("tabbedPanel", getTabs(getModel())));
	}

	public BrixNode getNode()
	{
		return getModelObject();
	}

	private void selectNode(BrixNode node, boolean refresh)
	{
		SitePlugin.get().selectNode(this, node, refresh);
	}

	private List<ITab> getTabs(IModel<BrixNode> nodeModel)
	{
		Collection<ManageNodeTabFactory> factories = nodeModel.getObject().getBrix().getConfig().getRegistry()
				.lookupCollection(ManageNodeTabFactory.POINT);
		if (factories != null && !factories.isEmpty())
		{
			int tabCount = 0;
			class Entry
			{
				ManageNodeTabFactory factory;
				List<ITab> tabs;
			}
			;
			List<Entry> list = new ArrayList<Entry>();
			for (ManageNodeTabFactory f : factories)
			{
				List<ITab> tabs = f.getManageNodeTabs(nodeModel);
				if (tabs != null && !tabs.isEmpty())
				{
					Entry e = new Entry();
					e.factory = f;
					e.tabs = tabs;
					tabCount += tabs.size();
					list.add(e);
				}
			}
			Collections.sort(list, new Comparator<Entry>()
			{
				public int compare(Entry o1, Entry o2)
				{
					return o2.factory.getPriority() - o1.factory.getPriority();
				}
			});
			List<ITab> result = new ArrayList<ITab>(tabCount);
			for (Entry e : list)
			{
				result.addAll(e.tabs);
			}
			return result;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	private static class SessionFeedbackPanel extends FeedbackPanel
	{

		public SessionFeedbackPanel(String id)
		{
			super(id, new Filter());
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean isVisible()
		{
			List messages = (List) getFeedbackMessagesModel().getObject();
			return messages != null && !messages.isEmpty();
		}

		private static class Filter implements IFeedbackMessageFilter
		{
			public boolean accept(FeedbackMessage message)
			{
				return message.getReporter() == null;
			}
		};
	};
}
