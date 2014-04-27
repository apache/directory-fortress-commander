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


import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.openldap.fortress.rbac.Permission;
import org.openldap.fortress.rbac.Session;

import java.util.List;


/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class RbacSession extends WebSession
{
    private Session session;
    private List<Permission> permissions;

    /**
     * Constructor. Note that {@link org.apache.wicket.request.cycle.RequestCycle} is not available until this
     * constructor returns.
     *
     * @param request The current request
     */
    public RbacSession( Request request )
    {
        super( request );
    }

    public Session getRbacSession()
    {
        return session;
    }

    public void setSession( Session session )
    {
        this.session = session;
    }

    public List<Permission> getPermissions()
    {
        return permissions;
    }

    public void setPermissions( List<Permission> permissions )
    {
        this.permissions = permissions;
    }
}
