/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;

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
