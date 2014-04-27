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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletRequest;

/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class SecureBookmarkablePageLink extends BookmarkablePageLink
{
    public <C extends Page> SecureBookmarkablePageLink( String id, Class<C> pageClass, String roleName )
    {
        super( id, pageClass );
        if(!isAuthorized( roleName ))
        {
            setVisible( false );
        }
    }

    public <C extends Page> SecureBookmarkablePageLink( String id, Class<C> pageClass, PageParameters parameters, String roleName )
    {
        super( id, pageClass, parameters );
        if(!isAuthorized( roleName ))
        {
            setVisible( false );
        }
    }

    private boolean isAuthorized( String roleName )
    {
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
        return GlobalUtils.isAuthorized( roleName, servletReq );
    }
}
