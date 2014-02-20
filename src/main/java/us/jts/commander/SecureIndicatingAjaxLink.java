/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
*/
package us.jts.commander;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import us.jts.fortress.rbac.Permission;

/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class SecureIndicatingAjaxLink extends IndicatingAjaxLink
{
    public SecureIndicatingAjaxLink( String id, String objName, String opName  )
    {
        super( id );
        if(!GlobalUtils.isFound( new Permission(objName, opName), this ))
            setEnabled( false );
    }

    @Override
    public void onClick( AjaxRequestTarget target )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
