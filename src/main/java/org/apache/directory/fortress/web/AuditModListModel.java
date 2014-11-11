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
import org.apache.directory.fortress.core.rbac.Mod;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.User;
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
public class AuditModListModel<T extends Serializable> extends Model
{
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(AuditModListModel.class.getName());
    private transient UserAudit userAudit;
    private transient List<Mod> mods = null;

    /**
     * Default constructor
     */
    public AuditModListModel( final Session session )
    {
        Injector.get().inject(this);
        this.auditMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditModListModel( UserAudit userAudit, final Session session )
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
        if (mods != null)
        {
            log.debug(".getObject count: " + userAudit != null ? mods.size() : "null");
            return (T) mods;
        }
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )  &&
               !VUtil.isNotNullOrEmpty( userAudit.getObjName() )  &&
               !VUtil.isNotNullOrEmpty( userAudit.getOpName() )  &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
           )
        {
            log.debug(".getObject null");
            mods = new ArrayList<Mod>();
        }
        else
        {
            // do we need to retrieve the internalUserId (which is what maps to admin modification record in slapd audit log?
            if(VUtil.isNotNullOrEmpty( userAudit.getUserId()) && !VUtil.isNotNullOrEmpty( userAudit.getInternalUserId()))
            {
                User user = getUser( userAudit );
                userAudit.setInternalUserId( user.getInternalId() );
                if(user == null)
                {
                    String warning = "Matching user not found for userId: " + userAudit.getUserId();
                    log.warn( warning );
                    throw new RuntimeException( warning );
                }
            }
            mods = getList(userAudit);
        }
        return (T) mods;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<AuthZ>)object).size() : "null");
        this.mods = (List<Mod>) object;
    }

    @Override
    public void detach()
    {
        this.mods = null;
        this.userAudit = null;
    }

    private List<Mod> getList(UserAudit userAudit)
    {
        List<Mod> modList = null;
        try
        {
            userAudit.setDn( "" );
            if(VUtil.isNotNullOrEmpty( userAudit.getObjName() ))
            {
                userAudit.setObjName( getTruncatedObjName( userAudit.getObjName() ) );
            }
            modList = auditMgr.searchAdminMods( userAudit );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return modList;
    }

    /**
     * Utility will parse a String containing objName.operationName and return the objName only.
     *
     * @param szObj contains raw data format.
     * @return String containing objName.
     */
    private String getTruncatedObjName(String szObj)
    {
        int indx = szObj.lastIndexOf('.');
        if(indx == -1)
        {
            return szObj;
        }
        return szObj.substring(indx + 1);
    }

    private User getUser(UserAudit userAudit)
    {
        User user = null;
        try
        {
            user = reviewMgr.readUser( new User ( userAudit.getUserId() ) );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getUser caught SecurityException=" + se;
            log.warn(error);
        }
        return user;
    }
}
