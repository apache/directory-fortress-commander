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
package org.apache.directory.fortress.web.control;


import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;

import java.util.List;


/**
 * This object is managed by wicket framework.  It is used to cache a copy of a user's session and permissions.
 *
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class WicketSession extends WebSession
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Session session;
    private List<Permission> permissions;


    /**
     * Constructor. Note that {@link org.apache.wicket.request.cycle.RequestCycle} is not available until this
     * constructor returns.
     *
     * @param request The current request
     */
    public WicketSession(Request request)
    {
        super( request );
    }


    public Session getSession()
    {
        return session;
    }


    public void setSession(Session session)
    {
        this.session = session;
    }


    public List<Permission> getPermissions()
    {
        return permissions;
    }


    public void setPermissions( List<Permission> permissions )
    {
        this.permissions = permissions;
    }
}
