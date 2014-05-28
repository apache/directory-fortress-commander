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

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.handler.ComponentRenderingRequestHandler;
import org.apache.wicket.markup.html.pages.ExceptionErrorPage;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
@Deprecated
public class CommanderRequestCycleListener extends AbstractRequestCycleListener
{
    private static final Logger LOG = Logger.getLogger( CommanderRequestCycleListener.class.getName() );

    @Override
    public IRequestHandler onException( final RequestCycle cycle, final Exception e )
    {
        final Page errorPage;
        PageParameters errorParameters = new PageParameters();
        errorParameters.add( "title", "System Exception Occurred" );
        String error = "CommanderExceptionHandler caught ";
        if ( e instanceof PageExpiredException )
        {
            error += "PageExpiredException=" + e;
            errorParameters.add( "message", new StringResourceModel( "pageExpiredException", null ).getString() );
            errorPage = new ErrorPage( errorParameters );
        }
        else if ( e instanceof java.lang.RuntimeException )
        {
            error += "RuntimeException=" + e;
            errorParameters.add( "message", "Runtime Exception" );
            errorPage = new ErrorPage( errorParameters );
        }
        else if ( e instanceof WicketRuntimeException )
        {
            error += "PageExpiredException=" + e;
            errorParameters.add( "message", "Wicket Runtime Exception" );
            errorPage = new ErrorPage( errorParameters );
        }
        else
        {
            error += "UnknownException=" + e;
            errorParameters.add( "title", "Unknown Exception Occurred" );
            // Standard wicket error page:
            errorPage = new ExceptionErrorPage( e, null );
        }
        LOG.error( error );
        return new ComponentRenderingRequestHandler( errorPage );
    }
}
