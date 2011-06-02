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

package org.brixcms.rmiserver.jackrabbit;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.brixcms.rmiserver.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.security.auth.spi.LoginModule;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class RepositoryFactoryBean implements FactoryBean, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryFactoryBean.class);

    private RepositoryImpl repository;
    private String repositoryHomeDir;
    private Authorizer authorizer;
    private Resource repositoryConfig;

    @Required
    public void setRepositoryConfig(Resource repositoryConfig) {
        this.repositoryConfig = repositoryConfig;
    }

    @Required
    public void setRepositoryHomeDir(String repositoryHomeDir) {
        this.repositoryHomeDir = repositoryHomeDir;
    }



    public void destroy() throws Exception {
        if (repository != null) {
            synchronized (this) {
                if (repository != null) {
                    destroyRepositoryInstance();
                }
            }
        }
    }

    public Object getObject() throws Exception {
        if (repository == null) {
            synchronized (this) {
                if (repository == null) {
                    createRepositoryInstance();
                }
            }
        }
        return repository;
    }

    public Class getObjectType() {
        return Repository.class;
    }

    public boolean isSingleton() {
        return true;
    }


    public void afterPropertiesSet() throws Exception {
        // eagerly create repo
        getObject();
    }

    private void createRepositoryInstance() {
        logger.info("Initializing JackRabbit repository at {}", repositoryHomeDir);

        RepositoryConfig config = loadRepositoryConfig();

        // install config hack to allow custom LoginModule instances
        ExtendedRepositoryConfig config2 = new ExtendedRepositoryConfig(config) {
            @Override
            protected LoginModule newLoginModule() {
                return new ServerLoginModule(authorizer);
            }
        };

        // create repository
        try {
            repository = RepositoryImpl.create(config2);
        } catch (RepositoryException e) {
            throw new RuntimeException("Could not create JackRabbit repository", e);
        }
    }

    private RepositoryConfig loadRepositoryConfig() {
        RepositoryConfig config = null;
        InputStream fis = null;
        try {
            fis = repositoryConfig.getInputStream();
            config = RepositoryConfig.create(fis, repositoryHomeDir);
        } catch (Exception e) {
            throw new RuntimeException("Could not configure JackRabbit repository", e);
        } finally {
            close(fis);
        }
        return config;
    }

    public static final void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not close stream", e);
            }
        }
    }

    private void destroyRepositoryInstance() {
        logger.info("Shutting down JackRabbit repository");
        repository.shutdown();
    }

    @Required
    public void setUserService(UserService userService) {
        authorizer = new Authorizer(userService);
    }
}
