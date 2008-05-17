package brix.rmiserver.boot;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import brix.rmiserver.UserService;

public class BootstrapperFactoryBean implements ApplicationContextAware, InitializingBean
{
    private ApplicationContext ctx;

    private SessionFactory sessionFactory;

    private UserService userService;
    private PlatformTransactionManager transactionManager;
    private DataSource dataSource;

    private String workspaceManagerLogin;
    private String workspaceManagerPassword;


    @Required
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Required
    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    @Required
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Required
    public void setWorkspaceManagerLogin(String workspaceManagerLogin)
    {
        this.workspaceManagerLogin = workspaceManagerLogin;
    }

    @Required
    public void setWorkspaceManagerPassword(String workspaceManagerPassword)
    {
        this.workspaceManagerPassword = workspaceManagerPassword;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;
    }

    public void afterPropertiesSet() throws Exception
    {
        LocalSessionFactoryBean hsf = (LocalSessionFactoryBean)BeanFactoryUtils
                .beanOfTypeIncludingAncestors(ctx, LocalSessionFactoryBean.class);
        Bootstrapper bootstrapper = new Bootstrapper(dataSource, transactionManager, hsf
                .getConfiguration(), sessionFactory, userService, workspaceManagerLogin,
                workspaceManagerPassword);
        bootstrapper.bootstrap();
    }

}
