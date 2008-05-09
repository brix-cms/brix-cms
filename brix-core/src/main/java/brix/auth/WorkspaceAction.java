package brix.auth;

public interface WorkspaceAction extends Action
{
    public enum Type {
        PUBLISH,
        VIEW
    };

    public Type getType();

    public String getWorkspaceName();
}
