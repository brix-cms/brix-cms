package brix.config;

import brix.registry.PointRegistry;

public class BrixConfig
{
    private final PointRegistry registry = new PointRegistry();

    private AdminConfig adminConfig = new AdminConfig();

    public AdminConfig getAdminConfig()
    {
        return adminConfig;
    }

    public PointRegistry getRegistry()
    {
        return registry;
    }


}
