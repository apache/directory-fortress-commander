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

package org.openldap.commander.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/20/13
 */
public interface Displayable
{
    public void setMessage(String message);
    public void display(AjaxRequestTarget target);
    public void display();
}
