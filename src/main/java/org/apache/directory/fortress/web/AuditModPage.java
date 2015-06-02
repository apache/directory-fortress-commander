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

import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.directory.fortress.web.panel.AuditModDetailPanel;
import org.apache.directory.fortress.web.panel.AuditModListPanel;
import org.apache.directory.fortress.web.panel.Displayable;
import org.apache.directory.fortress.web.panel.InfoPanel;
import org.apache.directory.fortress.core.model.UserAudit;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 8/10/13
 */
public class AuditModPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

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
        add(new Label( GlobalIds.PAGE_HEADER, "Audit Modification Viewer"));
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
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;
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
