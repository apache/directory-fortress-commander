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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.openldap.fortress.rbac.Permission;

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
