package brix.rmiserver.boot;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import brix.rmiserver.Role;
import brix.rmiserver.User;
import brix.rmiserver.UserService;
import brix.rmiserver.UserService.UserDto;

public class Bootstrapper
{
    private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);

    private final DataSource datasource;
    private final PlatformTransactionManager transactionManager;
    private final Configuration configuration;
    private final SessionFactory sessionFactory;
    private final UserService userService;
    private final String workspaceManagerLogin;
    private final String workspaceManagerPassword;

    public Bootstrapper(DataSource datasource, PlatformTransactionManager transactionManager,
            Configuration configuration, SessionFactory sessionFactory, UserService userService,
            String workspaceManagerLogin, String workspaceManagerPassword)
    {
        this.datasource = datasource;
        this.configuration = configuration;
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.userService = userService;
        this.workspaceManagerLogin = workspaceManagerLogin;
        this.workspaceManagerPassword = workspaceManagerPassword;
    }


    public void bootstrap() throws Exception
    {
        logger.info("Bootstrapper executing");

        // create schema if necessary
        Session session = sessionFactory.openSession();
        try
        {
            session.createCriteria(User.class).setMaxResults(1).list();
            logger.info("Bootstrapper found schema, skipping schema creation");
        }
        catch (HibernateException e)
        {
            logger.info("Bootstrapper did not find schema");

            TransactionStatus txn = transactionManager
                .getTransaction(new DefaultTransactionDefinition());
            try
            {
                logger.info("creating schema...");
                bootstrapSchema();
                logger.info("creating default admin user...");
                bootstrapUsers();
            }
            finally
            {

                if (txn.isRollbackOnly())
                {
                    transactionManager.rollback(txn);
                }
                else
                {
                    transactionManager.commit(txn);
                }
            }
        }
        finally
        {
            session.close();
        }

        logger.info("Bootstrapper execution completed");

    }

    private void bootstrapUsers()
    {
        UserDto dto = new UserDto();
        dto.login = "admin";
        dto.password = "admin";
        dto.roles.add(Role.ADMIN);
        userService.create(dto);

        dto.login = workspaceManagerLogin;
        dto.password = workspaceManagerPassword;
        dto.locked = true;
        dto.roles.clear();
        dto.roles.add(Role.RMI);
        userService.create(dto);
    }

    /**
     * @param sf
     */
    private void bootstrapSchema()
    {
        Connection con = null;
        try
        {
            con = datasource.getConnection();
            Dialect dialect = Dialect.getDialect(configuration.getProperties());
            String[] schema = configuration.generateSchemaCreationScript(dialect);
            for (String stmt : schema)
            {
                Statement st = con.createStatement();
                st.executeUpdate(stmt);
                st.close();
            }
            con.commit();
        }
        catch (SQLException e1)
        {
            if (con != null)
            {
                try
                {
                    con.rollback();
                }
                catch (SQLException e2)
                {
                    try
                    {
                        con.close();
                    }
                    catch (SQLException e3)
                    {
                    }
                }
            }
        }

    }


}
