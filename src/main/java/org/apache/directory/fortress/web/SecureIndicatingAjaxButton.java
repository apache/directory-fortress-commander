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

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;
import org.apache.directory.fortress.core.rbac.Permission;

import javax.servlet.http.HttpServletRequest;

/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
@Authorizable
public class SecureIndicatingAjaxButton extends IndicatingAjaxButton
{
    public SecureIndicatingAjaxButton( String id, String objName, String opName )
    {
        super( id );
        if(!GlobalUtils.isFound( new Permission(objName, opName), this ))
            setVisible( false );
    }

    public SecureIndicatingAjaxButton( String id, String roleName )
    {
        super( id );
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
        if( ! GlobalUtils.isAuthorized( roleName, servletReq ) )
            setVisible( false );
    }
}