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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.directory.fortress.web.panel.Displayable;
import org.apache.directory.fortress.web.panel.InfoPanel;
import org.apache.directory.fortress.web.panel.RoleDetailPanel;
import org.apache.directory.fortress.web.panel.RoleListPanel;
import org.apache.wicket.util.string.StringValue;

import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoleAdminPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private boolean isAdmin = true;
    private static final Logger LOG = LoggerFactory.getLogger( RoleAdminPage.class.getName() );


    /**
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$
     * @param parameters
     */
    public RoleAdminPage( PageParameters parameters )
    {
        String label = "Admin Role Administration";
        add( new Label( GlobalIds.PAGE_HEADER, label ) );
        WebMarkupContainer container = new WebMarkupContainer( GlobalIds.LAYOUT );
        FourWaySplitter splitter = new FourWaySplitter();
        splitter.addBorderLayout( container );

        // Add the four necessary panels of Fortress Web Page: 1. Nav, 2. List, 3. Info, 4. Detail.
        // Nav and Info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel( GlobalIds.NAVPANEL );

        // 2. List Panel:
        container.add( new AjaxLazyLoadPanel( GlobalIds.ROLELISTPANEL )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public Component getLazyLoadComponent( String id )
            {
                return new RoleListPanel( id, isAdmin );
            }
        } );

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

    /**
     *
     * @param parameters
     * @return
     */
    private String getPageType( PageParameters parameters )
    {
        String pageType = null;
        if ( parameters != null )
        {
            List<StringValue> values = parameters.getValues( GlobalIds.PAGE_TYPE );
            if ( values != null && values.size() > 0 )
            {
                pageType = values.get( 0 ).toString();
            }
        }
        return pageType;
    }
}
