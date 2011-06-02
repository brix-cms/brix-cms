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

package org.brixcms.rmiserver.workspacemanager;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

public class WorkspaceManagerFactoryBean extends AbstractFactoryBean {
    private RepositoryImpl repository;
    private String login;
    private String password;

    @Required
    public void setLogin(String login) {
        this.login = login;
    }

    @Required
    public void setPassword(String password) {
        this.password = password;
    }

    @Required
    public void setRepository(RepositoryImpl repository) {
        this.repository = repository;
    }


    @Override
    public Class getObjectType() {
        return JackrabbitWorkspaceManagerImpl.class;
    }

    @Override
    protected Object createInstance() throws Exception {
        Credentials simple = new SimpleCredentials(login, password.toCharArray());

        JackrabbitWorkspaceManagerImpl manager = new JackrabbitWorkspaceManagerImpl(repository,
                simple);
        manager.initialize();

        return manager;
    }
}
