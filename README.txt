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
Last updated: February 5, 2015
___________________________________________________________________________________
###################################################################################
# SECTION 0.  Prerequisites for Fortress Web installation and usage
###################################################################################
a. Internet access to retrieve source code from Apache Fortress Web GIT and binary dependencies from online Maven repo.

b. Java SDK Version 7 or beyond installed to target environment

c. Apache Maven installed to target environment

d. LDAP server installed.  (see README in Apache Fortress Core)

e. Apache Tomcat 7 or greater installed to local machine.

f. directory-fortress-realm proxy jar loaded into Tomcat server's /lib folder -
    The README.txt from directory-fortress-realm package provides instructions for obtaining.
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

b. Set java home:
# export JAVA_HOME=...

c. Set maven home:
# export M2_HOME=...

d. Run maven install:
# $M2_HOME/bin/mvn clean install -DskipTests
___________________________________________________________________________________
###################################################################################
# SECTION 3:  Obtain the fortress.properties
###################################################################################

Copy the fortress.properties, created during [directory-fortress-core] setup, to this package's resource folder.

# cp [directory-fortress-core]/config/fortress.properties [directory-fortress-commander]/src/main/resources

Where [directory-fortress-core] is base folder of the fortress core source package and [directory-fortress-commander] is the current package's home folder.
___________________________________________________________________________________
###################################################################################
# SECTION 4:  Load Test Users
###################################################################################

Run maven install with load file:
# $M2_HOME/bin/mvn install -Dload.file=./src/main/resources/FortressWebDemoUsers.xml -DskipTests=true

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

Note: REALM_CONTEXT_SETUP.txt and REALM_HOST_SETUP.txt, located in directory-fortress-realm package describes the Tomcat setup in more detail.

d. Enter maven command to deploy to Tomcat:
# $M2_HOME/bin/mvn tomcat:deploy

e. Enter maven command to deploy to Tomcat:
# $M2_HOME/bin/mvn tomcat:deploy

f. To redeploy:
# $M2_HOME/bin/mvn tomcat:redeploy
___________________________________________________________________________________
###################################################################################
# SECTION 6:  Instructions to test Fortress Rest application
###################################################################################

Run the Selenium Web driver integration tests:
# $M2_HOME/bin/mvn verify -DskipTests=false

Note: This test case depends on:

a. SECTION 8 in directory-fortress-core README, 'Instructions to integration test using 'FortressJUnitTest'' has been executed.
This step loads test data into the ldap server.

b. Test data loaded from SECTION 4 of this document

c. Firefox is loaded on target machine