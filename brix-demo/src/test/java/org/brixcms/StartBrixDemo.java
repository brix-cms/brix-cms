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

package org.brixcms;

import org.brixcms.demo.ApplicationProperties;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartBrixDemo {
    private static final Logger logger = LoggerFactory.getLogger(StartBrixDemo.class);

    public static void main(String[] args) throws Exception {
        ApplicationProperties properties = new ApplicationProperties("brix.demo");

        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);

        int port = Integer.getInteger("jetty.port", properties.getHttpPort());
        connector.setPort(port);


        SslSocketConnector sslConnector = new SslSocketConnector();
        sslConnector.setMaxIdleTime(1000 * 60 * 60);
        sslConnector.setSoLingerTime(-1);
        sslConnector.setKeyPassword("password");
        sslConnector.setPassword("password");
        sslConnector.setKeystore("src/main/webapp/WEB-INF/keystore");

        port = Integer.getInteger("jetty.sslport", properties.getHttpsPort());
        sslConnector.setPort(port);


        server.setConnectors(new Connector[]{connector, sslConnector});

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath("/");
        bb.setWar("src/main/webapp");


        // START JMX SERVER
        // MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        // server.getContainer().addEventListener(mBeanContainer);
        // mBeanContainer.start();

        server.addHandler(bb);

        try {
            System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
