/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
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

    public SecureIndicatingAjaxButton( String id, String objectName, String opName )
    {
        super( id );
        if(!GlobalUtils.isFound( new Permission(objectName, opName), this ))
            setVisible( false );
    }

    public SecureIndicatingAjaxButton( String id, String roleName )
    {
        super( id );
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
        if( ! GlobalUtils.isAuthorized( roleName, servletReq ) )
            setVisible( false );
    }

    public SecureIndicatingAjaxButton( String id )
    {
        super( id );
        try
        {
            boolean result = accessMgr.checkAccess( GlobalUtils.getRbacSession( this ), new Permission("test", "test") );
            if(!result)
                setVisible( false );
        }
        catch ( us.jts.fortress.SecurityException se )
        {
            String error = "Fortress Security Exception=" + se;
            LOG.error( error );
            //throw new RuntimeException("SecurityIndicatingAjaxButton caught Exception=" + e);
        }
    }
}