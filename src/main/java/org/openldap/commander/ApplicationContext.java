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


import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class ApplicationContext extends WebApplication
{
    @Override
    public Session newSession( Request request, Response response )
    {
        return new RbacSession( request );
    }

    @Override
    public void init()
    {
        super.init();
        getComponentInstantiationListeners().add( new SpringComponentInjector( this ) );

        // Catch runtime exceptions this way:
        getRequestCycleListeners().add( new AbstractRequestCycleListener()
        {
            @Override
            public IRequestHandler onException( RequestCycle cycle, Exception e )
            {
                return new RenderPageRequestHandler( new PageProvider( new ErrorPage( e ) ) );
            }
        } );
        getMarkupSettings().setStripWicketTags( true );
    }

    public Class<? extends Page> getHomePage()
    {
        return LaunchPage.class;
    }
}