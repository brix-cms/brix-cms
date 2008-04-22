/**
 * 
 */
package brix.plugin.site.node.resource;

import java.io.IOException;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;

public class ResourceRequestTarget implements IRequestTarget
{
    private final IModel<JcrNode> node;
    private final Boolean save;

    public ResourceRequestTarget(IModel<JcrNode> node)
    {
        super();
        this.node = node;
        this.save = null;
    }

    public ResourceRequestTarget(IModel<JcrNode> node, boolean save)
    {
        super();
        this.node = node;
        this.save = save;
    }

    public void detach(RequestCycle requestCycle)
    {
        node.detach();
    }

    public void respond(RequestCycle requestCycle)
    {
        boolean save = (this.save != null) ? this.save : Strings.isTrue(requestCycle.getRequest()
                .getParameter(SAVE_PARAMETER));

        BrixFileNode node = (BrixFileNode)this.node.getObject();

        WebResponse response = (WebResponse)requestCycle.getResponse();

        response.setContentType(node.getMimeType());

        if (save)
        {
            response.setAttachmentHeader(node.getName());
        }

        try
        {
            node.writeData(requestCycle.getResponse().getOutputStream());
        }
        catch (IOException e)
        {
            log.error("Error writing resource data to content", e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ResourceRequestTarget.class);

    public static final String SAVE_PARAMETER = Brix.NS_PREFIX + "save";

}