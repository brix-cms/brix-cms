package brix.web;

import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.util.string.Strings;

import brix.web.reference.Reference;

public class ReferenceRequestTarget extends RedirectRequestTarget
{

    public ReferenceRequestTarget(Reference reference)
    {
        super(referenceToUrl(reference));
    }

    private static String referenceToUrl(Reference reference)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException("reference cannot be null");
        }
        String url = reference.generateUrl();
        if (Strings.isEmpty(url))
        {
            url = "/";
        }
        return url;
    }

}
