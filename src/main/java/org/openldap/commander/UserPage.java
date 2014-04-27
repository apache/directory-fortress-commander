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

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.openldap.commander.panel.Displayable;
import org.openldap.commander.panel.InfoPanel;
import org.openldap.commander.panel.NavPanel;
import org.openldap.commander.panel.UserDetailPanel;
import org.openldap.commander.panel.UserListPanel;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class UserPage extends CommanderBasePage
{

    public UserPage()
    {
        add(new Label(GlobalIds.PAGE_HEADER, "User Administration"));
        WebMarkupContainer container = new WebMarkupContainer(GlobalIds.LAYOUT);
        FourWaySplitter splitter = new FourWaySplitter();
        splitter.addBorderLayout(container);

        // Add the four necessary panels for Commander Page: 1. Nav,, 2. List, 3. Info, 4. Detail.
        // Nav and info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel(GlobalIds.NAVPANEL);
        container.add(navPanel);

        // 2. List Panel:
        container.add(new AjaxLazyLoadPanel("userlistpanel")
        {
          @Override
          public Component getLazyLoadComponent(String id)
          {
               return new UserListPanel(id);
          }
        });

        // 3. Info Panel:
        InfoPanel infoPanel = new InfoPanel( GlobalIds.INFOPANEL );
        Displayable display = infoPanel.getDisplay();
        container.add(infoPanel);

        // 4. Detail Panel:
        UserDetailPanel userDetail = new UserDetailPanel("userdetailpanel", display );
        container.add(userDetail);

        this.add(container);
    }
}