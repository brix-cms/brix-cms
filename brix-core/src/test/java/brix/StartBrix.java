package brix;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class StartBrix
{

    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);

        int port = Integer.getInteger("jetty.port", 80);
        connector.setPort(port);


        SslSocketConnector sslConnector = new SslSocketConnector();
        sslConnector.setMaxIdleTime(1000 * 60 * 60);
        sslConnector.setSoLingerTime(-1);
        sslConnector.setKeyPassword("password");
        sslConnector.setPassword("password");
        sslConnector.setKeystore("src/test/java/brix/keystore");

        port = Integer.getInteger("jetty.sslport", 443);
        sslConnector.setPort(port);


        server.setConnectors(new Connector[] { connector, sslConnector });

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

        try
        {
            System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();
            while (System.in.available() == 0)
            {
                Thread.sleep(5000);
            }
            server.stop();
            server.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
