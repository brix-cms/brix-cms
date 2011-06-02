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

package org.brixcms.util;

import org.apache.jackrabbit.rmi.client.ClientAdapterFactory;
import org.apache.jackrabbit.rmi.client.ClientRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Nov 15, 2010 2:09:29 PM
 */

public class RmiRepositoryFactory {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RmiRepositoryFactory.class);

    static Repository getRmiRepository(String url) throws MalformedURLException, NotBoundException, RemoteException {
        ClientRepositoryFactory factory = new ClientRepositoryFactory(new ClientAdapterFactory());
        return factory.getRepository(url);
    }
}
