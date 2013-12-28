/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;


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
    public ErrorPage( final PageParameters parameters )
    {
        add( new Label( "title", new Model<String>( parameters.get( "title" ).toString() ) ) );
        add( new Label( "message", new Model<String>( parameters.get( "message" ).toString() ) ) );
        add( new BookmarkablePageLink( "homePage", getApplication().getHomePage() ) );
    }
}
