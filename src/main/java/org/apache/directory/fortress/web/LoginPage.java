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
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.directory.fortress.core.util.VUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public final class LoginPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( LoginPage.class.getName() );


    public LoginPage()
    {
        LoginPageForm loginForm = new LoginPageForm( "loginFields" );
        add( loginForm );
    }

    public class LoginPageForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
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
                if ( StringUtils.isNotEmpty( userId ) && StringUtils.isNotEmpty( pswdField ) )
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
                        getRequestCycle().replaceAllRequestHandlers( new RedirectRequestHandler( "/login/error.html" ) );
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