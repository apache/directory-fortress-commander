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


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ErrorPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    public ErrorPage( Exception e )
    {
        add( new Label( "title", new Model<>( "Runtime Exception Occurred" ) ) );
        add( new Label( "message", new Model<>( "Operation Failed" ) ) );
        /*add( new Label( "message", new Model<>( e.getLocalizedMessage() ) ) );*/
        add( new BookmarkablePageLink( "homePage", getApplication().getHomePage() ) );
    }

    @Deprecated
    public ErrorPage( final PageParameters parameters )
    {
        add( new Label( "title", new Model<>( parameters.get( "title" ).toString() ) ) );
        add( new Label( "message", new Model<>( parameters.get( "message" ).toString() ) ) );
        add( new BookmarkablePageLink( "homePage", getApplication().getHomePage() ) );
    }
}
