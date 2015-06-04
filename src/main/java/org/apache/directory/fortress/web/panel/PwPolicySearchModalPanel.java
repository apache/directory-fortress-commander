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

package org.apache.directory.fortress.web.panel;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.PwPolicyMgr;
import org.apache.directory.fortress.core.model.PwPolicy;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class PwPolicySearchModalPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private PwPolicyMgr pwPolicyMgr;
    private static final Logger LOG = Logger.getLogger( PwPolicySearchModalPanel.class.getName() );
    private ModalWindow window;
    private PwPolicy policySelection;
    private String policySearchVal;


    /**
     * @param id
     */
    public PwPolicySearchModalPanel( String id, ModalWindow window )
    {
        super( id );
        // TODO: add later:
        //this.pwPolicyMgr.setAdmin( GlobalUtils.getSession( this ) );
        this.window = window;
        loadPanel();
    }


    public void loadPanel()
    {
        LoadableDetachableModel requests = getListViewModel();
        PageableListView policyView = createListView( requests );
        add( policyView );
        add( new AjaxPagingNavigator( "policynavigator", policyView ) );
    }


    private PageableListView createListView( final LoadableDetachableModel requests )
    {
        return new PageableListView( "policydataview", requests, 16 )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem( final ListItem item )
            {
                final PwPolicy modelObject = ( PwPolicy ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;


                    @Override
                    public void onClick( AjaxRequestTarget target )
                    {
                        policySelection = modelObject;
                        window.close( target );
                    }
                } );

                item.add( new Label( "name", new PropertyModel( item.getModel(), "name" ) ) );
                item.add( new Label( "minAge", new PropertyModel( item.getModel(), "minAge" ) ) );
                item.add( new Label( "maxAge", new PropertyModel( item.getModel(), "maxAge" ) ) );
                item.add( new Label( "inHistory", new PropertyModel( item.getModel(), "inHistory" ) ) );
                item.add( new Label( "minLength", new PropertyModel( item.getModel(), "minLength" ) ) );
                item.add( new Label( "expireWarning", new PropertyModel( item.getModel(), "expireWarning" ) ) );
                item.add( new Label( "graceLoginLimit", new PropertyModel( item.getModel(), "graceLoginLimit" ) ) );
                item.add( new Label( "lockout", new PropertyModel( item.getModel(), "lockout" ) ) );
                item.add( new Label( "lockoutDuration", new PropertyModel( item.getModel(), "lockoutDuration" ) ) );
                item.add( new Label( "maxFailure", new PropertyModel( item.getModel(), "maxFailure" ) ) );
                item.add( new Label( "failureCountInterval",
                    new PropertyModel( item.getModel(), "failureCountInterval" ) ) );
                item.add( new Label( "mustChange", new PropertyModel( item.getModel(), "mustChange" ) ) );
                item.add( new Label( "allowUserChange", new PropertyModel( item.getModel(), "allowUserChange" ) ) );
                item.add( new Label( "safeModify", new PropertyModel( item.getModel(), "safeModify" ) ) );

            }
        };
    }


    private LoadableDetachableModel getListViewModel()
    {
        return new LoadableDetachableModel()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected Object load()
            {
                List<PwPolicy> policies = null;
                try
                {
                    policySelection = null;
                    if ( policySearchVal == null )
                        policySearchVal = "";
                    policies = pwPolicyMgr.search( policySearchVal );
                    // sort list by name:
                    if( CollectionUtils.isNotEmpty( policies ))
                    {
                        Collections.sort( ( List<PwPolicy> ) policies, new Comparator<PwPolicy>()
                        {
                            @Override
                            public int compare(PwPolicy p1, PwPolicy p2)
                            {
                                return p1.getName().compareToIgnoreCase( p2.getName() );
                            }
                        } );
                    }
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return policies;
            }
        };
    }


    public PwPolicy getPolicySelection()
    {
        return policySelection;
    }


    public void setSearchVal( String searchVal )
    {
        this.policySearchVal = searchVal;
    }
}