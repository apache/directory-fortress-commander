/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;


import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import us.jts.fortress.cfg.Config;

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