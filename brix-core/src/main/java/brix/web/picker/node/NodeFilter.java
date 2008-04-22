package brix.web.picker.node;

import java.io.Serializable;

import brix.jcr.api.JcrNode;

public interface NodeFilter extends Serializable
{
    public boolean isNodeAllowed(JcrNode node);
}
