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

import java.io.Serializable;

/**
 * @author Shawn McKinney
 * @version $Rev$
 *          Date: 8/11/13
 */
class RequestMod implements Serializable
{
    enum TYPE
    {
        ADD,
        UPDATE,
        DELETE,
        UNKNOWN
    }

    private int index;
    private TYPE type;
    private String name;
    private String value;

    RequestMod( int index, String name, String value )
    {
        this.type = type;
        this.index = index;
        this.name = name;
        this.value = value;
    }

    RequestMod( String name )
    {
        this.name = name;
    }

    public int getIndex()
    {
        return index;
    }

    public TYPE getType()
    {
        return type;
    }

    public void setType( TYPE type )
    {
        this.type = type;
    }

    public void setIndex( int index )
    {
        this.index = index;

    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        RequestMod that = ( RequestMod ) o;

        if ( !name.equals( that.name ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
