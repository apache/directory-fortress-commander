#
#   Licensed to the Apache Software Foundation (ASF) under one
#   or more contributor license agreements.  See the NOTICE file
#   distributed with this work for additional information
#   regarding copyright ownership.  The ASF licenses this file
#   to you under the Apache License, Version 2.0 (the
#   "License"); you may not use this file except in compliance
#   with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing,
#   software distributed under the License is distributed on an
#   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#   KIND, either express or implied.  See the License for the
#   specific language governing permissions and limitations
#   under the License.
#
___________________________________________________________________________________
###################################################################################
README for Fortress Web Application Installation
Last updated: February 10, 2015
___________________________________________________________________________________
###################################################################################
# SECTION 0.  Prerequisites for Fortress Web installation and usage
###################################################################################
a. Internet access to retrieve source code from Apache Fortress Rest GIT and binary dependencies from online Maven repo.

b. Git installed to target machine.

c. Java SDK Version 7 or beyond installed to target machine.

d. Apache Maven 3 installed to target machine.

e. Fortress Core installed to target machine.
(as described in README.txt located in the Apache Fortress Core package)

f. Fortress Realm installed to target machine.
(as described in README.txt located in the Apache Fortress Realm package)

g. Fortress enabled LDAP server installed to target environment.
(as described in README.txt located in the Apache Fortress Core package)

h. Apache Tomcat 7 or greater installed to target environment and Realm enabled.
(as described in REALM_CONTEXT_SETUP.txt or REALM_HOST_SETUP.txt located in the Apache Fortress Realm package).
_________________________________________________________________________________
###################################################################################
# SECTION 1:  Instructions to clone source from Fortress Web Git Repo:
###################################################################################
a. Clone the directory-fortress-commander from apache git repo:
# git clone https://git-wip-us.apache.org/repos/asf/directory-fortress-commander.git

b. Change directory to package home:
# cd directory-fortress-commander/
___________________________________________________________________________________
###################################################################################
# SECTION 2:  Instructions to build Fortress Web
###################################################################################
a. Open a command prompt on target machine in the root folder of the directory-fortress-commander package

b. Set java home and maven home.

c. Run maven install:
# mvn clean install -DskipTests
___________________________________________________________________________________
###################################################################################
# SECTION 3:  Obtain the fortress.properties
###################################################################################
Copy the fortress.properties, created during Fortress Core setup, to this package's resource folder.

# cp [directory-fortress-core]/config/fortress.properties [directory-fortress-commander]/src/main/resources

Where [directory-fortress-core] is base folder of the fortress core source package and [directory-fortress-commander] is the current package's home folder.
___________________________________________________________________________________
###################################################################################
# SECTION 4:  Load Fortress Web Security Policy
###################################################################################
Run maven install with load file:
# mvn install -Dload.file=./src/main/resources/FortressWebDemoUsers.xml -DskipTests=true

###################################################################################
# SECTION 5:  Instructions to Deploy Fortress Web application to Tomcat
###################################################################################
a. If Tomcat has global security enabled you must add credentials to pom.xml:

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

b. Copy the Fortress Realm Proxy jar file to Tomcat's server/lib folder.
        Place the fortress-realm proxy jar, generated by the [directory-fortress-realm] package into Tomcat server's /lib folder.  The proxy
           jar name is named fortress-realm-proxy-[version].jar and located here: [directory-fortress-realm]/proxy/target.

Where [directory-fortress-realm] is base folder of the fortress realm source package.

c. Restart Tomcat server.

Note: REALM_CONTEXT_SETUP.txt and REALM_HOST_SETUP.txt, located in directory-fortress-realm package describes the Tomcat Realm setup in more detail.

d. Enter maven command to deploy to Tomcat:
# mvn tomcat:deploy

e. To redeploy:
# mvn tomcat:redeploy
___________________________________________________________________________________
###################################################################################
# SECTION 6:  Instructions to test Fortress Web application using Solenium
###################################################################################
Run the Selenium Web driver integration tests:
# mvn verify -DskipTests=false -Dnoload

Note: This test case depends on:

a. SECTION 8 in directory-fortress-core README, 'Instructions to integration test using 'FortressJUnitTest'' has been executed.
This step needs test data loaded into the ldap server.

b. Sample Fortress Web security policy loaded in SECTION 4 of this document

c. Firefox is installed to target machine