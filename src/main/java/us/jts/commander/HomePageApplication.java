/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.wicket.Page;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class HomePageApplication extends ApplicationContext
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage()
	{
        return LaunchPage.class;
	}

	@Override
	public void init()
	{
		super.init();
        //mountPage("index.html", LoginPage.class);
        mountPage("index.html", LaunchPage.class);
        mountPage("home.html", LaunchPage.class);
	}
}
