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
import org.openldap.commander.panel.AuditModDetailPanel;
import org.openldap.commander.panel.AuditModListPanel;
import org.openldap.commander.panel.Displayable;
import org.openldap.commander.panel.InfoPanel;
import org.openldap.commander.panel.NavPanel;
import org.openldap.fortress.rbac.UserAudit;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 8/10/13
 */
public class AuditModPage extends CommanderBasePage
{
    boolean firstLoad = true;

    public AuditModPage()
    {
        UserAudit userAudit = new UserAudit();
        init( userAudit );
    }

    public AuditModPage(final UserAudit userAudit)
    {
        boolean firstLoad = false;
        init( userAudit );
    }

    private void init(final UserAudit userAudit)
    {
        add(new Label(GlobalIds.PAGE_HEADER, "Audit Modification Viewer"));
        WebMarkupContainer container = new WebMarkupContainer(GlobalIds.LAYOUT);
        FourWaySplitter splitter = new FourWaySplitter("55", "45");
        splitter.addBorderLayout(container);

        // Add the four necessary panels for Commander Page: 1. Nav,, 2. Info, 3. Detail, 4. List, .
        // Nav and Info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel( GlobalIds.NAVPANEL );

        // 2. Info Panel:
        InfoPanel infoPanel;
        if( firstLoad )
        {
            infoPanel = new InfoPanel(GlobalIds.INFOPANEL);
        }
        else
        {
            infoPanel = new InfoPanel(GlobalIds.INFOPANEL, "searching modification records...");
        }

        container.add(infoPanel);

        // 3. Detail Panel:
        Displayable display = infoPanel.getDisplay();
        AuditModDetailPanel modDetail = new AuditModDetailPanel("moddetailpanel", display);
        container.add(modDetail);

        container.add(navPanel);

        // 4. List Panel:
        container.add(new AjaxLazyLoadPanel("modlistpanel")
         {
           @Override
           public Component getLazyLoadComponent(String id)
           {
                //return new AuditAuthzListPanel(id);
               return new AuditModListPanel( id, userAudit );
           }
         });

        this.add(container);
    }
}
