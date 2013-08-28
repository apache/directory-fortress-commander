Copyright © 2011-2013. JoshuaTree. All Rights Reserved.
___________________________________________________________________________________
###################################################################################
README for Fortress Commander Web Application Installation
RC29 (BETA RELEASE CANDIDATE)
Last updated: August 27, 2013
___________________________________________________________________________________
###################################################################################
# Prerequisites
###################################################################################
1. Internet access to retrieve dependencies from online Maven repo.

NOTE: The EnMasse maven may run without connection to Internet iff:
- The binary dependencies are already present in M2_HOME

2. Java SDK Version 7 or beyond installed to target environment
3. Maven 3 installed to target environment
4. Fortress/OpenLDAP are installed to target system.

###################################################################################
# Important Notes about Commander Web Application
###################################################################################

1. Commander is released as Open Source and available for unrestricted use via BSD 3 clause license. (see LICENSE.txt)
  - Commander dependencies are Open Source also.

2. This web app was tested using Apache Tomcat 7 but would work inside any current Java Servlet container (with changes to deploy procedure)

3. Maven 'install' target in this package builds Commander war file which deploys to Java EE servlet container.

4. This document includes instructions to Compile, Deploy, run Javadoc and Test the Commander Web application using Apache Tomcat.

5. Security Measures implemented within this application include:

  - Java EE Security - Confidentiality, Authentication, Session Management
    - requires HTTP Basic Auth header exchange to pass credentials used for security checks.

  - Spring Security - Role-Based Access Control Interceptor
    - Service-level Authorization uses Spring Security.
    - To find out what Roles required to which Services, view the Spring annotations inside this file:
        commander-dist-[version]/src//main/java/com/jts/enmasse/FortressServiceImpl.java

  - Fortress Sentry - Java EE security plugin for Identity, Coarse-grained Authorization, and Audit Trail

  - Commander - Identity, Administrative, Compliance and Review pages

  - OpenLDAP - Password Hashing, Policies.
___________________________________________________________________________________
###################################################################################
# SECTION 1:  Prerequisites for use of Commander Web Application
###################################################################################

Before you can successfully complete the steps to install and run Commander, the following steps must be completed:

1. Internet access from your target machine to Maven 2 online repos.

2. Maven 3 installed to target:
http://maven.apache.org/download.html
http://www.sonatype.com/books/mvnref-book/reference/installation-sect-maven-install.html

3. Java SDK Version 6 or beyond installed:
http://www.oracle.com/technetwork/java/javase/downloads/index.html

4. Fortress/OpenLDAP QUICKSTART installed:
instructions: http://www.joshuatreesoftware.us/iamfortress/guides/README-QUICKSTART.html
binaries: https://iamfortress.org/projects

5. Tomcat 7 installed:
http://tomcat.apache.org

6. Fortress Sentry package (a.k.a Realm) installed:
instructions: http://www.joshuatreesoftware.us/iamfortress/javadocs/api-sentry/com/jts/fortress/sentry/tomcat/package-summary.html
binaries: https://iamfortress.org/projects

Note: There is a complete Commander demo that handles these prereqs for you located here:
https://iamfortress.org/Commander

_________________________________________________________________________________
###################################################################################
# SECTION 2:  Instructions for Commander installation using distribution package
###################################################################################

1. Retrieve Fortress Commander source code bundle either from iamfortress.org or OpenLDAP.org.

2. Extract contents of openldap-fortress-commander.tar.gz to target env.
___________________________________________________________________________________
###################################################################################
# SECTION 3:  Instructions to build Commander Web archive file
###################################################################################

1. Open a command prompt on target machine in the root folder of the commander-dist package

2. Set java home:
>export JAVA_HOME=/opt/jdk1.7.0_10

3. Set maven home:
>export M2_HOME=/usr/share/maven

4. Run maven install:
>mvn install

###################################################################################
# SECTION 4:  Instructions to Deploy Commander Web application to Tomcat
###################################################################################

1. Enable Maven to communicate with Tomcat using settings.xml file.

note: a typical location for this maven configuration file is: ~/.m2/settings.xml

Add to file:

<server>
	<id>local-tomcat</id>
      <username>tcmanager</username>
      <password>m@nager123</password>
</server>

note: If you followed the installation steps of Fortress QUICKSTART your Tomcat Manager creds would be as above.

2. Enter maven command to deploy to Tomcat:
>mvn tomcat:deploy

3. To redeploy:
>mvn tomcat:redeploy
___________________________________________________________________________________
###################################################################################
# SECTION 5:  Instructions to integration test Commander Web application
###################################################################################

note: This test case depends that the following Fortress targets have been run:
a. init-slapd
b. test-full

1. Run Selenium Web driver test
>mvn verify -DskipTests=false

___________________________________________________________________________________
###################################################################################
# SECTION 6:  Instructions to create Commander javadoc (optional)
###################################################################################

The service level documentation provides descriptions for each of the Commander pages.

1. Enter the following:

$ mvn javadoc:javadoc

2. View the document output here:

openldap-fortress-commander/target/site/apidocs