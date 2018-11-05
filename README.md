   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.

# README for Apache Fortress Web
 * Version 2.0.3
 * Apache Fortress Web System Architecture Diagram
 ![Apache Fortress Web System Architecture](images/fortress-web-system-arch.png "Apache Fortress Web System Architecture")

-------------------------------------------------------------------------------
## Table of Contents

 * Document Overview
 * Tips for first-time users.
 * SECTION 1. Prerequisites
 * SECTION 2. Download & Install
 * SECTION 3. Get the fortress.properties
 * SECTION 4. Load Sample Security Policy
 * SECTION 5. Deploy to Tomcat Server
 * SECTION 6. Test with Selenium
 * SECTION 7. Fortress Web properties

___________________________________________________________________________________
## Document Overview

This document contains instructions to download, build, and test operations using Apache Fortress Web component.

___________________________________________________________________________________
##  Tips for first-time users

 * For a tutorial on how to use Apache Fortress check out the: [10 Minute Guide](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/doc-files/ten-minute-guide.html).
 * If you see **FORTRESS_CORE_HOME**, refer to the base package of [directory-fortress-core].
 * If you see **FORTRESS_REALM_HOME**, refer to the base package of [directory-fortress-realm].
 * If you see **FORTRESS_WEB_HOME**, refer to this packages base folder.
 * If you see **TOMCAT_HOME**, refer to the location of that package's base folder.
 * Questions about this software package should be directed to its mailing list:
   * http://mail-archives.apacheorg/mod_mbox/directory-fortress/

-------------------------------------------------------------------------------
## SECTION 1. Prerequisites

Minimum hardware requirements:
 * 2 Cores
 * 4GB RAM

Minimum software requirements:
 * Java SDK 8
 * git
 * Apache Maven3++
 * Apache Tomcat8++
 * Apache Fortress Core **Download & Install** in **FORTRESS_CORE_HOME** package **README.md**.
 * Apache Fortress Core **Options for using Apache Fortress and LDAP server** in **FORTRESS_CORE_HOME** package **README.md**.
 * Apache Fortress Realm **Download & Install** in **FORTRESS_REALM_HOME** package **README.md**.

Everything else covered in steps that follow.  Tested on Debian, Centos & Windows systems.

-------------------------------------------------------------------------------
## SECTION 2. Download & Install

Build the source.

 a. from git:
 ```
 git clone --branch 2.0.3 https://git-wip-us.apache.org/repos/asf/directory-fortress-commander.git
 cd directory-fortress-commander
 mvn clean install
 ```

 b. or download package:

 ```
 wget http://www.apache.org/dist/directory/fortress/dist/2.0.3/fortress-web-2.0.3-source-release.zip
 unzip fortress-web-2.0.3-source-release.zip
 cd fortress-web-2.0.3
 mvn clean install
 ```

___________________________________________________________________________________
## SECTION 3. Get the fortress.properties

These contain the coordinates to the target LDAP server.

1. Copy the **fortress.properties**, created during **FORTRESS_CORE_HOME** **README.md**, to this package's resource folder.

 ```
 cp $FORTRESS_CORE_HOME/config/fortress.properties $FORTRESS_WEB_HOME/src/main/resources
 ```

2. Verify they match your target LDAP server.
 ```
 # This param tells fortress what type of ldap server in use:
 ldap.server.type=apacheds

 # ldap host name
 host=localhost

 # if ApacheDS is listening on
 port=10389

 # If ApacheDS, these credentials are used for read/write to fortress DIT
 admin.user=uid=admin,ou=system
 admin.pw=secret

 # This is min/max settings for admin pool connections:
 min.admin.conn=1
 max.admin.conn=10

 # This node contains more fortress properties stored on behalf of connecting LDAP clients:
 config.realm=DEFAULT
 config.root=ou=Config,dc=example,dc=com

 # Used by application security components:
 perms.cached=true

 # Fortress uses a cache:
 ehcache.config.file=ehcache.xml

 # Default for pool reconnect flag is false:
 enable.pool.reconnect=true
 ```

___________________________________________________________________________________
## SECTION 4. Load Sample Security Policy

Run maven install with load file:
```
mvn install -Dload.file=./src/main/resources/FortressWebDemoUsers.xml
```

 Notes:
  * This step must be completed before tests can be successfully run.
  * The [DelegatedAdminManagerLoad](https://github.com/apache/directory-fortress-core/blob/master/ldap/setup/DelegatedAdminManagerLoad.xml) must also be loaded into LDAP, for base policy req's.

___________________________________________________________________________________
## SECTION 5. Deploy to Tomcat Server

1. If Tomcat has global security enabled you must add credentials to pom.xml:

 ```
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>tomcat-maven-plugin</artifactId>
        <version>${version.tomcat.maven.plugin}</version>
        <configuration>
            ...
          <!-- Warning the tomcat manager creds here are for deploying into a demo environment only. -->
          <username>tcmanager</username>
          <password>m@nager123</password>
        </configuration>
      </plugin>
 ```

2. copy **FORTRESS_REALM_HOME** proxy jar to **TOMCAT_HOME**/lib/

 ```
 cp $FORTRESS_REALM_HOME/proxy/target/fortress-realm-proxy-[version].jar $TOMCAT_HOME/lib
 ```

3. Restart Tomcat server.

4. Enter maven command to deploy to Tomcat:

 ```
 mvn tomcat:deploy
 ```

5. To redeploy:

 ```
 mvn tomcat:redeploy
 ```

6. Open browser and test (creds: test/password):

 ```
 http://hostname:8080/fortress-web
 ```

 where hostname is host or ip for your machine

___________________________________________________________________________________
## SECTION 6. Test with Selenium

1. There version of Webdriver being used for Selenium now requires downloading and installing the gecko (firefox) and/or chrome drivers as separate binary packages. For more info:
https://github.com/bonigarcia/webdrivermanager

2. Once that download has been completed, you will need to set the location of the driver as a system.property.
 For example, these lines may need to be added to the FortressWebSeleniumITCase test class, or set via other means:

 ```
     @BeforeClass
     public static void setupClass()
     {
         System.setProperty("webdriver.gecko.driver", "/home/user/drivers/geckodriver");
         System.setProperty( "webdriver.chrome.driver", "/home/user/drivers/chromedriver");
         ...
     }
 ```

2. Run the Selenium Web driver integration tests with Firefox (default):

 ```
 mvn test -Dtest=FortressWebSeleniumITCase
 ```

3. Run the tests using Chrome:

 ```
 mvn test -Dtest=FortressWebSeleniumITCase -Dweb.driver=chrome
 ```

 Note: These automated tests require that:
 * Either Firefox or Chrome installed to target machine.
 * **FORTRESS_CORE_HOME**/*FortressJUnitTest* successfully run.  This will load some test data to grind on.
 * **FORTRESS_CORE_HOME**/./setup/ldap/*FortressJUnitTest* successfully run.  This will load some test data to grind on.
 * [FortressWebDemoUsers](./src/main/resources/FortressWebDemoUsers.xml) policy loaded into target LDAP server.

___________________________________________________________________________________
## SECTION 7. Fortress Web properties

This section describes the properties needed to control fortress web.

1. LDAP Hostname coordinates.  The host name can be specified as a fully qualified domain name or IP address.

 ```
 # Host name and port of LDAP DIT:
 host=localhost
 port=10389
 ```

2. LDAP Server type.  Each LDAP server impl has different behavior on operations like password policies and audit.  If using a 3rd type of server that isn't formally supported, leave blank or type is other.

 ```
 # If ApacheDS server:
 ldap.server.type=apacheds
 ```

 ```
 # Else if OpenLDAP server:
 ldap.server.type=slapd
 ```

 ```
 # Else leave blank:
 #ldap.server.type=other
 ```

3.  Set the credentials of service account.  Must have read/write privileges over the Fortress LDAP DIT:

 ```
 # If ApacheDS it will look something like this:
 admin.user=uid=admin,ou=system
 admin.pw=secret
 ```

 ```
 # Else If OpenLDAP it will look something like this:
 admin.user=cn=Manager,dc=example,dc=com
 ```

4. Define the number of LDAP connections to use in the pool  This setting will be proportional to the number of concurrent users but won't be one-to-one.  The number of required ldap connections will be much lower than concurrent users:

 ```
 # This is min/max settings for LDAP connections.  For testing and low-volume instances this will work:
 min.admin.conn=1
 max.admin.conn=10
 ```

5. Give coordinates to the Config node that contains all of the other Fortress properties.  This will match your LDAP's server's config node per Fortress Core setup.

 ```
 # This node contains fortress properties stored on behalf of connecting LDAP clients:
 config.realm=DEFAULT
 config.root=ou=Config,dc=example,dc=com
 ```

6. If using LDAPS.

 ```
 # Used for SSL Connection to LDAP Server:
 enable.ldap.ssl=true
 enable.ldap.ssl.debug=true
 trust.store=/fully/qualified/path/and/file/name/to/java/truststore
 trust.store.password=changeit
 trust.store.set.prop=true
 ```

7. To use REST instead of LDAP.  Points to fortress-rest instance.

 ```
 # This will override default LDAP manager implementations for the RESTful ones:
 enable.mgr.impl.rest=true
 ```

8. If using REST, provide the credentials of user that has access to fortress-rest.

 ```
 # Optional parameters needed when Fortress client is connecting with the En Masse (rather than LDAP) server:
 http.user=demouser4
 http.pw=gX9JbCTxJW5RiH+otQEX0Ja0RIAoPBQf
 http.host=localhost
 http.port=8080
 ```

9. To reenable the nav panel on startup.

 ```
 # The default is 'false':
 enable.nav.panel=true
 ```

10. If using ApacheDS and setting password policies, point to the correction location.

 ```
 # ApacheDS stores its password policies objects here by default:
 apacheds.pwpolicy.root=ou=passwordPolicies,ads-interceptorId=authenticationInterceptor,ou=interceptors,ads-directoryServiceId=default,ou=config
 ```

11. The fortress web runtime will cache user's permissions in their session if set to true.

 ```
 # Used by application security components:
 perms.cached=true
 ```

12. Each instance of a fortress web can be scoped to one and only one tenant.  The default tenant is called HOME.

 ```
 # This is the default tenant or home context
 contextId=HOME
 ```

 ```
 # If you need to scope to a different tenant, supply its ID here:
 contextId=mytenantid
 ```

___________________________________________________________________________________
#### END OF README