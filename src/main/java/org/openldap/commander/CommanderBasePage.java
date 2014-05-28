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
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.openldap.fortress.*;
import org.openldap.fortress.rbac.Session;
import org.openldap.fortress.rbac.Permission;
import org.openldap.fortress.rbac.User;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Principal;
import java.util.List;

/**
 * Base class for Commander Web.  This class initializes Fortress RBAC context and so contains a synchronized block.
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public abstract class CommanderBasePage extends WebPage
{
    @SpringBean
    private AccessMgr accessMgr;
    @SpringBean
    private DelAccessMgr delAccessMgr;
    private static final String CLS_NM = CommanderBasePage.class.getName();
    private static final Logger LOG = Logger.getLogger( CLS_NM );

    public CommanderBasePage()
    {
        SecureBookmarkablePageLink usersLink = new SecureBookmarkablePageLink( GlobalIds.USERS_PAGE, UserPage.class,
            GlobalIds.ROLE_USERS );
        add( usersLink );
        PageParameters parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink rolesLink = new SecureBookmarkablePageLink( GlobalIds.ROLES_PAGE, RolePage.class,
            parameters, GlobalIds.ROLE_ROLES );
        add( rolesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admrolesLink = new SecureBookmarkablePageLink( GlobalIds.ADMROLES_PAGE,
            RoleAdminPage.class, parameters, GlobalIds.ROLE_ADMINROLES );
        add( admrolesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink objectsLink = new SecureBookmarkablePageLink( GlobalIds.POBJS_PAGE,
            ObjectPage.class, parameters, GlobalIds.ROLE_PERMOBJS );
        add( objectsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admobjsLink = new SecureBookmarkablePageLink( GlobalIds.ADMPOBJS_PAGE,
            ObjectAdminPage.class, parameters, GlobalIds.ROLE_ADMINOBJS );
        add( admobjsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink permsLink = new SecureBookmarkablePageLink( GlobalIds.PERMS_PAGE, PermPage.class,
            parameters, GlobalIds.ROLE_PERMS );
        add( permsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admpermsLink = new SecureBookmarkablePageLink( GlobalIds.ADMPERMS_PAGE,
            PermAdminPage.class, parameters, GlobalIds.ROLE_ADMINPERMS );
        add( admpermsLink );
        SecureBookmarkablePageLink policiesLink = new SecureBookmarkablePageLink( GlobalIds.PWPOLICIES_PAGE,
            PwPolicyPage.class, GlobalIds.ROLE_POLICIES );
        add( policiesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.SSD );
        SecureBookmarkablePageLink ssdsLink = new SecureBookmarkablePageLink( GlobalIds.SSDS_PAGE,
            SdStaticPage.class, parameters, GlobalIds.ROLE_SSDS );
        add( ssdsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.DSD );
        SecureBookmarkablePageLink dsdsLink = new SecureBookmarkablePageLink( GlobalIds.DSDS_PAGE,
            SdDynamicPage.class, parameters, GlobalIds.ROLE_DSDS );
        add( dsdsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.USEROUS );
        SecureBookmarkablePageLink userouLink = new SecureBookmarkablePageLink( GlobalIds.USEROUS_PAGE,
            OuUserPage.class, parameters, GlobalIds.ROLE_USEROUS );
        add( userouLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, "PERMOUS" );
        SecureBookmarkablePageLink permouLink = new SecureBookmarkablePageLink( GlobalIds.PERMOUS_PAGE,
            OuPermPage.class, parameters, GlobalIds.ROLE_PERMOUS );
        add( permouLink );

        add( new SecureBookmarkablePageLink( GlobalIds.GROUP_PAGE, GroupPage.class,
            GlobalIds.ROLE_GROUPS ) );

        add( new SecureBookmarkablePageLink( GlobalIds.AUDIT_BINDS_PAGE, AuditBindPage.class,
            GlobalIds.ROLE_AUDIT_BINDS ) );

        add( new SecureBookmarkablePageLink( GlobalIds.AUDIT_AUTHZS_PAGE, AuditAuthzPage.class,
            GlobalIds.ROLE_AUDIT_AUTHZS ) );

        add( new SecureBookmarkablePageLink( GlobalIds.AUDIT_MODS_PAGE, AuditModPage.class,
            GlobalIds.ROLE_AUDIT_MODS ) );

        add( new Label( "footer", "Copyright (c) 1998-2014, The OpenLDAP Foundation. All Rights Reserved." ) );

        final Link actionLink = new Link( "logout" )
        {
            @Override
            public void onClick()
            {
                HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
                servletReq.getSession().invalidate();
                getSession().invalidate();
                setResponsePage( LaunchPage.class );
            }
        };
        add( actionLink );
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();

        // RBAC Security Processing:
        Principal principal = servletReq.getUserPrincipal();
        // Is this a Java EE secured page && has the User successfully authenticated already?
        boolean isSecured = principal != null;
        if( isSecured && !isLoggedIn( ) )
        {
            String szPrincipal = principal.toString();
            // Pull the RBAC session from the realm and assert into the Web app's session:
            Session realmSession = GlobalUtils.deserialize(szPrincipal, Session.class);

            // If this is null, app in container that cannot share rbac session with app, Must now create session manually:
            if(realmSession == null)
            {
                realmSession = GlobalUtils.createRbacSession( accessMgr, principal.getName() );
            }
            if(realmSession != null)
            {
                synchronized ( ( RbacSession ) RbacSession.get() )
                {
                    GlobalUtils.loadPermissionsIntoSession( delAccessMgr, realmSession );
                }
            }
            // give up
            else
            {
                throw new RuntimeException( "cannot create RBAC session for user: " + principal.getName() );
            }
        }
    }

    private boolean isLoggedIn( )
    {
        boolean isLoggedIn = false;
        if ( GlobalUtils.getRbacSession( this ) != null )
        {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }
}