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
RC40
Last updated: November 4, 2014
___________________________________________________________________________________
###################################################################################
# SECTION 1: Prerequisites
###################################################################################
1. Internet access to retrieve dependencies from online Maven repo.

NOTE: The Commander maven may run without connection to Internet iff:
- The binary dependencies are already present in M2_HOME

2. Java SDK Version 7 or beyond installed to target environment

3. Apache Ant installed.

4. Apache Maven 3 installed.

5. Fortress/ApacheDS QUICKSTART installed to target system.
Instructions: http://directory.apache.org/fortress/quick-start/apacheds/apacheds.html

6. Tomcat 7 (or suitable servlet container) installed:
http://tomcat.apache.org

7. Fortress Realm must be enabled:
Instructions: https://symas.com/javadocs/sentry

###################################################################################
# SECTION 2: Important Notes about Fortress Web Application
###################################################################################

1. Fortress Web is released as Apache 2.0. (see LICENSE.txt)

2. This web app was tested using Apache Tomcat 7 but would work inside any current Java Servlet container (with changes to deploy procedure)

3. Maven 'install' target in this package builds Commander war file which deploys to Java EE servlet container.

4. This document includes instructions to Compile, Deploy, run Javadoc and Test the Commander Web application using Apache Tomcat.

5. Security Measures implemented within this application include:

  - Java EE Security - Confidentiality, Authentication, Session Management
    - requires HTTP Basic Auth header exchange to pass credentials used for security checks.

  - Spring Security - Role-Based Access Control Interceptor
    - Service-level Authorization uses Spring Security.
    - To find out what Roles required to which Services, view the Spring annotations inside this file:
        commander-dist-[version]/src//main/java/org/openldap/enmasse/FortressServiceImpl.java

  - Fortress Sentry - Java EE security plugin for Identity, Coarse-grained Authorization, and Audit Trail

  - Commander - Identity, Administrative, Compliance and Review pages

  - Wicket - Controls buttons and links displayed by role and permissions

  - OpenLDAP - Password Hashing, Policies.
_________________________________________________________________________________
###################################################################################
# SECTION 3:  Instructions for Fortress Web installation using Source Bundle
###################################################################################

1. Retrieve Fortress Web source code bundle:
https://git-wip-us.apache.org/repos/asf/directory-fortress-web.git


2. Extract contents of directory-fortress-web.tar.gz to target env.
___________________________________________________________________________________
###################################################################################
# SECTION 4:  Instructions to build Fortress Web archive file
###################################################################################

1. Open a command prompt on target machine in the root folder of the commander-dist package

2. Set java home:
>export JAVA_HOME=/opt/jdk1.7.0_10

3. Set maven home:
>export M2_HOME=/usr/share/maven

4. Run maven install:
>mvn install

###################################################################################
# SECTION 5:  Instructions to Deploy Fortress Web application to Tomcat
###################################################################################

1. Enable Maven to communicate with Tomcat using settings.xml file.

note: a typical location for this maven configuration file is: ~/.m2/settings.xml

Add to file:

<server>
	<id>local-tomcat</id>
      <username>tcmanager</username>
      <password>m@nager123</password>
</server>

note1: If you followed the installation steps of Fortress QUICKSTART your Tomcat Manager creds would be as above.
note2: If not using the Fortress Tomcat realm, add the following to tomcat-users.xml and restart tomcat:
  <role rolename="manager-script"/>
  <user username="tcmanager" password="m@nager123" roles="manager-script"/>

2. Enter maven command to deploy to Tomcat:
>mvn tomcat:deploy

3. To redeploy:
>mvn tomcat:redeploy
___________________________________________________________________________________
###################################################################################
# SECTION 6:  Instructions to integration test Fortress Web application
###################################################################################

Note: This test case depends that the prerequisite tasks have been performed in SECTION 1.

1. Load the test security policy into LDAP using Commander test target:
>mvn test

2. Run the Selenium Web driver integration tests:
>mvn verify -DskipTests=false

___________________________________________________________________________________
###################################################################################
# SECTION 7:  Instructions to create Fortress Web javadoc (optional)
###################################################################################

The service level documentation provides descriptions for each of the Commander pages.

1. Enter the following:

$ mvn javadoc:javadoc

2. View the document output here:

directory-fortress-web/target/site/apidocs