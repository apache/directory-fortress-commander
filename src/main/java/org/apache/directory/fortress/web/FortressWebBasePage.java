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


import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.util.Config;
import org.apache.directory.fortress.realm.J2eePolicyMgr;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.control.SecureBookmarkablePageLink;
import org.apache.directory.fortress.web.control.WicketSession;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.model.Session;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


/**
 * This base class is extended by all pages in the fortress web.  It contains the security session and links for user.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class FortressWebBasePage extends WebPage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private AccessMgr accessMgr;
    @SpringBean
    private DelAccessMgr delAccessMgr;
    @SpringBean
    private J2eePolicyMgr j2eePolicyMgr;
    private static final String CLS_NM = FortressWebBasePage.class.getName();
    private static final Logger LOG = Logger.getLogger( CLS_NM );


    /**
     * Default constructor puts page links onto the page, gets or initializes the user's fortress rbac session, and loads it into the wicket session.
     */
    public FortressWebBasePage()
    {
        // Build the title bar string.
        StringBuilder titlebuf = new StringBuilder();
        titlebuf.append( "Fortress Web Administration" );
        String szContextId = Config.getInstance().getProperty( GlobalIds.CONTEXT_ID_PROPERTY );
        // append the tenantId if set
        if( StringUtils.isNotEmpty( szContextId ) && !szContextId.equalsIgnoreCase( org.apache.directory.fortress.core.GlobalIds.HOME ))
        {
            titlebuf.append( " : " );
            titlebuf.append( szContextId );
        }
        // add it to title bar of page
        add( new Label( org.apache.directory.fortress.web.common.GlobalIds.TITLE_BAR, titlebuf.toString() ) );

        SecureBookmarkablePageLink usersLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web
            .common.GlobalIds.USERS_PAGE, UserPage.class,
            org.apache.directory.fortress.web.common.GlobalIds.ROLE_USERS );
        add( usersLink );
        PageParameters parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink rolesLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.ROLES_PAGE, RolePage.class,
            parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_ROLES );
        add( rolesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admrolesLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.ADMROLES_PAGE,
            RoleAdminPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_ADMINROLES );
        add( admrolesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink objectsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.POBJS_PAGE,
            ObjectPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_PERMOBJS );
        add( objectsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admobjsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.ADMPOBJS_PAGE,
            ObjectAdminPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_ADMINOBJS );
        add( admobjsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.RBAC_TYPE );
        SecureBookmarkablePageLink permsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.PERMS_PAGE, PermPage.class,
            parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_PERMS );
        add( permsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.ADMIN_TYPE );
        SecureBookmarkablePageLink admpermsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.ADMPERMS_PAGE,
            PermAdminPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_ADMINPERMS );
        add( admpermsLink );
        SecureBookmarkablePageLink policiesLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.PWPOLICIES_PAGE,
            PwPolicyPage.class, org.apache.directory.fortress.web.common.GlobalIds.ROLE_POLICIES );
        add( policiesLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.SSD );
        SecureBookmarkablePageLink ssdsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.SSDS_PAGE,
            SdStaticPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_SSDS );
        add( ssdsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.DSD );
        SecureBookmarkablePageLink dsdsLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.DSDS_PAGE,
            SdDynamicPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_DSDS );
        add( dsdsLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, GlobalIds.USEROUS );
        SecureBookmarkablePageLink userouLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.USEROUS_PAGE,
            OuUserPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_USEROUS );
        add( userouLink );
        parameters = new PageParameters();
        //parameters.set( GlobalIds.PAGE_TYPE, "PERMOUS" );
        SecureBookmarkablePageLink permouLink = new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.PERMOUS_PAGE,
            OuPermPage.class, parameters, org.apache.directory.fortress.web.common.GlobalIds.ROLE_PERMOUS );
        add( permouLink );

        add( new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.GROUP_PAGE, GroupPage.class,
            org.apache.directory.fortress.web.common.GlobalIds.ROLE_GROUPS ) );

        // The audit pages only work with OpenLDAP:
        if ( Config.getInstance().isOpenldap() )
        {
            add( new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_BINDS_PAGE, AuditBindPage.class,
                org.apache.directory.fortress.web.common.GlobalIds.ROLE_AUDIT_BINDS ) );

            add( new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_AUTHZS_PAGE, AuditAuthzPage.class,
                org.apache.directory.fortress.web.common.GlobalIds.ROLE_AUDIT_AUTHZS ) );

            add( new SecureBookmarkablePageLink( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_MODS_PAGE, AuditModPage.class,
                org.apache.directory.fortress.web.common.GlobalIds.ROLE_AUDIT_MODS ) );
        }
        else
        {
            // Only supported for openldap so set dummy links to be invisible.
            add( new Label( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_BINDS_PAGE, "" ).setVisible( false ) );
            add( new Label( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_AUTHZS_PAGE, "" ).setVisible( false ) );
            add( new Label( org.apache.directory.fortress.web.common.GlobalIds.AUDIT_MODS_PAGE, "" ).setVisible( false ) );
        }

        add( new Label( "footer", "Copyright (c) 2003-2018, The Apache Software Foundation. All Rights Reserved." ) );

        final Link actionLink = new Link( "logout" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage( LogoutPage.class );
            }
        };
        add( actionLink );
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();

        // RBAC Security Processing:
        Principal principal = servletReq.getUserPrincipal();
        // Is this a Java EE secured page && has the User successfully authenticated already?
        boolean isSecured = principal != null;
        if ( isSecured && !isLoggedIn() )
        {
            // Here the principal was created by fortress realm and is a serialized instance of {@link Session}.
            String szPrincipal = principal.toString();
            Session session = null;

            String szIsJetty = System.getProperty( org.apache.directory.fortress.web.common.GlobalIds.IS_JETTY_SERVER );
            boolean isJetty = false;
            if( StringUtils.isNotEmpty( szIsJetty ))
            {
                if ( szIsJetty.equalsIgnoreCase( "true" ) )
                {
                    isJetty = true;
                }
            }
            if( !isJetty )
            {
                try
                {
                    // Deserialize the principal string into a fortress session:
                    session = j2eePolicyMgr.deserialize( szPrincipal );
                }
                catch(SecurityException se)
                {
                    // Can't recover....
                    throw new RuntimeException( se );
                }
            }

            // If this is null, it means this app cannot share an rbac session with container and must now (re)create session here:
            if ( session == null )
            {
                session = SecUtils.createSession( accessMgr, principal.getName() );
            }

            // Now load the fortress session into the Wicket session and let wicket hold onto that for us.  Also retreive the arbac perms from server and cache those too.
            synchronized ( ( WicketSession ) WicketSession.get() )
            {
                SecUtils.loadPermissionsIntoSession( delAccessMgr, session );
            }
        }
    }


    /**
     * Return true if the fortress session is cached within the wicket session object.
     *
     * @return true if logged in, false otherwise.
     */
    private boolean isLoggedIn()
    {
        boolean isLoggedIn = false;
        if ( SecUtils.getSession( this ) != null )
        {
            isLoggedIn = true;
        }
        return isLoggedIn;
    }
}