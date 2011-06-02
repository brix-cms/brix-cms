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

package org.brixcms.jcr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Repository;

public class ThreadLocalSessionFactory extends AbstractThreadLocalSessionFactory
        implements
        JcrSessionFactory {
    static final Logger logger = LoggerFactory.getLogger(ThreadLocalSessionFactory.class);

    private final Repository repository;
    private final Credentials credentials;

    public ThreadLocalSessionFactory(Repository repository, Credentials credentials) {
        if (repository == null) {
            throw new IllegalArgumentException("repository cannot be null");
        }

        if (credentials == null) {
            throw new IllegalArgumentException("credentials cannot be null");
        }
        this.credentials = credentials;
        this.repository = repository;
    }

    @Override
    protected Credentials getCredentials() {
        return credentials;
    }

    @Override
    protected Repository getRepository() {
        return repository;
    }
}
