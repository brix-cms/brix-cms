package brix.config;

import brix.Path;
import brix.registry.ExtensionPointRegistry;

public class BrixConfig
{
    private final ExtensionPointRegistry registry = new ExtensionPointRegistry();

    private AdminConfig adminConfig = new AdminConfig();

    private UriMapper uriMapper = new PrefixUriMapper(Path.ROOT);

    private int httpPort = 80;
    private int httpsPort = 443;


    public AdminConfig getAdminConfig()
    {
        return adminConfig;
    }

    public ExtensionPointRegistry getRegistry()
    {
        return registry;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public int getHttpsPort()
    {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort)
    {
        this.httpsPort = httpsPort;
    }

    public UriMapper getUriMapper()
    {
        return uriMapper;
    }

    public void setUriMapper(UriMapper uriMapper)
    {
        this.uriMapper = uriMapper;
    }


}
