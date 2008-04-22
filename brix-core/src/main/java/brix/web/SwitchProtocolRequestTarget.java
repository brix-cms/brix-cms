package brix.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;

import brix.jcr.wrapper.BrixNode.Protocol;

public class SwitchProtocolRequestTarget implements IRequestTarget
{

    private final Protocol protocol;

    public SwitchProtocolRequestTarget(Protocol protocol)
    {
        if (protocol == null)
        {
            throw new IllegalArgumentException("Argument 'protocol' may not be null.");
        }
        if (protocol == Protocol.PRESERVE_CURRENT)
        {
        	throw new IllegalArgumentException("Argument 'protocol' may not have value '" + Protocol.PRESERVE_CURRENT.toString() + "'.");
        }
        this.protocol = protocol;
    }

    public void detach(RequestCycle requestCycle)
    {

    }

    private String getUrl(String protocol, Integer port, HttpServletRequest request)
    {
        StringBuilder result = new StringBuilder();
        result.append(protocol);
        result.append("://");
        result.append(request.getServerName());
        if (port != null)
        {
            result.append(":");
            result.append(port);
        }
        result.append(request.getRequestURI());
        if (request.getQueryString() != null)
        {
            result.append("?");
            result.append(request.getQueryString());
        }
        return result.toString();
    }

    public void respond(RequestCycle requestCycle)
    {
        WebRequest webRequest = (WebRequest)requestCycle.getRequest();
        HttpServletRequest request = webRequest.getHttpServletRequest();

        BrixRequestCycleProcessor processor = (BrixRequestCycleProcessor)requestCycle
                .getProcessor();
        Integer port = null;
        if (protocol == Protocol.HTTP)
        {
            if (processor.getHttpPort() != 80)
            {
                port = processor.getHttpPort();
            }
        }
        else if (protocol == Protocol.HTTPS)
        {
            if (processor.getHttpsPort() != 443)
            {
                port = processor.getHttpsPort();
            }
        }

        String url = getUrl(protocol.toString().toLowerCase(), port, request);

        requestCycle.getResponse().redirect(url);
    }

    public static IRequestTarget requireProtocol(Protocol protocol)
    {
        RequestCycle requestCycle = RequestCycle.get();
        WebRequest webRequest = (WebRequest)requestCycle.getRequest();
        HttpServletRequest request = webRequest.getHttpServletRequest();
        if (protocol == null || protocol == Protocol.PRESERVE_CURRENT || 
        	request.getScheme().equals(protocol.toString().toLowerCase()))
        {
            return null;
        }
        else
        {
            return new SwitchProtocolRequestTarget(protocol);
        }
    }

}
