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

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openldap.fortress.AccessMgr;
import org.openldap.fortress.rbac.Permission;
import org.openldap.fortress.rbac.Session;

/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class TestAuthorization
{
    @SpringBean
    AccessMgr accessMgr;

    public TestAuthorization()
    {
        Injector.get().inject(this);
    }

    private static final Logger LOG = LoggerFactory.getLogger( TestAuthorization.class.getName() );

    public boolean checkAccess( Session session, String objName, String opName )
    {
        boolean result = false;
        try
        {
            result = accessMgr.checkAccess( session, new Permission(objName, opName) );
        }
        catch ( org.openldap.fortress.SecurityException se )
        {
            LOG.warn( "SecurityException=" + se );
        }
        return result;
    }
}