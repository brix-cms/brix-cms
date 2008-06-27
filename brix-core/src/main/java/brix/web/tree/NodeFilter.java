package brix.web.tree;

import java.io.Serializable;

import brix.jcr.wrapper.BrixNode;

public interface NodeFilter extends Serializable
{
	 public boolean isNodeAllowed(BrixNode node);
}
