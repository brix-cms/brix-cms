package brix.plugin.site.webdav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;

public class RulesNode extends BrixNode
{

	private static final String RULES = "rules";

	public RulesNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	public List<Rule> getRules(boolean sortByPriority)
	{
		if (!hasNode(RULES))
		{
			return Collections.emptyList();
		}
		else
		{
			JcrNode rules = getNode(RULES);
			List<Rule> result = new ArrayList<Rule>();
			JcrNodeIterator n = rules.getNodes();
			while (n.hasNext())
			{
				Rule rule = Rule.load(n.nextNode());
				result.add(rule);
			}

			if (sortByPriority)
			{
				Collections.sort(result, new Comparator<Rule>() {
					public int compare(Rule o1, Rule o2)
					{
						return o2.getPriority() - o1.getPriority();
					}
				});
			}
			else
			{
				Collections.sort(result, new Comparator<Rule>() {
					public int compare(Rule o1, Rule o2)
					{
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
			}

			return result;
		}
	}

	public Rule getRule(String name)
	{
		if (!hasNode(RULES))
		{
			return null;
		}
		else
		{
			JcrNode parent = getNode(RULES);
			if (parent.hasNode(name))
			{
				Rule rule = Rule.load(parent.getNode(name));				
				return rule;
			}
			else
			{
				return null;
			}
		}
	}
	
	public void removeRule(Rule rule)
	{
		if (hasNode(RULES))
		{
			JcrNode parent = getNode(RULES);
			if (parent.hasNode(rule.getName()))
			{
				parent.getNode(rule.getName()).remove();
				parent.save();
			}
		}
	}

	public void saveRule(Rule rule)
	{
		JcrNode parent;
		if (!hasNode(RULES))
		{
			parent = addNode(RULES, "nt:unstructured");
		}
		else
		{
			parent = getNode(RULES);
		}
		rule.save(parent);
		save();
	}

	@Override
	public String getUserVisibleName()
	{
		return "WebDAV Rules";
	}

	public static RulesNode initialize(BrixNode node)
	{
		node.setNodeType(TYPE);
		return new RulesNode(node.getDelegate(), node.getSession());
	}

	public static final String TYPE = Brix.NS_PREFIX + "webDavContainer";

	public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
	{
		@Override
		public boolean canWrap(Brix brix, JcrNode node)
		{
			return TYPE.equals(getNodeType(node));
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session)
		{
			return new RulesNode(node, session);
		}

		@Override
		public void initializeRepository(Brix brix, Session session)
		{
			RepositoryUtil.registerMixinType(session.getWorkspace(), TYPE, false, false);
		}
	};

}
