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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrSession.Behavior;
import org.brixcms.jcr.exception.JcrException;

import javax.jcr.RepositoryException;

/**
 * @author Matej Knopp
 */
abstract class AbstractWrapper {
    private final Object delegate;
    private final JcrSession session;

    protected AbstractWrapper(Object delegate, JcrSession session) {
        if (delegate == null) {
            throw new IllegalArgumentException("Argument 'delegate' may not be null.");
        }
        this.delegate = delegate;
        this.session = session;
    }

    public Object getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractWrapper == false) {
            return false;
        }
        AbstractWrapper that = (AbstractWrapper) obj;

        return delegate == that.delegate || delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

// -------------------------- OTHER METHODS --------------------------
    protected <T> T executeCallback(Callback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Argument 'callback' may not be null.");
        }
        try {
            return callback.execute();
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    protected void handleException(Exception e) {
        // TODO: This is definitely not what we want to do.
        // Inspect the exception and register flash messages for certain
        // exceptions (versioning, locking, ...)

        Behavior behavior = getJcrSession().getBehavior();

        if (behavior != null) {
            behavior.handleException(e);
        } else {
            if (e instanceof RepositoryException) {
                throw new JcrException((RepositoryException) e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    protected void executeCallback(VoidCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Argument 'callback' may not be null.");
        }
        try {
            callback.execute();
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected JcrSession getJcrSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    protected <T> T unwrap(T wrapper) {
        while (wrapper instanceof AbstractWrapper) {
            wrapper = (T) ((AbstractWrapper) wrapper).getDelegate();
        }
        return wrapper;
    }

    public <T> T[] unwrap(T original[], T newArray[]) {
        for (int i = 0; i < original.length; ++i) {
            newArray[i] = unwrap(original[i]);
        }
        return newArray;
    }

    protected interface Callback<T> {
        public T execute() throws Exception;
    }

    protected interface VoidCallback {
        public void execute() throws Exception;
    }
}
