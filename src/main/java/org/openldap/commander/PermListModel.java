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
import org.openldap.fortress.ReviewMgr;
import org.openldap.fortress.rbac.Permission;
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
public class PermListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(PermListModel.class.getName());
    private transient Permission perm;
    private transient List<Permission> perms = null;
    private boolean isAdmin;

    public PermListModel(final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param perm
     */
    public PermListModel(Permission perm, final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.perm = perm;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Permission> perms data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (perms != null)
        {
            log.debug(".getObject count: " + perms != null ? perms.size() : "null");
            return (T) perms;
        }
        if (perm == null)
        {
            log.debug(".getObject null");
            perms = new ArrayList<Permission>();
        }
        else
        {
            log.debug(" .getObject perm objectNm: " + perm != null ? perm.getObjName() : "null");
            log.debug(" .getObject perm opNm: " + perm != null ? perm.getOpName() : "null");
            perms = getList(perm);
        }
        return (T) perms;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + perms != null ? ((List<Role>)object).size() : "null");
        this.perms = (List<Permission>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.perms = null;
        this.perm = null;
    }

    private List<Permission> getList(Permission perm)
    {
        List<Permission> permsList = null;
        try
        {
            String szObjectNm = perm != null ? perm.getObjName() : "";
            String szOpNm = perm != null ? perm.getOpName() : "";
            log.debug(".getList objectNm: " + szObjectNm + " opNm: " + szOpNm);
            perm.setAdmin( isAdmin );
            permsList = reviewMgr.findPermissions(perm);
        }
        catch (org.openldap.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return permsList;
    }
}
