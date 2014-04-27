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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.openldap.fortress.util.attr.VUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public final class LoginPage extends CommanderBasePage
{
    private static final Logger LOG = Logger.getLogger( LoginPage.class.getName() );

    public LoginPage()
    {
        LoginPageForm loginForm = new LoginPageForm( "loginFields" );
        add( loginForm );
    }

    public class LoginPageForm extends Form
    {
        private String pswdField;
        private String userId;

        public LoginPageForm( String id )
        {
            super( id );
            HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
            Principal principal = servletReq.getUserPrincipal();
            if ( principal != null )
            {
                LOG.info( "user already logged in, route to launch page instead" );
                setResponsePage( LaunchPage.class );
            }
            add( new Button( "login" ) );
            TextField userId = new TextField( "userId", new PropertyModel<String>( this, "userId" ) );
            add( userId );
            PasswordTextField pw = new PasswordTextField( "pswdField", new PropertyModel<String>( this, "pswdField" ) );
            pw.setRequired( false );
            add( pw );
        }

        @Override
        protected void onSubmit()
        {
            System.out.println( "form was submitted!" );
            HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
            Principal principal = servletReq.getUserPrincipal();
            if ( principal == null )
            {
                if ( VUtil.isNotNullOrEmpty( userId ) && VUtil.isNotNullOrEmpty( pswdField ) )
                {
                    try
                    {
                        servletReq.login( userId, pswdField );
                        setResponsePage( LaunchPage.class );
                    }
                    catch ( ServletException se )
                    {
                        String error = "Login form caught ServletException=" + se;
                        LOG.error( error );
                        getRequestCycle().replaceAllRequestHandlers(new RedirectRequestHandler("/login/error.html"));
                    }
                }
                else
                {
                    LOG.debug( "null userid or password detected" );
                }
            }
            else
            {
                setResponsePage( LaunchPage.class );
            }
        }
    }
}