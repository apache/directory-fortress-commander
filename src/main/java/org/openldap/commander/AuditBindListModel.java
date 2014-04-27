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
import org.openldap.fortress.AuditMgr;
import org.openldap.fortress.rbac.Bind;
import org.openldap.fortress.rbac.Session;
import org.openldap.fortress.rbac.UserAudit;
import org.openldap.fortress.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class AuditBindListModel<T extends Serializable> extends Model
{
    @SpringBean
    private AuditMgr auditMgr;
    private static final Logger log = Logger.getLogger(AuditBindListModel.class.getName());
    private transient UserAudit userAudit;
    private transient List<Bind> binds = null;

    /**
     * Default constructor
     */
    public AuditBindListModel( final Session session )
    {
        Injector.get().inject(this);
        this.auditMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditBindListModel( UserAudit userAudit, final Session session )
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
        if (binds != null)
        {
            log.debug(".getObject count: " + userAudit != null ? binds.size() : "null");
            return (T) binds;
        }
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )   &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
           )
        {
            log.debug(".getObject null");
            binds = new ArrayList<Bind>();
        }
        else
        {
            // get the list of matching bind records from fortress:
            binds = getList(userAudit);
        }
        return (T) binds;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<Bind>)object).size() : "null");
        this.binds = (List<Bind>) object;
    }

    @Override
    public void detach()
    {
        this.binds = null;
        this.userAudit = null;
    }

    private List<Bind> getList(UserAudit userAudit)
    {
        List<Bind> bindList = null;
        try
        {
            bindList = auditMgr.searchBinds( userAudit );
        }
        catch (org.openldap.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return bindList;
    }
}
