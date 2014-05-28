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
package org.openldap.commander;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class ErrorPage extends CommanderBasePage
{
    public ErrorPage( Exception e )
    {
        add( new Label( "title", new Model<String>( "Runtime Exception Occurred" ) ) );
        add( new Label( "message", new Model<String>( e.getLocalizedMessage() ) ) );
        add( new BookmarkablePageLink( "homePage", getApplication().getHomePage() ) );
    }

    @Deprecated
    public ErrorPage( final PageParameters parameters )
    {
        add( new Label( "title", new Model<String>( parameters.get( "title" ).toString() ) ) );
        add( new Label( "message", new Model<String>( parameters.get( "message" ).toString() ) ) );
        add( new BookmarkablePageLink( "homePage", getApplication().getHomePage() ) );
    }
}
