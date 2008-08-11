/**
 * 
 */
package brix.plugin.site.resource;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.Brix;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class ResourceRequestTarget implements IRequestTarget
{
    private final IModel<BrixNode> node;
    private final Boolean save;

    public ResourceRequestTarget(IModel<BrixNode> node)
    {
        super();
        this.node = node;
        this.save = null;
    }

    public ResourceRequestTarget(IModel<BrixNode> node, boolean save)
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
        
        response.setLastModifiedTime(Time.valueOf(node.getLastModified()));        
        
        try
        {   
        	HttpServletRequest r = ((WebRequest)requestCycle.getRequest()).getHttpServletRequest();
        	String since = r.getHeader("If-Modified-Since");
        	if (!save && since != null) 
        	{
        		Date d = new Date(r.getDateHeader("If-Modified-Since"));        	
        		Date m = node.getLastModified();
        		        	
        		// the weird toString comparison is to prevent comparing milliseconds
        		if (d.after(m) || d.toString().equals(m.toString()))
        		{        	
        			response.getHttpServletResponse().setStatus(304);        			
        			return;
        				
        		}        		
        	}
            node.writeData(requestCycle.getResponse().getOutputStream());
        }
        catch (Exception e)
        {
            log.error("Error writing resource data to content", e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ResourceRequestTarget.class);

    public static final String SAVE_PARAMETER = Brix.NS_PREFIX + "save";

}
