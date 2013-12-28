/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

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
