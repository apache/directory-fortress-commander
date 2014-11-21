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
 * Created with IntelliJ IDEA.
 * User: smckinn
 * Date: 26/05/14
 * Time: 22:05
 * To change this template use File | Settings | File Templates.
 */
public class Member implements Serializable
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private String userDn;
    private int index;


    public String getUserDn()
    {
        return userDn;
    }


    public void setUserDn( String userDn )
    {
        this.userDn = userDn;
    }


    public int getIndex()
    {
        return index;
    }


    public void setIndex( int index )
    {
        this.index = index;
    }
}
