package brix.plugin.site.resource.managers;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.resource.ResourceManager;
import brix.plugin.site.resource.ResourceRequestTarget;

public class ImageResourceManager implements ResourceManager
{

    public ImageResourceManager()
    {

    }

    public boolean handles(String mimeType)
    {
        return mimeType.equals("image/jpeg") || mimeType.equals("image/gif") ||
                mimeType.equals("image/png");
    }

    public boolean hasEditor()
    {
        return false;
    }

    public boolean hasViewer()
    {
        return true;
    }

    public Panel newEditor(String id, IModel<BrixNode> nodeModel)
    {
        throw new UnsupportedOperationException();
    }

    public Panel newViewer(String id, IModel<BrixNode> nodeModel)
    {
        return new ViewerPanel(id, nodeModel);
    }

    private static class ViewerPanel extends Panel
    {

        private abstract class ResourceBehavior extends AbstractBehavior
                implements
                    IBehaviorListener
        {
            public void onRequest()
            {
                getRequestCycle().setRequestTarget(new ResourceRequestTarget(getNodeModel()));
            }

            abstract IModel<BrixNode> getNodeModel();
        };

        public ViewerPanel(String id, final IModel<BrixNode> nodeModel)
        {
            super(id, nodeModel);

            final ResourceBehavior behavior = new ResourceBehavior()
            {
                @Override
                IModel<BrixNode> getNodeModel()
                {
                    return nodeModel;
                }
            };

            add(new WebMarkupContainer("image")
            {
                @Override
                protected void onComponentTag(ComponentTag tag)
                {
                    CharSequence url = getRequestCycle().urlFor(this, behavior,
                            IBehaviorListener.INTERFACE);
                    tag.put("src", url);
                    super.onComponentTag(tag);
                }
            }.add(behavior));
        }

    };

}
