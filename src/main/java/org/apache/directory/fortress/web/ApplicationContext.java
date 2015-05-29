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


import org.apache.directory.fortress.web.control.WicketSession;
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
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ApplicationContext extends WebApplication
{
    @Override
    public Session newSession( Request request, Response response )
    {
        return new WicketSession( request );
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