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

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;
import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.openldap.fortress.AccessMgr;
import org.openldap.fortress.rbac.Permission;

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
    @SpringBean
    AccessMgr accessMgr;

    private static final Logger LOG = Logger.getLogger(SecureIndicatingAjaxButton.class.getName());

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