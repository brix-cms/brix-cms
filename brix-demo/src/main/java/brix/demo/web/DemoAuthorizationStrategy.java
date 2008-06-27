package brix.demo.web;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;

/**
 * Implementation of {@link AuthorizationStrategy} that allows everything for the purposes of the
 * demo
 * 
 * @author ivaynberg
 * 
 */
public class DemoAuthorizationStrategy implements AuthorizationStrategy
{

    /** {@inheritDoc} */
    public boolean isActionAuthorized(Action action)
    {
        return true;
    }

}
