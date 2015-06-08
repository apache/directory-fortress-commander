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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.directory.fortress.web.panel.Displayable;
import org.apache.directory.fortress.web.panel.InfoPanel;
import org.apache.directory.fortress.web.panel.ObjectDetailPanel;
import org.apache.directory.fortress.web.panel.ObjectListPanel;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ObjectPage extends FortressWebBasePage
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private boolean isAdmin = false;


    public ObjectPage( PageParameters parameters )
    {
        String label = "Permission Object Page";
        add( new Label( GlobalIds.PAGE_HEADER, label ) );
        WebMarkupContainer container = new WebMarkupContainer( GlobalIds.LAYOUT );
        FourWaySplitter splitter = new FourWaySplitter();
        splitter.addBorderLayout( container );

        // Add the four necessary panels of Fortress Web Page: 1. Nav, 2. List, 3. Info, 4. Detail.
        // Nav and Info are generic and work across all entities, the others are specific to this entity type.

        // 1. Nav Panel:
        NavPanel navPanel = new NavPanel( GlobalIds.NAVPANEL );

        // 2. List Panel:
        container.add( new AjaxLazyLoadPanel( GlobalIds.OBJECTLISTPANEL )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public Component getLazyLoadComponent( String id )
            {
                return new ObjectListPanel( id, isAdmin );
            }
        } );

        // 3. Info Panel:
        InfoPanel infoPanel = new InfoPanel( GlobalIds.INFOPANEL );
        container.add( infoPanel );

        // 4. Detail Panel:
        Displayable display = infoPanel.getDisplay();
        ObjectDetailPanel objectDetail = new ObjectDetailPanel( GlobalIds.OBJECTDETAILPANEL, display, isAdmin );
        container.add( objectDetail );

        container.add( navPanel );
        this.add( container );
    }
}