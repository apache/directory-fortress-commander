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
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openldap.commander.panel.Displayable;
import org.openldap.commander.panel.InfoPanel;
import org.openldap.commander.panel.NavPanel;
import org.openldap.commander.panel.RoleDetailPanel;
import org.openldap.commander.panel.RoleListPanel;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class RoleAdminPage extends CommanderBasePage
{
    private boolean isAdmin = true;
    private String label = "Admin Role Administration";
    private static final Logger LOG = Logger.getLogger( RoleAdminPage.class.getName() );

    /**
     * @author Shawn McKinney
     * @version $Rev$
     * @param parameters
     */
    public RoleAdminPage(PageParameters parameters)
    {
        String type = GlobalUtils.getPageType(parameters);
        add( new Label( GlobalIds.PAGE_HEADER, label ) );
        WebMarkupContainer container = new WebMarkupContainer( GlobalIds.LAYOUT );
        FourWaySplitter splitter = new FourWaySplitter();
        splitter.addBorderLayout( container );

        // Add the four necessary panels for Commander Page: 1. Nav,, 2. List, 3. Info, 4. Detail
        // Nav and Info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel( GlobalIds.NAVPANEL );

        // 2. List Panel:
        container.add(new AjaxLazyLoadPanel( GlobalIds.ROLELISTPANEL )
         {
           @Override
           public Component getLazyLoadComponent(String id)
           {
                return new RoleListPanel(id, isAdmin);
           }
         });

        // 3. Info Panel:
        InfoPanel infoPanel = new InfoPanel( GlobalIds.INFOPANEL );
        container.add( infoPanel );

        // 4. Detail Panel:
        Displayable display = infoPanel.getDisplay();
        RoleDetailPanel roleDetail = new RoleDetailPanel( GlobalIds.ROLEDETAILPANEL, display, isAdmin );
        container.add( roleDetail );

        container.add( navPanel );
        this.add( container );
    }
}