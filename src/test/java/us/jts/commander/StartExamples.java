/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Separate startup class for people that want to run the examples directly. Use parameter
 * -Dcom.sun.management.jmxremote to startup JMX (and e.g. connect with jconsole).
 */
public class StartExamples
{
	/**
	 * Used for logging.
	 */

	/**
	 * Main function, starts the jetty server.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

        System.setProperty("version", "1.0-RC35");

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8081);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		//bb.setContextPath("/rbac");
        bb.setContextPath("/commander");
		bb.setWar("src/main/webapp");

        // Setup the test security realm, its name must match what's in the web.xml's 'realm-name' tag:
        HashLoginService dummyLoginService = new HashLoginService(
          "SentrySecurityRealm");
        dummyLoginService.setConfig("src/test/resources/jetty-users.properties");
        bb.getSecurityHandler().setLoginService( dummyLoginService );
		server.setHandler(bb);

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		server.getContainer().addEventListener(mBeanContainer);

		try
		{
			mBeanContainer.start();
			server.start();
			server.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(100);
		}
	}

	/**
	 * Construct.
	 */
	StartExamples()
	{
		super();
	}
}
