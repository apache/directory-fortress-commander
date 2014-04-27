/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.openldap.fortress.DelReviewMgr;
import org.openldap.fortress.ReviewMgr;
import org.openldap.fortress.rbac.AdminRole;
import org.openldap.fortress.rbac.Role;
import org.openldap.fortress.rbac.Session;

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
        catch (org.openldap.fortress.SecurityException se)
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
        catch (org.openldap.fortress.SecurityException se)
        {
            String error = ".getAdminList caught SecurityException=" + se;
            log.warn(error);
        }
        return rolesList;
    }
}
