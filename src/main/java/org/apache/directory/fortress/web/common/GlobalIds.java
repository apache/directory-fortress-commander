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
package org.apache.directory.fortress.web.common;

/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class GlobalIds
{
    public static final String ROLE_USERS = "ROLE_USERS";
    public static final String ROLE_ROLES = "ROLE_ROLES";
    public static final String ROLE_PERMOBJS = "ROLE_PERMOBJS";
    public static final String ROLE_PERMS = "ROLE_PERMS";
    public static final String ROLE_SSDS = "ROLE_SSDS";
    public static final String ROLE_DSDS = "ROLE_DSDS";
    public static final String ROLE_USEROUS = "ROLE_USEROUS";
    public static final String ROLE_PERMOUS = "ROLE_PERMOUS";
    public static final String ROLE_POLICIES = "ROLE_POLICIES";
    public static final String ROLE_ADMINROLES = "ROLE_ADMINROLES";
    public static final String ROLE_ADMINOBJS = "ROLE_ADMINOBJS";
    public static final String ROLE_ADMINPERMS = "ROLE_ADMINPERMS";
    public static final String ROLE_AUDIT_AUTHZS = "ROLE_AUDIT_AUTHZS";
    public static final String ROLE_AUDIT_BINDS = "ROLE_AUDIT_BINDS";
    public static final String ROLE_AUDIT_MODS = "ROLE_AUDIT_MODS";
    public static final String ROLE_GROUPS = "ROLE_GROUPS";
    public static final String SSD = "SSD";
    public static final String DSD = "DSD";
    public static final String PAGE_TYPE = "type";
    public static final String ADMIN_MGR = "org.apache.directory.fortress.core.impl.AdminMgrImpl";
    public static final String REVIEW_MGR = "org.apache.directory.fortress.core.impl.ReviewMgrImpl";
    public static final String DEL_ADMIN_MGR = "org.apache.directory.fortress.core.impl.DelAdminMgrImpl";
    public static final String DEL_REVIEW_MGR = "org.apache.directory.fortress.core.impl.DelReviewMgrImpl";
    public static final String PWPOLICY_MGR = "org.apache.directory.fortress.core.impl.PwPolicyMgrImpl";
    public static final String AUDIT_MGR = "org.apache.directory.fortress.core.impl.AuditMgrImpl";
    public static final String GROUP_MGR = "org.apache.directory.fortress.core.ldap.group.GroupMgrImpl";
    public static final String ASSIGN_USER = "assignUser";
    public static final String ADD = "add";
    public static final String COMMIT = "commit";
    public static final String DELETE = "delete";
    public static final String CANCEL = "cancel";
    public static final String TIMEOUT_ARC = "timeoutARC";
    public static final String BEGIN_TIME_ARC = "beginTimeARC";
    public static final String END_TIME_ARC = "endTimeARC";
    public static final String BEGIN_DATE_ARC = "beginDateARC";
    public static final String END_DATE_ARC = "endDateARC";
    public static final String BEGIN_LOCK_DATE_ARC = "beginLockDateARC";
    public static final String END_LOCK_DATE_ARC = "endLockDateARC";
    public static final String BEGIN_TIME_RC = "beginTimeRC";
    public static final String END_TIME_RC = "endTimeRC";
    public static final String BEGIN_DATE_RC = "beginDateRC";
    public static final String END_DATE_RC = "endDateRC";
    public static final String BEGIN_LOCK_DATE_RC = "beginLockDateRC";
    public static final String END_LOCK_DATE_RC = "endLockDateRC";
    public static final String TIMEOUT_RC = "timeoutRC";
    public static final String SUNDAY_RC = "sundayRC";
    public static final String MONDAY_RC = "mondayRC";
    public static final String TUESDAY_RC = "tuesdayRC";
    public static final String WEDNESDAY_RC = "wednesdayRC";
    public static final String THURSDAY_RC = "thursdayRC";
    public static final String FRIDAY_RC = "fridayRC";
    public static final String SATURDAY_RC = "saturdayRC";
    public static final String ASSIGN = "assign";
    public static final String ROLE_ASSIGNMENTS_LABEL = "roleAssignmentsLabel";
    public static final String SELECT = "select";
    public static final String SEARCH = "search";
    public static final String CLEAR = "clear";
    public static final String SEARCH_VAL = "searchVal";
    public static final String MONDAY_ARC = "mondayARC";
    public static final String TUESDAY_ARC = "tuesdayARC";
    public static final String WEDNESDAY_ARC = "wednesdayARC";
    public static final String THURSDAY_ARC = "thursdayARC";
    public static final String FRIDAY_ARC = "fridayARC";
    public static final String ASSIGN_NEW_ROLE = "newUserRole";
    public static final String ASSIGN_NEW_ADMIN_ROLE = "newUserAdminRole";
    public static final String ASSIGN_ADMIN_ROLE = "assignAdminRole";
    public static final String DESCRIPTION = "description";
    public static final String EMAILS = "emails";
    public static final String PHONES = "phones";
    public static final String MOBILES = "mobiles";
    public static final String ADDRESS_ASSIGNMENTS_LABEL = "addressAssignmentsLabel";
    public static final String ADDRESSES = "addresses";
    public static final String ADDRESS_CITY = "address.city";
    public static final String ADDRESS_STATE = "address.state";
    public static final String ADDRESS_COUNTRY = "address.country";
    public static final String ADDRESS_POSTAL_CODE = "address.postalCode";
    public static final String ADDRESS_POST_OFFICE_BOX = "address.postOfficeBox";
    public static final String ADDRESS_BUILDING = "address.building";
    public static final String ADDRESS_DEPARTMENT_NUMBER = "address.departmentNumber";
    public static final String ADDRESS_ROOM_NUMBER = "address.roomNumber";
    public static final String TEMPORAL_CONSTRAINTS_LABEL = "temporalConstraintsLabel";
    public static final String BEGIN_TIME_P = "beginTimeP";
    public static final String END_TIME_P = "endTimeP";
    public static final String BEGIN_DATE_P = "beginDateP";
    public static final String END_DATE_P = "endDateP";
    public static final String BEGIN_LOCK_DATE_P = "beginLockDateP";
    public static final String END_LOCK_DATE_P = "endLockDateP";
    public static final String TIMEOUT_P = "timeoutP";
    public static final String SUNDAY_P = "sundayP";
    public static final String MONDAY_P = "mondayP";
    public static final String TUESDAY_P = "tuesdayP";
    public static final String WEDNESDAY_P = "wednesdayP";
    public static final String THURSDAY_P = "thursdayP";
    public static final String FRIDAY_P = "fridayP";
    public static final String SATURDAY_P = "saturdayP";
    public static final String SYSTEM_INFO_LABEL = "systemInfoLabel";
    public static final String SYSTEM = "system";
    public static final String CN = "cn";
    public static final String SN = "sn";
    public static final String IMPORT_PHOTO_LABEL = "importPhotoLabel";
    public static final String SAVE = "save";
    public static final String NAME = "name";
    public static final String USER_ID = "userId";
    public static final String PSWD_FIELD = "pswdField";
    public static final String NEW_USER_ROLE_FIELD = "newUserRole";
    public static final String NEW_USER_ADMIN_ROLE_FIELD = "newUserAdminRole";
    public static final String LOGIN = "login";
    public static final String EMPLOYEE_TYPE = "employeeType";
    public static final String TITLE = "title";
    public static final String GROUP_PAGE = "groups";
    public static final String AUDIT_AUTHZS_PAGE = "authzs";
    public static final String AUDIT_MODS_PAGE = "mods";
    public static final String AUDIT_BINDS_PAGE = "binds";
    public static final String JPEGPHOTO = "jpegPhoto";
    public static final String OU = "ou";
    public static final String REQ_AUTHZ_ID = "reqAuthzID";
    public static final String REQ_DN = "reqDN";
    public static final String REQ_RESULT = "reqResult";
    public static final String REQ_START = "reqStart";
    public static final String REQ_ATTR = "reqAttr";
    public static final String REQ_ATTRS_ONLY = "reqAttrsOnly";
    public static final String REQ_DEREF_ALIASES = "reqDerefAliases";
    public static final String AUTHZ_SUCCESS_CODE = "6";
    public static final String BIND_SUCCESS_CODE = "0";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String AUDIT_TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String FIND_USERS = "findUsers";
    public static final String FIND_PERMISSIONS = "findPermissions";
    public static final String ONBLUR = "blur";
    public static final String ADDRESS_ADDRESSES = "address.addresses";
    public static final String FT_MOD_ID = "ftModId";
    public static final String FT_MODIFIER = "ftModifier";
    public static final String FT_MOD_CODE = "ftModCode";

    public static final String OBJ_NAME = "objName";
    public static final String OP_NAME = "opName";
    public static final String FAILED_ONLY = "failedOnly";
    public static final String ADMIN = "admin";
    public static final String GET_USER_AUTHZS = "getUserAuthZs";
    public static final String GET_USER_BINDS = "searchBinds";
    public static final String OBJECT_ID = "objId";
    public static final String USERS_PAGE = "users";
    public static final String ROLES_PAGE = "roles";
    public static final String ADMROLES_PAGE = "admroles";
    public static final String POBJS_PAGE = "pobjs";
    public static final String ADMPERMS_PAGE = "admperms";
    public static final String PERMS_PAGE = "perms";
    public static final String PWPOLICIES_PAGE = "pwpolicies";
    public static final String SSDS_PAGE = "ssds";
    public static final String DSDS_PAGE = "dsds";
    public static final String USEROUS_PAGE = "userous";
    public static final String PERMOUS_PAGE = "permous";
    public static final String ADMPOBJS_PAGE = "admpobjs";
    public static final String WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML = "window.location.replace(\"/fortress-web/home.html\");";

    public static final String WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE = "Wicket.Window.unloadConfirmation = false;";
    public static final String ADD_USER = "addUser";
    public static final String UPDATE_USER = "updateUser";
    public static final String DELETE_USER = "deleteUser";
    public static final String DEASSIGN = "deassign";
    public static final String DEASSIGN_USER = "deassignUser";
    public static final String NAVPANEL = "navpanel";
    public static final String INFOPANEL = "infopanel";
    public static final String OBJECTLISTPANEL = "objectlistpanel";
    public static final String OBJECTDETAILPANEL = "objectdetailpanel";
    public static final String OULISTPANEL = "oulistpanel";
    public static final String OUDETAILPANEL = "oudetailpanel";
    public static final String PERMLISTPANEL = "permlistpanel";
    public static final String PERMDETAILPANEL = "permdetailpanel";
    public static final String ROLELISTPANEL = "rolelistpanel";
    public static final String ROLEDETAILPANEL = "roledetailpanel";
    public static final String SDLISTPANEL = "sdlistpanel";
    public static final String SDDETAILPANEL = "sddetailpanel";
    public static final String GROUPLISTPANEL = "grouplistpanel";
    public static final String GROUPDETAILPANEL = "groupdetailpanel";
    public static final String LAYOUT = "layout";
    public static final String PAGE_HEADER = "pageHeader";
    public static final String DETAIL_FIELDS = "detailFields";
    public static final String EDIT_FIELDS = "editFields";
    public static final String USERAUDITDETAILPANEL = "userauditdetailpanel";
    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";

    public static final String FIND_ROLES = "findRoles";
    public static final String ROLEAUXPANEL = "roleauxpanel";
    public static final String ADD_ROLE = "addRole";
    public static final String UPDATE_ROLE = "updateRole";
    public static final String DELETE_ROLE = "deleteRole";
    public static final String PARENTS = "parents";
    public static final String OS_P = "osP";
    public static final String OS_U = "osU";
    public static final String BEGIN_RANGE = "beginRange";
    public static final String BEGIN_INCLUSIVE = "beginInclusive";
    public static final String END_RANGE = "endRange";
    public static final String END_INCLUSIVE = "endInclusive";
    public static final String PERMOU_SEARCH = "permou.search";
    public static final String USEROU_SEARCH = "userou.search";
    public static final String BEGIN_RANGE_SEARCH = "beginRange.search";
    public static final String END_RANGE_SEARCH = "endRange.search";
    public static final String PARENTROLES_SEARCH = "parentroles.search";
    public static final String POLICY_SEARCH = "policy.search";
    public static final String OU_SEARCH = "ou.search";
    public static final String ROLES_SEARCH = "roles.search";
    public static final String FIELD_2 = "field2";
    public static final String FIELD_1 = "field1";
    public static final String IS_JETTY_SERVER = "is-jetty-server";
}
