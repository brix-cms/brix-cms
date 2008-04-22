package brix.auth;

public interface AuthorizationStrategy
{

    public boolean isActionAuthorized(Action action);

}
