/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.fortress.web;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

import org.apache.directory.fortress.web.common.GlobalIds;
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

		// fortress-rest navigation (iff enabled in fortress.properties)
        System.setProperty("version", "2.0.5");
        System.setProperty( GlobalIds.IS_JETTY_SERVER, "true");

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8081);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		//bb.setContextPath("/rbac");
        bb.setContextPath("/fortress-web");
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
