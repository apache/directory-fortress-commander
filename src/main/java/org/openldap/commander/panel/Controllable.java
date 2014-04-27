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

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/16/13
 */
public interface Controllable
{
    public enum Operations
    {
        ADD,
        UPDATE,
        DELETE,
        SEARCH,
        EXPORT,
        CANCEL
    }

    public void setOperation(Operations operations);
    public Operations getOperation();
}
