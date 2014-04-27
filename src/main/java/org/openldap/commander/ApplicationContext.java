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
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.openldap.fortress.cfg.Config;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class ApplicationContext extends WebApplication
{
    @Override
    public Session newSession(Request request, Response response)
    {
        return new RbacSession(request);
    }

    @Override
    public void init()
    {
        super.init();
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        getRequestCycleListeners().add(new CommanderRequestCycleListener());
        getMarkupSettings().setStripWicketTags(true);

        //getApplicationSettings().setPageExpiredErrorPage(LaunchPage.class);
        //IResourceSettings.setThrowExceptionOnMissingResource(true);
        // mounting login page so that it can be referred to in the security constraint
        //mountPage( "/login", LoginPage.class );
        //mountPage( "/login", LoginPage.class );
        //mountPage( "/app/login", LoginPage.class );
        //mountBookmarkablePage( "/login", LoginPage.class );
    }

    public Class<? extends Page> getHomePage()
    {
        return LaunchPage.class;
    }
}