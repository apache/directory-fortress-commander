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

import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.cfg.Config;
import org.apache.directory.fortress.realm.J2eePolicyMgr;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.User;
import org.apache.directory.fortress.core.util.attr.VUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Common static utils used by Wicket web apps to make security calls using Fortress apis.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SecUtils
{
    private static final Logger LOG = Logger.getLogger( SecUtils.class.getName() );
    private static final String PERMS_CACHED = "perms.cached";
    public static final boolean IS_PERM_CACHED = ( ( Config.getProperty( PERMS_CACHED ) != null ) && ( Config
        .getProperty( PERMS_CACHED ).equalsIgnoreCase( "true" ) ) );

    /**
     * Return the fortress session that is cached within the wicket session object.
     *
     * @param component needed to get handle to wicket session.
     * @return fortress session object.
     */
    public static Session getSession(Component component)
    {
        return ( ( WicketSession ) component.getSession() ).getSession();
    }

    /**
     *  Used when web app needs to create a 'trusted' fortress session.
     *
     *  Does not check user's password.
     *
     * @param accessMgr fortress access mgr apis
     * @param userId required for rbac session creation.
     * @return rbac session.
     */
    public static Session createSession(AccessMgr accessMgr, String userId)
    {
        Session session;
        try
        {
            // Create an RBAC session and attach to Wicket session:
            session = accessMgr.createSession( new User( userId ), true );
            String message = "RBAC Session successfully created for userId: " + session.getUserId();
            LOG.debug( message );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "createSession caught SecurityException=" + se;
            LOG.error( error );
            throw new RuntimeException( error );
        }
        return session;
    }

    /**
     * Here the wicket session is loaded with the fortress session and permissions.
     *
     *
     * @param delAccessMgr needed to pull back fortress arbac permissions.
     * @param session needed for call into accessMgr.
     */
    public static void loadPermissionsIntoSession( DelAccessMgr delAccessMgr, Session session)
    {
        try
        {
            // Retrieve user permissions and attach fortress session to Wicket session:
            ( ( WicketSession ) WicketSession.get() ).setSession( session );
            List<Permission> permissions = delAccessMgr.sessionPermissions( session );
            ( ( WicketSession ) WicketSession.get() ).setPermissions( permissions );
            String message = "Session successfully created for userId: " + session.getUserId();
            LOG.debug( message );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "loadPermissionsIntoSession caught SecurityException=" + se;
            LOG.error( error );
            throw new RuntimeException( error );
        }
    }

    /**
     * Returns the fortress arbac perms that are cashed in the wicket session.
     *
     * @param component needed to get a handle on the wicket session object.
     * @return collection of fortress admin perms.
     */
    public static List<Permission> getPermissions(Component component)
    {
        return ( ( WicketSession ) component.getSession() ).getPermissions();
    }

    /**
     * Retrieve RBAC session permissions from Fortress and place in the Wicket session.
     */
    public static void getPermissions( Component component, AccessMgr accessMgr )
    {
        try
        {
            if ( IS_PERM_CACHED )
            {
                WicketSession session = ( WicketSession ) component.getSession();
                List<Permission> permissions = accessMgr.sessionPermissions( session.getSession() );
                ( ( WicketSession ) WicketSession.get() ).setPermissions( permissions );
            }
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "getPermissions caught SecurityException=" + se;
            LOG.error( error );
            throw new RuntimeException( error );
        }
    }

    /**
     * Wrapper for the httpservlet isUserInRole api.
     *
     * @param roleName contains the name of role being checked.
     * @param servletReq handle used to make inquiry.
     * @return true if authorized, false otherwise.
     */
    public static boolean isAuthorized( String roleName, HttpServletRequest servletReq )
    {
        boolean isAuthorized = false;
        if ( servletReq.isUserInRole( roleName ) )
        {
            isAuthorized = true;
        }
        return isAuthorized;
    }

    /**
     * Is the supplied permission in the wicket session cache?  Called by buttons.
     * if not found, button will be invisible.
     *
     * @param permission fortress perm requires {link @Permission#objName} and {link @Permission#opName} are set.
     * @param component needed to get handle on the wicket session object.
     * @return true if found, false otherwise
     */
    public static boolean isFound( Permission permission, Component component )
    {
        List<Permission> permissions = SecUtils.getPermissions( component );
        return VUtil.isNotNullOrEmpty( permissions ) && permissions.contains( permission );
    }

    /**
     * Wrapper to fortress checkAccess api.
     * @param component contains the wicket session handle.
     * @param accessMgr has the checkAccess api
     * @param objName string value
     * @param opName string value
     * @param objId string value
     * @return
     * @throws org.apache.directory.fortress.core.SecurityException checked exception for system errors.
     */
    public static boolean checkAccess(Component component, AccessMgr accessMgr, String objName, String opName, String objId ) throws org.apache.directory.fortress.core.SecurityException
    {
        WicketSession session = ( WicketSession )component.getSession();
        Permission permission = new Permission( objName, opName, objId );
        return accessMgr.checkAccess( session.getSession(), permission );
    }


    /**
     * Convert the principal into fortress session and load into wicket session along with perms.
     *
     */
    public static void initializeSession(Component component, J2eePolicyMgr j2eePolicyMgr, AccessMgr accessMgr, String szPrincipal )
    {
        Session realmSession = null;
        try
        {
            realmSession = j2eePolicyMgr.deserialize( szPrincipal );
        }
        catch( SecurityException se )
        {
            throw new RuntimeException( se );
        }
        if(realmSession != null)
        {
            synchronized ( ( WicketSession ) WicketSession.get() )
            {
                if ( SecUtils.getSession( component ) == null )
                {
                    LOG.info( "realmSession user: " + realmSession.getUserId() );
                    // Retrieve user permissions and attach RBAC session to Wicket session:
                    ( ( WicketSession ) WicketSession.get() ).setSession( realmSession );
                    getPermissions( component, accessMgr );
                }
            }
        }
    }
}