package brix.jcr;

import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.event.SaveEvent;
import brix.jcr.event.SaveEventListener;
import brix.jcr.wrapper.BrixNode;

public class JcrEventListener implements SaveEventListener
{

    public void onEvent(EventIterator events)
    {
        while (events.hasNext())
        {
            handleEvent(events.nextEvent());
        }
    }

    private void touch(JcrNode node)
    {
        if (node.isNodeType("nt:file") || node.isNodeType("nt:folder"))
        {
            new BrixNode(node.getDelegate(), node.getSession()).touch();
        }
    }

    private void handleSaveEvent(SaveEvent event)
    {
        JcrNode node = event.getNode();
        touch(node);
        if (node.getDepth() == 0 || node.isNodeType("nt:folder"))
        {
            // go through immediate children to determine if there are any modified ones
            JcrNodeIterator iterator = node.getNodes();
            while (iterator.hasNext())
            {
                JcrNode n = iterator.nextNode();
                if (n.isModified() || n.isNew())
                {
                    touch(n);
                }
                else if (n.hasNode("jcr:content") &&
                        (n.getNode("jcr:content").isModified() || n.getNode("jcr:content").isNew()))
                {
                    touch(n);
                }
            }
        }

    }

    private void handleEvent(Event event)
    {
        if (event instanceof SaveEvent)
        {
            handleSaveEvent((SaveEvent)event);
        }
    }

}
