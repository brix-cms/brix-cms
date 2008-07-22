package brix;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.demo.ApplicationProperties;
import brix.demo.util.FileUtils;

public class StartBrixDemo
{
    private static final Logger logger = LoggerFactory.getLogger(StartBrixDemo.class);

    public static void main(String[] args) throws Exception
    {
        ApplicationProperties properties = new ApplicationProperties();

        // setup temorary webapp folder
        File webapp = new File(FileUtils.getDefaultWebAppFileName());
        if (!webapp.exists())
        {
            logger.info("Initializing webapp directory in: {}", webapp.getAbsolutePath());
            File webinf = new File(webapp + "/WEB-INF");
            FileUtils.mkdirs(webinf);
            FileUtils.copyClassResourceToFile("/brix/demo/webapp/webinf/web.xml", new File(webinf,
                "web.xml"));
            FileUtils.copyClassResourceToFile("/brix/demo/webapp/webinf/config.xml", new File(
                webinf, "config.xml"));
            FileUtils.copyClassResourceToFile("/brix/demo/webapp/webinf/keystore", new File(webinf, "keystore"));
        }


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
        sslConnector.setKeystore(new File(webapp, "WEB-INF/keystore").getAbsolutePath());

        port = Integer.getInteger("jetty.sslport", properties.getHttpsPort());
        sslConnector.setPort(port);


        server.setConnectors(new Connector[] { connector, sslConnector });

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath("/");
        bb.setWar(webapp.getAbsolutePath());


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
