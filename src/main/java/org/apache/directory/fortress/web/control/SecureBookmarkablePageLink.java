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


import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;


/**
 * ...
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SecureBookmarkablePageLink extends BookmarkablePageLink
{
    public <C extends Page> SecureBookmarkablePageLink( String id, Class<C> pageClass, String roleName )
    {
        super( id, pageClass );
        if(!isAuthorized( roleName ))
        {
            setVisible( false );
        }
    }

    public <C extends Page> SecureBookmarkablePageLink( String id, Class<C> pageClass, PageParameters parameters,
        String roleName )
    {
        super( id, pageClass, parameters );
        if ( !isAuthorized( roleName ) )
        {
            setVisible( false );
        }
    }

    private boolean isAuthorized( String roleName )
    {
        HttpServletRequest servletReq = ( HttpServletRequest ) getRequest().getContainerRequest();
        return isAuthorized( roleName, servletReq );
    }

    private boolean isAuthorized( String roleNames, HttpServletRequest servletReq )
    {
        boolean isAuthorized = false;
        StringTokenizer tokenizer = new StringTokenizer( roleNames, "," );
        if (tokenizer.countTokens() > 0)
        {
            while (tokenizer.hasMoreTokens())
            {
                String roleName = tokenizer.nextToken();
                isAuthorized = SecUtils.isAuthorized( roleName, servletReq );
            }
        }
        return isAuthorized;
    }
}
