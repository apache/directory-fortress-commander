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
