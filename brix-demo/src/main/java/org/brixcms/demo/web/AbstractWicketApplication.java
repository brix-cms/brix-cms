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

package org.brixcms.demo.web;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.brixcms.demo.ApplicationProperties;
import org.brixcms.jcr.ThreadLocalSessionFactory;
import org.brixcms.util.JcrUtils;
import org.brixcms.workspace.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;

/**
 * Factors out noise not necessary to demonstrating how to install Brix into a Wicket application. This class takes care
 * of peripheral duties such as creating the Jcr repository, setting up JcrSessionFactory, etc.
 *
 * @author igor.vaynberg
 */
public abstract class AbstractWicketApplication extends WebApplication {
// ------------------------------ FIELDS ------------------------------

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractWicketApplication.class);

    /**
     * application properties
     */
    private ApplicationProperties properties;
    /**
     * jcr repository
     */
    private Repository repository;

    /**
     * jcr session factory. sessions created by this factory are cleaned up by {@link WicketRequestCycle}
     */
    private ThreadLocalSessionFactory sessionFactory;

    /**
     * workspace manager to be used by brix
     */
    private WorkspaceManager workspaceManager;

// -------------------------- STATIC METHODS --------------------------

    /**
     * @return application instance
     */
    public static AbstractWicketApplication get() {
        return (AbstractWicketApplication) WebApplication.get();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * @return application properties
     */
    public final ApplicationProperties getProperties() {
        return properties;
    }

    /**
     * @return jcr repository
     */
    public final Repository getRepository() {
        return repository;
    }

    /**
     * @return workspace manager
     */
    public final WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * @return jcr session factory
     */
    public final ThreadLocalSessionFactory getJcrSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected void init() {
        super.init();
        // read application properties
        properties = new ApplicationProperties("brix.demo");

        logger.info("Using JCR repository url: " + properties.getJcrRepositoryUrl());

        // create jcr repository
        repository = JcrUtils.createRepository(properties.getJcrRepositoryUrl());

        // create session factory that will be used to feed brix jcr sessions
        sessionFactory = new ThreadLocalSessionFactory(repository, properties
                .buildSimpleCredentials());

        try {
            // create workspace manager brix will use to access workspace-related functionality
            workspaceManager = JcrUtils.createWorkspaceManager(properties.getWorkspaceManagerUrl(),
                    sessionFactory);
        } finally {
            // since creating workspace manager may require access to session we need to clean up
            cleanupSessionFactory();
        }

        getMarkupSettings().setStripWicketTags(true);
    }

    /**
     * cleans up any opened sessions in session factory
     */
    public final void cleanupSessionFactory() {
        sessionFactory.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RequestCycle newRequestCycle(Request request, Response response) {
        // install request cycle that will cleanup #sessionFactory at the end of request
        return new WicketRequestCycle(this, (WebRequest) request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // shutdown the repository cleanly
        if (repository instanceof RepositoryImpl) {
            logger.info("Shutting down JackRabbit repository...");
            ((RepositoryImpl) repository).shutdown();
        }
        super.onDestroy();
    }
}
