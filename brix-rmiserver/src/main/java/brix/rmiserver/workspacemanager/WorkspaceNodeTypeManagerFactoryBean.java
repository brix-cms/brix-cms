package brix.rmiserver.workspacemanager;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import brix.workspace.WorkspaceNodeTypeManager;

public class WorkspaceNodeTypeManagerFactoryBean extends AbstractFactoryBean
{
    private RepositoryImpl repository;
    private String login;
    private String password;

    @Required
    public void setRepository(RepositoryImpl repository)
    {
        this.repository = repository;
    }

    @Required
    public void setLogin(String login)
    {
        this.login = login;
    }

    @Required
    public void setPassword(String password)
    {
        this.password = password;
    }


    @Override
    protected Object createInstance() throws Exception
    {
        Credentials creds = new SimpleCredentials(login, password.toCharArray());
        return new JackrabbitWorkspaceNodeTypeManager(repository, creds);
    }

    @Override
    public Class getObjectType()
    {
        return WorkspaceNodeTypeManager.class;
    }

}
