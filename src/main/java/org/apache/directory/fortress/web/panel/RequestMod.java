/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.fortress.web.panel;


import java.io.Serializable;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 *          Date: 8/11/13
 */
class RequestMod implements Serializable
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

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

        return name.equals( that.name );

    }


    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
