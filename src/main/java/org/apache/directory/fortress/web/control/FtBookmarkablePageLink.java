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


import org.apache.directory.fortress.core.model.Permission;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;


/**
 * This link requires the id format be: objname.operationname
 * where name maps to ft perm obj name and op maps to ft perm op name.
 * If match not found link will quietly not display the link on page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class FtBookmarkablePageLink extends BookmarkablePageLink
{
    private static final Logger LOG = Logger.getLogger( FtBookmarkablePageLink.class.getName() );
    Permission perm;

    public <C extends Page> FtBookmarkablePageLink( String id, Class<C> pageClass )
    {
        super( id, pageClass );
        perm = SecUtils.getPermFromId( id );
        if ( perm != null && SecUtils.isFound( perm, this ) )
        {
            LOG.debug( "FtBookmarkablePageLink id: " + id + ", status found" );
        }
        else
        {
            LOG.debug( "FtBookmarkablePageLink id: " + id + ", status NOT found" );
            setVisible( false );
        }
    }
}
