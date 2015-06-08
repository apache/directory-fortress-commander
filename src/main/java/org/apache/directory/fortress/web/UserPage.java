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
import org.apache.directory.fortress.web.panel.Displayable;
import org.apache.directory.fortress.web.panel.InfoPanel;
import org.apache.directory.fortress.web.panel.UserDetailPanel;
import org.apache.directory.fortress.web.panel.UserListPanel;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class UserPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;


    public UserPage()
    {
        add( new Label( GlobalIds.PAGE_HEADER, "User Administration" ) );
        WebMarkupContainer container = new WebMarkupContainer( GlobalIds.LAYOUT );
        FourWaySplitter splitter = new FourWaySplitter();
        splitter.addBorderLayout( container );

        // Add the four necessary panels of Fortress Web Page: 1. Nav, 2. List, 3. Info, 4. Detail.
        // Nav and info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel( GlobalIds.NAVPANEL );
        container.add( navPanel );

        // 2. List Panel:
        container.add( new AjaxLazyLoadPanel( "userlistpanel" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public Component getLazyLoadComponent( String id )
            {
                return new UserListPanel( id );
            }
        } );

        // 3. Info Panel:
        InfoPanel infoPanel = new InfoPanel( GlobalIds.INFOPANEL );
        Displayable display = infoPanel.getDisplay();
        container.add( infoPanel );

        // 4. Detail Panel:
        UserDetailPanel userDetail = new UserDetailPanel( "userdetailpanel", display );
        container.add( userDetail );

        this.add( container );
    }
}