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

-------------------------------------------------------------------------------
# README for Apache Fortress Web Multitenancy Configuration

-------------------------------------------------------------------------------
## Table of Contents

 * SECTION 1. Multitenancy Overview
 * SECTION 2. Multitenant Fortress Realm Instance
 * SECTION 3. Multitenant Fortress Web Instance
 * SECTION 4. Rationale for setting a contextId in two locations

-------------------------------------------------------------------------------
## SECTION 1.  Multitenancy Overview

From Wikipedia:
* *Software Multitenancy refers to a software architecture in which a single instance of a software runs on a server and serves multiple tenants. A tenant is a group of users who share a common access with specific privileges to the software instance. With a multitenant architecture, a software application is designed to provide every tenant a dedicated share of the instance including its data, configuration, user management, tenant individual functionality and non-functional properties. Multitenancy contrasts with multi-instance architectures, where separate software instances operate on behalf of different tenants.*

 *Commentators regard multitenancy as an important feature of cloud computing.*

 https://en.wikipedia.org/wiki/Multitenancy

For an overview of how fortress multitenancy works:
 * [Fortress Core Multitenancy README](https://github.com/apache/directory-fortress-core/blob/master/README-MULTITENANCY.md)

-------------------------------------------------------------------------------
## SECTION 2.  Multitenant Fortress Realm Instance

Fortress Realm uses the tenant id inside the context.xml file:

 ```
 <Context path="/commander" reloadable="true">

    <Realm className="org.apache.directory.fortress.realm.tomcat.Tc7AccessMgrProxy"
           defaultRoles=""
           containerType="TomcatContext"
           realmClasspath=""
           contextId="HOME"
           />
 </Context>
 ```

 The operations for this particular instance will be on behalf of the home contextId.

-------------------------------------------------------------------------------
## SECTION 3.  Multitenant Fortress Web Instance

Fortress Web uses the tenant id inside the fortress.properties file:

 ```
 contextId=acme123
 ```

 The operations for this instance will be scoped to acme123 tenant.

___________________________________________________________________________________
## SECTION 4.  Rationale for setting a contextId in two locations

Why are there are two locations for setting the tenant id, [context.xml](https://github.com/apache/directory-fortress-commander/blob/master/src/main/resources/META-INF/context.xml) and [fortress.properties](https://github.com/apache/directory-fortress-commander/blob/master/src/main/resources/fortress.properties.example)?

1. Expedience in loading the realm tenant id with the context.xml file and the spring beans using fortress.properties because that is where properties for those components are usually set.

2. Security Control.  It is necessary to allow the realm to use one tenant context, e.g. HOME, and the web app instance another, e.g. acme123.  For the why consider a use case.  One where many customer web app instances run from within one or more instances of a container (like Tomcat).
 Only corporate employees may administer security policies within the customer's web app instances, not the customers themselves.  On the contrary, we may want to allow the customer to administer their own security data in which case we'd set both to acme123.

___________________________________________________________________________________
#### END OF README-MULTITENANCY