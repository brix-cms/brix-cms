package brix.rmiserver.jackrabbit;

import java.util.Properties;

import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.config.BeanConfig;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.LoginModuleConfig;

abstract class ExtendedLoginModuleConfig extends LoginModuleConfig
{
    private final LoginModuleConfig delegate;

    public ExtendedLoginModuleConfig(LoginModuleConfig delegate)
    {
        super(new BeanConfig("java.lang.String", new Properties()));
        this.delegate = delegate;
    }

    protected abstract LoginModule newLoginModule();

    @Override
    public LoginModule getLoginModule() throws ConfigurationException
    {
        return newLoginModule();
    }

    public boolean equals(Object arg0)
    {
        return delegate.equals(arg0);
    }

    public ClassLoader getClassLoader()
    {
        return delegate.getClassLoader();
    }

    public String getClassName()
    {
        return delegate.getClassName();
    }

    public Properties getParameters()
    {
        return delegate.getParameters();
    }

    public int hashCode()
    {
        return delegate.hashCode();
    }

    public Object newInstance() throws ConfigurationException
    {
        return newLoginModule();
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        delegate.setClassLoader(classLoader);
    }

    public String toString()
    {
        return delegate.toString();
    }


}