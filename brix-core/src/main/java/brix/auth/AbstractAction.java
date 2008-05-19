package brix.auth;

public abstract class AbstractAction implements Action
{
    private final Context context;
    
    public AbstractAction(Context context)
    {
        this.context = context;
    }
    
    public Context getContext()
    {
        return context;
    }
}
