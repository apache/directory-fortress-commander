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
 * Version 1.0.0

-------------------------------------------------------------------------------
## Table of Contents

 * Document Overview
 * SECTION 1. Prerequisites
 * SECTION 2. Configure Tomcat
 * SECTION 3. Load Sample Security Policy
 * SECTION 4. Deploy to Tomcat Server

___________________________________________________________________________________
## Document Overview

Note: This document is a *work in progress*

This document contains instructions to deploy a pre-built Apache Fortress Web instance to Tomcat and configure the server for its use.

-------------------------------------------------------------------------------
## SECTION 1. Prerequisites

Minimum software requirements:
 * Apache Tomcat7++
 * Either OpenLDAP or ApacheDS configured for Apache Fortress

___________________________________________________________________________________
## SECTION 2. Configure Tomcat

Set the java system properties in tomcat with the target ldap server's coordinates.

1. Edit the startup script for Tomcat

2. Set the java opts

 a. For OpenLDAP:

 ```
 JAVA_OPTS="-Dversion=1.0.0 -Dfortress.admin.user=cn=Manager,dc=example,dc=com -Dfortress.admin.pw=secret -Dfortress.config.root=ou=Config,dc=example,dc=com"
 ```

 b. For ApacheDS:
 ```
 JAVA_OPTS="$JAVA_OPTS -Dfortress.admin.user=uid=admin,ou=system -Dfortress.admin.pw=secret -Dfortress.config.root=ou=Config,dc=example,dc=com -Dfortress.port=10389"
 ```

3. Verify these settings match your target LDAP server.

4. Download the fortress realm proxy jar into tomcat/lib folder:

  ```
  wget http://repo.maven.apache.org/maven2/org/apache/directory/fortress/fortress-realm-proxy/1.0-RC42/fortress-realm-proxy-1.0-RC42.jar -P /usr/local/tomcat8/lib
  ```

5. Restart tomcat for new settings to take effect.

___________________________________________________________________________________
## SECTION 3. Load Sample Security Policy

Run maven install with load file:
```
mvn install -Dload.file=./src/main/resources/FortressWebDemoUsers.xml
```

___________________________________________________________________________________
## SECTION 4. Deploy to Tomcat Server

1. Download the fortress web war into tomcat/webapps folder:

  ```
  wget https://repository.apache.org/content/repositories/orgapachedirectory-1094/org/apache/directory/fortress/fortress-web/1.0.0/fortress-web-1.0.0.war
  ```

2. Open browser and test (creds: test/password):

 ```
 http://hostname:8080/fortress-web-1.0.0
 ```

___________________________________________________________________________________
#### END OF README-QUICKSTART