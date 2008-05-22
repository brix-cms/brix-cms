package brix.config;

public class AdminConfig
{
    private boolean enableCodePress = true;
    private boolean enableWysiwyg = true;

    public boolean isEnableCodePress()
    {
        return enableCodePress;
    }

    public void setEnableCodePress(boolean enableCodePress)
    {
        this.enableCodePress = enableCodePress;
    }

    public boolean isEnableWysiwyg()
    {
        return enableWysiwyg;
    }

    public void setEnableWysiwyg(boolean enableWysiwyg)
    {
        this.enableWysiwyg = enableWysiwyg;
    }


}
