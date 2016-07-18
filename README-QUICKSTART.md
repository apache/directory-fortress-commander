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

# README-QUICKSTART for Apache Fortress Web
 * Version 1.0.1

-------------------------------------------------------------------------------
## Table of Contents

 * Document Overview
 * SECTION 1. Prerequisites
 * SECTION 2. Configure Tomcat and Deploy Fortress Web
 * SECTION 3. Load Sample Security Policy
 * SECTION 4. Test

___________________________________________________________________________________
## Document Overview

This document contains instructions to deploy a pre-built Fortress Web instance to Apache Tomcat.

-------------------------------------------------------------------------------
## SECTION 1. Prerequisites

Minimum software requirements:
 * Apache Tomcat7++
 * Completed either section in Apache Fortress Core Quickstart:
    * *SECTION 3. Apache Fortress Core Integration Test* in [README-QUICKSTART-SLAPD.md](https://github.com/apache/directory-fortress-core/blob/master/README-QUICKSTART-SLAPD.md)
    * *SECTION 4. Apache Fortress Core Integration Test* in [README-QUICKSTART-APACHEDS.md](https://github.com/apache/directory-fortress-core/blob/master/README-QUICKSTART-APACHEDS.md)

___________________________________________________________________________________
## SECTION 2. Configure Tomcat and Deploy Fortress Web

Set the java system properties in tomcat with the target ldap server's coordinates.

1. Edit the startup script for Tomcat

2. Set the java opts

 a. For OpenLDAP:

 ```
 JAVA_OPTS="-Dversion=1.0.1 -Dfortress.admin.user=cn=Manager,dc=example,dc=com -Dfortress.admin.pw=secret -Dfortress.config.root=ou=Config,dc=example,dc=com"
 ```

 b. For ApacheDS:
 ```
 JAVA_OPTS="$JAVA_OPTS -Dfortress.admin.user=uid=admin,ou=system -Dfortress.admin.pw=secret -Dfortress.config.root=ou=Config,dc=example,dc=com -Dfortress.port=10389"
 ```

3. Verify these settings match your target LDAP server.

4. Download the fortress realm proxy jar into tomcat/lib folder:

  ```
  wget http://repo.maven.apache.org/maven2/org/apache/directory/fortress/fortress-realm-proxy/1.0.1/fortress-realm-proxy-1.0.1.jar -P $TOMCAT_HOME/lib
  ```

  where *TOMCAT_HOME* matches your target env.

5. Download the fortress web war into tomcat/webapps folder:

  ```
  wget http://repo.maven.apache.org/maven2/org/apache/directory/fortress/fortress-web/1.0.1/fortress-web-1.0.1.war -P $TOMCAT_HOME/webapps
  ```

  where *TOMCAT_HOME* matches your target env.

6. Restart tomcat for new settings to take effect.

___________________________________________________________________________________
## SECTION 3. Load Sample Security Policy

From the Fortress Core package perform the following steps:

1. Download the fortress web sample security policy from git:

 ```
 wget https://github.com/apache/directory-fortress-commander/blob/master/src/main/resources/FortressWebDemoUsers.xml -P ldap/setup
 ```

2. Run maven install with -Dload.file file:

 ```
 mvn install -Dload.file=ldap/setup/FortressWebDemoUsers.xml
 ```

 Note: This step must be completed before tests can be successfully run.

___________________________________________________________________________________
## SECTION 4. Test

1. Open browser and test (creds: test/password):

 ```
 http://hostname:8080/fortress-web
 ```

2. Click on the links, to pull up various views on the data stored in the directory.

3. Run the selenium automated test:

 ```
 mvn test -Dtest=FortressWebSeleniumITCase
 ```

 *Requires Firefox on target machine.*

___________________________________________________________________________________
#### END OF README-QUICKSTART