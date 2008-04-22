package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;

abstract class ChildNodeAdapter extends NodeAdapter
{

    private final String name;
    private final String newNodeType;
    private final String newNodeMixinTypes[];

    public ChildNodeAdapter(String name, String newNodeType, String newNodeMixinTypes[])
    {
        this.name = name;
        this.newNodeType = newNodeType;
        this.newNodeMixinTypes = newNodeMixinTypes;
    }

    public ChildNodeAdapter(String name)
    {
        this(name, null, null);
    }

    @Override
    JcrNode getNode()
    {
        NodeAdapter parent = getParent();
        JcrNode parentNode = parent.getNode();
        if (parentNode != null && parentNode.hasNode(name))
        {
            return parentNode.getNode(name);
        }
        else
        {
            return null;
        }
    }

    public String getName()
    {
        return name;
    }

    void create()
    {
        JcrNode parent = getParent().getNode();
        if (parent == null)
        {
            throw new IllegalStateException("Parent node may not be null during child creation.");
        }
        if (newNodeType == null)
        {
            throw new IllegalStateException("newNodeType must be set in order to create child node");
        }
        JcrNode node = parent.addNode(name, newNodeType);
        if (newNodeMixinTypes != null)
        {
            for (String s : newNodeMixinTypes)
            {
                node.addMixin(s);
            }
        }
    }

    abstract NodeAdapter getParent();

}
