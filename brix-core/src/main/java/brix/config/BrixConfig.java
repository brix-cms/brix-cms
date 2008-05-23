package brix.config;

import brix.registry.ExtensionPointRegistry;

public class BrixConfig
{
    private final ExtensionPointRegistry registry = new ExtensionPointRegistry();

    private AdminConfig adminConfig = new AdminConfig();

    public AdminConfig getAdminConfig()
    {
        return adminConfig;
    }

    public ExtensionPointRegistry getRegistry()
    {
        return registry;
    }


}
