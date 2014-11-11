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
package org.apache.directory.fortress.web;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.AuditMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.rbac.AuthZ;
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.UserAudit;
import org.apache.directory.fortress.core.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class AuditAuthzListModel<T extends Serializable> extends Model
{
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(AuditAuthzListModel.class.getName());
    private transient UserAudit userAudit;
    private transient List<AuthZ> authZs = null;

    /**
     * Default constructor
     */
    public AuditAuthzListModel( final Session session )
    {
        Injector.get().inject(this);
        this.auditMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditAuthzListModel( UserAudit userAudit, final Session session )
    {
        Injector.get().inject(this);
        this.userAudit = userAudit;
        this.auditMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (authZs != null)
        {
            log.debug(".getObject count: " + userAudit != null ? authZs.size() : "null");
            return (T) authZs;
        }
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )   &&
               !VUtil.isNotNullOrEmpty( userAudit.getObjName() )  &&
               !VUtil.isNotNullOrEmpty( userAudit.getOpName() )  &&
               //!VUtil.isNotNullOrEmpty( userAudit.getDn() )  &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
            ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )   &&
                VUtil.isNotNullOrEmpty( userAudit.getObjName() )  &&
                !VUtil.isNotNullOrEmpty( userAudit.getOpName() )  &&
                userAudit.getBeginDate() == null  &&
                userAudit.getEndDate() == null
            )
           )

        {
            log.debug(".getObject null");
            authZs = new ArrayList<AuthZ>();
        }
        else
        {
            // get the list of matching authorization records from fortress:
            //log.debug(".getObject authZ id: " + userAudit != null ? userAudit.getUserId() : "null");
            if(VUtil.isNotNullOrEmpty( userAudit.getObjName()) && VUtil.isNotNullOrEmpty( userAudit.getOpName()) && !VUtil.isNotNullOrEmpty( userAudit.getDn()))
            {
                Permission permission = getPermission( userAudit );
                userAudit.setDn( permission.getDn() );
                if(permission == null)
                {
                    String warning = "Matching permission not found for object: " + userAudit.getObjName() + " operation: " + userAudit.getOpName();
                    log.warn( warning );
                    throw new RuntimeException( warning );
                }
            }
            authZs = getList(userAudit);
            userAudit.setDn( "" );
        }
        return (T) authZs;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<AuthZ>)object).size() : "null");
        this.authZs = (List<AuthZ>) object;
    }

    @Override
    public void detach()
    {
        this.authZs = null;
        this.userAudit = null;
    }

    private List<AuthZ> getList(UserAudit userAudit)
    {
        List<AuthZ> authZList = null;
        try
        {
            authZList = auditMgr.getUserAuthZs( userAudit );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return authZList;
    }

    private Permission getPermission(UserAudit userAudit)
    {
        Permission permission = null;
        try
        {
            permission = reviewMgr.readPermission( new Permission ( userAudit.getObjName(), userAudit.getOpName(), userAudit.isAdmin()) );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getPermission caught SecurityException=" + se;
            log.warn(error);
        }
        return permission;
    }
}
