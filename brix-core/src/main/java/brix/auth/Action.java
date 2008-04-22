package brix.auth;

public interface Action
{

    public enum Context {
        ADMINISTRATION,
        PRESENTATION,
        WEBDAV
        /* TODO: Implement action checks for webdav */
    };

    public Context getContext();

}
