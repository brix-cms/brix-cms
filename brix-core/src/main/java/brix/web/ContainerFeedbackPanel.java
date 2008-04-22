package brix.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class ContainerFeedbackPanel extends FeedbackPanel
{

    public ContainerFeedbackPanel(String id, MarkupContainer container)
    {
        super(id, new ContainerFeedbackMessageFilter(container));
    }

}
