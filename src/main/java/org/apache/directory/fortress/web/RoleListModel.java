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
import org.apache.directory.fortress.core.DelReviewMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.rbac.AdminRole;
import org.apache.directory.fortress.core.rbac.Role;
import org.apache.directory.fortress.core.rbac.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class RoleListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger log = Logger.getLogger(RoleListModel.class.getName());
    private transient T role;
    private transient List<T> roles = null;
    private boolean isAdmin;


    public RoleListModel(final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param role
     */
    public RoleListModel(T role, final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.role = role;
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (roles != null)
        {
            log.debug(".getObject count: " + role != null ? roles.size() : "null");
            return (T) roles;
        }
        if (role == null)
        {
            log.debug(".getObject null");
            roles = new ArrayList<T>();
        }
        else
        {
            log.debug(".getObject roleNm: " + role != null ? ((Role)role).getName() : "null");
            if(isAdmin)
            {
                roles = getAdminList( ( (AdminRole)role ).getName() );
            }
            else
            {
                roles = getList( ( (Role)role ).getName() );
            }

        }
        return (T) roles;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<Role>)object).size() : "null");
        this.roles = (List<T>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.roles = null;
        this.role = null;
    }

    private List<T> getList(String szRoleNm)
    {
        List<T> rolesList = null;
        try
        {
            log.debug(".getList roleNm: " + szRoleNm);
            rolesList = (List<T>)reviewMgr.findRoles(szRoleNm);
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return rolesList;
    }

    private List<T> getAdminList(String szRoleNm)
    {
        List<T> rolesList = null;
        try
        {
            log.debug(".getList roleNm: " + szRoleNm);
            rolesList = (List<T>)delReviewMgr.findRoles(szRoleNm);
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getAdminList caught SecurityException=" + se;
            log.warn(error);
        }
        return rolesList;
    }
}
