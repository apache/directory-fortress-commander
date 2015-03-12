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
package org.apache.directory.fortress.web;


import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LogoutPage extends FortressWebBasePage
{
    private static final Logger LOG = Logger.getLogger( LogoutPage.class.getName() );
    public LogoutPage()
    {
        HttpServletRequest servletReq = (HttpServletRequest)getRequest().getContainerRequest();
        // invalidate the session and force the user to log back on:
        servletReq.getSession().invalidate();
        getSession().invalidate();
        setResponsePage( LoginPage.class );
        add(new Label("label1", "Select logout"));
    }
}