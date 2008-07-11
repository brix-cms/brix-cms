package brix.jcr.base;

import javax.jcr.observation.Event;

import brix.jcr.api.JcrNode;

public interface SaveEvent extends Event
{

    public static final int NODE_SAVE = 4096;

    JcrNode getNode();

}
