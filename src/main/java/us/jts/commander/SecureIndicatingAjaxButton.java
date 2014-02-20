/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;
import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.AccessMgr;
import us.jts.fortress.rbac.Permission;

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