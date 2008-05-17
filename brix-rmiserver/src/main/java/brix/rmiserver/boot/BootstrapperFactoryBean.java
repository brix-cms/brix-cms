package brix.rmiserver.boot;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
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

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }


    public void setWorkspaceManagerLogin(String workspaceManagerLogin)
    {
        this.workspaceManagerLogin = workspaceManagerLogin;
    }

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
