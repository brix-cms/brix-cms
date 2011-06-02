/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.rmiserver.boot;

import org.brixcms.rmiserver.UserService;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class BootstrapperFactoryBean implements ApplicationContextAware, InitializingBean {
    private ApplicationContext ctx;

    private SessionFactory sessionFactory;

    private UserService userService;
    private PlatformTransactionManager transactionManager;
    private DataSource dataSource;

    private String workspaceManagerLogin;
    private String workspaceManagerPassword;

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setWorkspaceManagerLogin(String workspaceManagerLogin) {
        this.workspaceManagerLogin = workspaceManagerLogin;
    }

    @Required
    public void setWorkspaceManagerPassword(String workspaceManagerPassword) {
        this.workspaceManagerPassword = workspaceManagerPassword;
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


    public void afterPropertiesSet() throws Exception {
        LocalSessionFactoryBean hsf = (LocalSessionFactoryBean) BeanFactoryUtils
                .beanOfTypeIncludingAncestors(ctx, LocalSessionFactoryBean.class);
        Bootstrapper bootstrapper = new Bootstrapper(dataSource, transactionManager, hsf
                .getConfiguration(), sessionFactory, userService, workspaceManagerLogin,
                workspaceManagerPassword);
        bootstrapper.bootstrap();
    }
}
