/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.jts.fortress.AccessMgr;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.Session;

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

    public boolean checkAccess( Session session, String objectName, String opName )
    {
        boolean result = false;
        try
        {
            result = accessMgr.checkAccess( session, new Permission(objectName, opName) );
        }
        catch ( us.jts.fortress.SecurityException se )
        {
            LOG.warn( "SecurityException=" + se );
        }
        return result;
    }
}