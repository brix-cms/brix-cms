package brix.plugin.site.resource.managers.image;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.resource.ResourceRequestTarget;

public class ViewImagePanel extends Panel<BrixNode>
{

	public ViewImagePanel(String id, IModel<BrixNode> model)
	{

		super(id, model);
		
        final ResourceBehavior behavior = new ResourceBehavior()
        {
            @Override
            IModel<BrixNode> getNodeModel()
            {
                return ViewImagePanel.this.getModel();
            }
        };

        add(new WebMarkupContainer<Void>("image")
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

    
}
