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

# README for Apache Fortress WEB Security Model
![Apache Fortress Web Security Model](images/apache-fortress-web-security-model.png "Apache Fortress Web Security Model")

## Table of Contents

- Document Overview
- Understand the security model of Apache Fortress Web
- SECTION 1. TLS
- SECTION 2. Java EE security
- SECTION 3. Spring security FilterSecurityInterceptor
- SECTION 4. Apache Wicket Links
- SECTION 5. Apache Wicket Buttons
- SECTION 6. Additional Administrative Role-Based Access Control (ARBAC) Checks
- SECTION 7. Policy load
- SECTION 8. Verification

## Document Overview

- Provides a description of the various security mechanisms that are performed during Apache Fortress WEB runtime operations.

## Understand the security model of Apache Fortress Web

### A Typical Deployment

```
               .---------.      
               | Browser |      
               '----.----'      
                    | HTTPS
            .-------'------.
            | FortressWeb  |
            '-------.------'
                    | in-process
            .-------'------.
            | FortressCore |
            '-------.------'
                    | LDAPS
          .---------'-------.
          | DirectoryServer |
          '-----------------'
```

- Consists of three tiers: 1. **Browser**, 2. Servlet Container hosting **FortressWeb**, and 3. **DirectoryServer** that stores the policy information.
- **FortressWeb** is a web application archive (.war) that deploys into a Servlet Container, i.e. Apache Tomcat.
- **FortressCore** is a set of APIs that get embedded inside of Java apps, FortressWeb and Fortress Rest.
- **DirectoryServer** is a process implementing LDAPv3 protocols, e.g. ApacheDS or OpenLDAP.

### High-level Security Flow

- The user credentials are introduced into the call chain by the Client as a standard HTTP basic auth header.
- Passed into the Servlet Container for authentication and coarse-grained authorization before dispatch to FortressWeb.
- Spring security verifies user has role to view the web page.
- Medium-grained authorization performed inside the pages via Apache Wicket controls button and link visibility.
- The RBAC session passed into the FortressCore for fine-grained checks.

### Apache Fortress Web security model includes:

### 1. TLS

 Be sure to use because it allows confidentiality of credentials and message content via HTTPS. Refer to the documentation of your servlet container for how to enable.

## 2. Java EE security

- FortressWeb uses the [Apache Fortress Realm](https://github.com/apache/directory-fortress-realm) to provide Java EE authentication, coarse-grained authorization mapping the users and roles back to a given LDAP server.
- This interface requires standard HTTP Basic Auth tokens for the userid/password credentials.
- The credentials are verified by the Apache Fortress Realm via bind op invocation to the Directory Server.
- The coarse-grained authorization policy ensures callers have been assigned at least one of the following roles to successfully navigate to any page:
    1. ROLE_ADMIN
    2. ROLE_USERS
    3. ROLE_ROLES
    4. ROLE_PERMS
    5. ROLE_SSDS
    6. ROLE_DSDS
    7. ROLE_POLICIES
    8. ROLE_PERMOBJS
    9. ROLE_USEROUS
    10. ROLE_PERMOUS
    11. ROLE_ADMINROLES
    12. ROLE_ADMINOBJS
    13. ROLE_ADMINPERMS
    14. ROLE_AUDIT_AUTHZS
    15. ROLE_AUDIT_MODS
    16. ROLE_AUDIT_BINDS
    17. ROLE_GROUPS
    
 * per its deployment descriptor, [web.xml](src/main/webapp/WEB-INF/web.xml).

## 3. Spring security **FilterSecurityInterceptor**

- The page-to-role mappings are enforced by Spring security as defined [applicationContext](src/main/resources/applicationContext.xml)
- The following table illustrates the mapping:

| Role Name    | USERS | ROLES | POBJS | PERMS | PWPOLICIES | SSDS  | DSDS  | USEROUS | PERMOUS | ADMINROLES | ADMPOBJS | ADMPERMS | GROUPS | BINDS | AUTHZ | MODS  |
|--------------| ----- | ----- | ------| ----- | ---------- | ----- | ----- | ------- | ------- | ---------- | -------- | -------- | -------| ----- | ----- | ----- |
| ROLE_RBAC_ADMIN | true  | true  | true  | true  | true       | true  | true  | true    | true    | true       | true     | true     | true   | true  | true  | true  |
| ROLE_USERS   | true  | false | false | false | false      | false | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_ROLES   | false | true  | false | false | false      | false | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_PERMOBJS | false | false | true  | false | false      | false | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_PERMS   | false | false | false | true  | false      | false | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_POLICIES | false | false | false | false | true       | false | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_SSDS    | false | false | false | false | false      | true  | false | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_DSDS    | false | false | false | false | false      | false | true  | false   | false   | false      | false    | false    | false  | false | false | false |
| ROLE_USEROUS | false | false | false | false | false      | false | false | true    | false   | false      | false    | false    | false  | false | false | false |
| ROLE_PERMOUS | false | false | false | false | false      | false | false | false   | true    | false      | false    | false    | false  | false | false | false |
| ROLE_ADMINROLES | false | false | false | false | false      | false | false | false   | false   | true       | false    | false    | false  | false | false | false |
| ROLE_ADMINOBJS | false | false | false | false | false      | false | false | false   | false   | false      | true     | false    | false  | false | false | false |
| ROLE_ADMINPERMS | false | false | false | false | false      | false | false | false   | false   | false      | false    | true     | false  | false | false | false |
| ROLE_GROUPS  | false | false | false | false | false      | false | false | false   | false   | false      | false    | false    | true   | false | false | false |
| ROLE_AUDIT_BINDS | false | false | false | false | false      | false | false | false   | false   | false      | false    | false    | false  | true  | false | false |
| ROLE_AUDIT_AUTHZS | false | false | false | false | false      | false | false | false   | false   | false      | false    | false    | false  | false | true  | false |
| ROLE_AUDIT_MODS | false | false | false | false | false      | false | false | false   | false   | false      | false    | false    | false  | false | false | true  |

- For example, the administrator must have the 'ROLE_GROUPS' role activated into their session before Spring security allows entry to the 'GROUPS' page.

## 4. Apache Wicket Links

- The page links are controlled by the same RBAC Role assignments as the Spring security checks.
- For example, the administrator must have the 'ROLE_GROUPS' role activated into their session before Wicket will show the 'GROUPS' link on any page.
- The Spring and Wicket enforcements overlap. An example of 'Defense in Depth'. Both mechanisms must pass before an administrator is allowed to view any page.

## 5. Apache Wicket Buttons

- The page buttons are protected by Administrative perms.
- For example, the administrator must have the 'AdminMgrImpl:addRole' perm activated before Wicket will show the add button on the 'ROLES' page.

### Table of Apache Fortress Web Permissions

- Below are list of Admin perms (1), Pages (2), and the admin role mappings (3 - 6).
- Each perm maps to a single button on a single page.

| 1. Administrative Permission Name (object name:operation name)            | 2. Pages        | 3. fortress-core-super-admin | 4. fortress-web-user-admin | 5. fortress-web-audit-admin | 6. fortress-web-group-admin |
|---------------------------------------------------------------------------|-----------------|------------------------------|----------------------------|-----------------------------|-----------------------------|
| org.apache.directory.fortress.core.impl.AdminMgrImpl:addUser              | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:disableUser          | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deleteUser           | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updateUser           | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:changePassword       | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:lockUserAccount      | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:unlockUserAccount    | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:resetPassword        | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:assignUser           | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deassignUser         | USERS           | true                         | true                       | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:addRole              | ROLES           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updateRole           | ROLES           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deleteRole           | ROLES           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:addPermObj           | POBJS ADMOBJS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updatePermObj        | POBJS ADMOBJS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deletePermObj        | POBJS ADMOBJS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:addPermission        | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updatePermission     | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deletePermission     | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:createSsdSet         | SSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updateSsdSet         | SSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deleteSsdSet         | SSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:createDsdSet         | DSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updateDsdSet         | DSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deleteDsdSet         | DSDS            | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:addPermission        | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:updatePermission     | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AdminMgrImpl:deletePermission     | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.DelAdminMgrImpl:assignAdminRole   | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.DelAdminMgrImpl:deassignAdminRole | PERMS ADMPERMS  | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.DelAdminMgrImpl:addOU             | OUSERS OUPRMS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.DelAdminMgrImpl:updateOU          | OUSERS OUPRMS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.DelAdminMgrImpl:deleteOU          | OUSERS OUPRMS   | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:findUsers           | USERS GROUPS    | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:findRoles           | ROLES SSDS DSDS | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:findPermissions     | PERMS ADMPERMS  | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:ssdRoleSets         | SSDS DSDS       | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:dsdRoleSets         | SSDS DSDS       | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.DelReviewMgrImpl:ssdSets          | SSDS DSDS       | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.DelReviewMgrImpl:dsdSets          | SSDS DSDS       | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.ReviewMgrImpl:findPermObjs        | POBJS ADMPERMS  | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.DelReviewMgrImpl:searchOU         | OUUSERS OUPERMS | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:add                  | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:update               | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:delete               | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:addProperty          | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:deleteProperty       | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:assign               | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:deassign             | GROUPS          | true                         | false                      | false                       | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:findUsers            | GROUPS          | true                         | false                      | true                        | true                        |
| org.apache.directory.fortress.core.impl.GroupMgrImpl:find                 | GROUPS          | true                         | false                      | true                        | true                        |
| org.apache.directory.fortress.core.impl.PwPolicyMgrImpl:add               | PLCYS           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.PwPolicyMgrImpl:update            | PLCYS           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.PwPolicyMgrImpl:delete            | PLCYS           | true                         | false                      | false                       | false                       |
| org.apache.directory.fortress.core.impl.AuditMgrImpl:searchAdminMods      | MODS            | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.AuditMgrImpl:searchBinds          | BINDS           | true                         | false                      | true                        | false                       |
| org.apache.directory.fortress.core.impl.AuditMgrImpl:getUserAuthZs        | AUTHZ           | true                         | false                      | true                        | false                       |

- For example, the administrator must have the 'GroupMgrImpl:add' perm activated into their session before Wicket will show the add button on the 'GROUPS' page.
- These policies are defined here: [FortressWebDemoUsers](src/main/resources/FortressWebDemoUsers.xml)

### More on Apache Fortress Administrative Permissions

- Apache Fortress Web applies two types of security semantics: RBAC and ARBAC. 
- Their respective policies get stored inside separate trees in LDAP.

Sample Directory Information Tree:

```
dc=example,dc=com
 ├─ou=rbac  <- 'normal' RBAC data
 │  ├─ou=roles
 │  └─ou=perms
 ├─ou=arbac <- administrative RBAC data (ARBAC)
 │  ├─ou=roles
 │  └─ou=perms
 ├─ou=people
 └─ou=groups
 ...
```

- RBAC roles and perms are stored and used on behalf of business apps. For 'typical' security use cases.
- RBAC roles are also checked by the Spring and Wicket enforcement layers mentioned earlier.
- ARBAC roles and perms are checked when edits are made to RBAC policies. Like what the Apache Fortress Web does.
- Users and Groups can be assigned both RBAC and ARBAC roles.
- For example, an administrator would be assigned the 'ROLE_GROUPS' RBAC role + the 'fortress-web-group-admin' ARBAC role to be allowed entry to the 'GROUPS' page and use its buttons. 

## 6. Additional Administrative Role-Based Access Control (ARBAC) Checks

Fortress Web optionally enforces more rigorous checks.

- For more on ARBAC checking: [Apache Fortress Rest Security Model](https://github.com/apache/directory-fortress-enmasse/blob/master/README-SECURITY-MODEL.md)
- By default, ARBAC is disabled in the Apache Fortress Web runtime.
- To enable, add the following to fortress.properties:

```
is.arbac02=true
```

### ARBAC Rational

Pros:

1. Mandatory Access Controls

- Every Fortress API is guarded by an automatic permission check.
- More granular than buttons. One button may invoke two APIs.
- The Wicket and ARBAC enforcement layers overlap. Both mechanisms must pass before an administrator is allowed to call an API. 

2. Delegated Administration Checking

- Enforces administrator actions across entity sets on behalf of a particular organization.
- The administrator must be granted access before an organization's user can be modified and before any of its roles or permissions can be assigned.
- For detailed description, checkout the Rest Security Model document.

3. Auditing and History

- Using OpenLDAP's slapo-access log a complete audit trail can be stored. 
- Logs all actions, entity history, binds and authorization attempts, etc.

These Fortress Web pages are for viewing the audit log:

- BINDS - authentication attempts
- AUTHZ - authorization attempts
- MODS - history of changes to data

Cons:

1. Can be difficult to setup.

- We've given a head start with sample ARBAC policy load files.

2. It's complicated.

- The user and perm ou and range checks are hard to conceptualize and may not be required.

## 7. Policy load

 - The [Policy load file](./src/main/resources/FortressWebDemoUsers.xml) is a script that creates the roles and permissions that this app checks during code execution.  This step is performed during setup as described in the project's setup documentation. 
 - Test Users 
 
| User Type   | UserID | USERS | ROLES | POBJS | PERMS | PWPOLICIES | SSDS  | DSDS  | USEROUS | PERMOUS | ADMINROLES | ADMPOBJS | ADMPERMS | GROUPS  | BINDS   | AUTHZ | MODS  |
| ----------- | ------ | ----- |-------|-------|-------| ---------- |-------|-------|---------|---------|------------|----------|----------|---------|---------| ----- | ----- |
| Super Admin | test   | true  | true  | true  | true  | true       | true  | true  | true    | true    | true       | true     | true     | true    | true    | true  | true  |
| User Admin  | test1  | true  | false | false | false | false      | false | false | false   | false   | false      | false    | false    | false   | false   | false | false |
| Auditor     | test2  | true  | true  | true  | true  | true       | true  | true  | true    | true    | true       | true     | true     | true    | true    | true  | true  |
| Group Admin | test3  | false | false | false | false | false      | false | false | false   | false   | false      | false    | false    | true    | false   | false | false |

 * All test passwords = 'password'
 
## 8. Verification
 
- Run the Selenium Tests: [FortressWebSeleniumITCase](src/test/java/org/apache/directory/fortress/web/integration/FortressWebSeleniumITCase.java)
- Required security policy for selenium tests is loaded: a or b and c:
    - a. ARBAC Policy Load: [DelegatedAdminManagerLoad](https://github.com/apache/directory-fortress-core/blob/master/ldap/setup/DelegatedAdminManagerLoad.xml)
    - b. Fortress Junit Tests: [FortressJUnitTest](https://github.com/apache/directory-fortress-core/blob/master/src/test/java/org/apache/directory/fortress/core/impl/FortressJUnitTest.java)
    - c. Fortress Web Demo Load: [FortressWebDemoUsers](src/main/resources/FortressWebDemoUsers.xml)

#### END OF README
