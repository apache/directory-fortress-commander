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
package org.apache.directory.fortress.web.control;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.util.Config;
import org.apache.directory.fortress.core.model.UserRole;
import org.apache.directory.fortress.core.model.Warning;
import org.apache.directory.fortress.realm.*;
import org.apache.directory.fortress.realm.GlobalIds;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.apache.wicket.ajax.AjaxRequestTarget;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * Common static utils and wrappers used by Wicket web apps to make fortress style security calls.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SecUtils
{
    private static final Logger LOG = Logger.getLogger( SecUtils.class.getName() );
    private static final String PERMS_CACHED = "perms.cached";
    public static final boolean IS_PERM_CACHED = ( ( Config.getInstance().getProperty( PERMS_CACHED ) != null ) && ( Config.getInstance()
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
        return CollectionUtils.isNotEmpty( permissions ) && permissions.contains( permission );
    }

    /**
     * Wrapper to fortress checkAccess api.
     *
     * @param component contains the wicket session handle.
     * @param accessMgr has the checkAccess api
     * @param objName string value
     * @param opName string value
     * @param objId string value
     * @return true if success, false otherwise.
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
     * @param component contains handle to wicket session.
     * @param j2eePolicyMgr used to call deserize api
     * @param accessMgr used to call fortress api for role op
     * @param szPrincipal contains the instance of fortress session deserialized.
     */
    public static void initializeSession(Component component, J2eePolicyMgr j2eePolicyMgr, AccessMgr accessMgr, String szPrincipal ) throws SecurityException
    {
        Session realmSession = null;

        if(j2eePolicyMgr == null || accessMgr == null)
        {
            throw new SecurityException( GlobalIds.SESSION_INITIALIZATION_FAILED, "initializeSession failed - verify the injection of fortress spring beans into your application" );
        }
        try
        {
            if( StringUtils.isNotEmpty( szPrincipal ))
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

    /**
     * Call RBAC addActiveRole to activate a new role into user's session.
     * This routine must first retrieves the wicket session.
     * It is needed because it contains the fortress session which is required for api.
     * Next it invokes the fortress addActiveRole method.
     * If all successful refresh user's perms cached as they've changed.
     *
     * @param component contains handle to wicket session.
     * @param target used to display modal if something goes wrong
     * @param accessMgr used to call fortress api for role op
     * @param roleName contains the role name target
     * @return true if success, false otherwise.
     */
    public static boolean addActiveRole( Component component, AjaxRequestTarget target, AccessMgr accessMgr, String roleName )
    {
        boolean isSuccessful = false;
        try
        {
            WicketSession session = ( WicketSession ) component.getSession();
            session.getSession().setWarnings( null );
            accessMgr.addActiveRole( session.getSession(), new UserRole( roleName ) );
            List<Warning> warnings = session.getSession().getWarnings();
            if ( CollectionUtils.isNotEmpty( warnings ) )
            {
                for ( Warning warning : warnings )
                {
                    LOG.info( "Warning: " + warning.getMsg() + " errCode: " + warning.getId() + " name: " + warning
                        .getName() + " type: " + warning.getType().toString() );
                    if ( warning.getType() == Warning.Type.ROLE && warning.getName().equalsIgnoreCase( roleName ) )
                    {
                        String error = warning.getMsg() + " code: " + warning.getId();
                        LOG.error( error );
                        target.appendJavaScript( ";alert('" + error + "');" );
                        return false;
                    }
                }
            }

            // User's active role set changed so refresh their permissions:
            SecUtils.getPermissions( component, accessMgr );
            isSuccessful = true;
            String message = "Activate role name: " + roleName + " successful";
            LOG.info( message );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String msg = "Role selection " + roleName + " activation failed because of ";
            if ( se.getErrorId() == GlobalErrIds.DSD_VALIDATION_FAILED )
            {
                msg += "Dynamic SoD rule violation";
            }
            else if ( se.getErrorId() == GlobalErrIds.URLE_ALREADY_ACTIVE )
            {
                msg += "Role already active in Session";
            }
            else
            {
                msg += "System error: " + se + ", " + "errId=" + se.getErrorId();
            }
            LOG.error( msg );
            target.appendJavaScript( ";alert('" + msg + "');" );
        }
        return isSuccessful;
    }

    /**
     * Call RBAC dropActiveRole to deactivate a new role from user's session.
     * This routine must first retrieves the wicket session.
     * It is needed because it contains the fortress session which is required for api.
     * Next it invokes the fortress dropActiveRole method.
     * If all successful refresh user's perms cached as they've changed.
     *
     * @param component contains handle to wicket session.
     * @param target used to display modal if something goes wrong
     * @param accessMgr used to call fortress api for role op
     * @param roleName contains the role name target
     * @return true if success, false otherwise.
     */
    public static boolean dropActiveRole( Component component, AjaxRequestTarget target, AccessMgr accessMgr, String roleName )
    {
        boolean isSuccessful = false;
        try
        {
            WicketSession session = ( WicketSession ) component.getSession();
            accessMgr.dropActiveRole( session.getSession(), new UserRole( roleName ) );
            // User's active role set changed so refresh their permissions:
            SecUtils.getPermissions( component, accessMgr );
            isSuccessful = true;
            LOG.info( "Fortress dropActiveRole roleName: " + roleName + " was successful" );
        }
        catch ( SecurityException se )
        {
            String msg = "Role selection " + roleName + " deactivation failed because of ";
            if ( se.getErrorId() == GlobalErrIds.URLE_NOT_ACTIVE )
            {
                msg += "Role not active in session";
            }
            else
            {
                msg += "System error: " + se + ", " + "errId=" + se.getErrorId();
            }
            LOG.error( msg );
            target.appendJavaScript( ";alert('" + msg + "');" );
        }
        return isSuccessful;
    }

    /**
     * Enables fortress session on behalf of a java.security.Principal retrieved from the container.
     *
     * @param component
     * @param servletReq
     * @param j2eePolicyMgr
     * @param accessMgr
     * @throws SecurityException
     */
    public static void enableFortress( Component component, HttpServletRequest servletReq, J2eePolicyMgr j2eePolicyMgr, AccessMgr accessMgr ) throws SecurityException
    {
        // Get the principal from the container:
        Principal principal = servletReq.getUserPrincipal();
        // Is this a Java EE secured page && has the User successfully authenticated already?
        boolean isSecured = principal != null;
        if(isSecured)
        {
            //linksLabel += " for " + principal.getName();
            if( !isLoggedIn( component ) )
            {
                String szPrincipal = principal.toString();
                // Pull the fortress session from the realm and assert into the Web app's session along with user's perms:
                SecUtils.initializeSession( component, j2eePolicyMgr, accessMgr, szPrincipal );
            }
        }
    }

    /**
     * If user has a wicket session then considered logged in.
     *
     * @return true if wicket session is not null
     */
    public static boolean isLoggedIn( Component component )
    {
        boolean isLoggedIn = false;
        if ( getSession( component ) != null )
        {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }

    public static Permission getPermFromId( String id )
    {
        Permission perm = null;
        String[] parts = id.split( "\\." );
        if( parts.length > 1)
        {
            String objName = parts[0];
            String opName = parts[1];
            perm = new Permission( objName, opName );
        }
        return perm;
    }

}
