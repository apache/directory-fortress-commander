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


import java.util.ArrayList;
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
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.core.DelReviewMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Role;
import org.apache.directory.fortress.core.model.UserAdminRole;
import org.apache.directory.fortress.core.model.UserRole;
import org.apache.directory.fortress.core.model.ConstraintUtil;
import org.apache.directory.fortress.core.model.Constraint;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoleSearchModalPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ReviewMgr reviewMgr;
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger( RoleSearchModalPanel.class.getName() );
    private ModalWindow window;
    private Constraint roleSelection;
    private String roleSearchVal;
    private boolean isAdmin;

    private boolean isParentSearch;


    /**
     * @param id
     */
    public RoleSearchModalPanel( String id, ModalWindow window, boolean isAdmin )
    {
        super( id );
        this.window = window;
        this.isAdmin = isAdmin;
        this.delReviewMgr.setAdmin( SecUtils.getSession( this ) );
        loadPanel();
    }


    public void loadPanel()
    {
        LoadableDetachableModel requests = getListViewModel();
        PageableListView roleView = createListView( requests );
        add( roleView );
        add( new AjaxPagingNavigator( "navigator", roleView ) );
    }


    private PageableListView createListView( final LoadableDetachableModel requests )
    {
        return new PageableListView( "dataview", requests, 16 )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem( final ListItem item )
            {
                final Constraint modelObject = ( Constraint ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;


                    @Override
                    public void onClick( AjaxRequestTarget target )
                    {
                        roleSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( "name", new PropertyModel( item.getModel(), "name" ) ) );
                item.add( new Label( "beginTime", new PropertyModel( item.getModel(), "beginTime" ) ) );
                item.add( new Label( "endTime", new PropertyModel( item.getModel(), "endTime" ) ) );
                item.add( new Label( "beginDate", new PropertyModel( item.getModel(), "beginDate" ) ) );
                item.add( new Label( "endDate", new PropertyModel( item.getModel(), "endDate" ) ) );
                item.add( new Label( "beginLockDate", new PropertyModel( item.getModel(), "beginLockDate" ) ) );
                item.add( new Label( "endLockDate", new PropertyModel( item.getModel(), "endLockDate" ) ) );
                item.add( new Label( "timeout", new PropertyModel( item.getModel(), "timeout" ) ) );
                item.add( new Label( "dayMask", new PropertyModel( item.getModel(), "dayMask" ) ) );
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
                List<?> roles = null;
                try
                {
                    roleSelection = null;
                    if ( roleSearchVal == null )
                        roleSearchVal = "";

                    if ( isParentSearch )
                    {
                        Role childRole = reviewMgr.readRole( new Role( roleSearchVal ) );
                        if ( childRole != null )
                        {
                            List<Role> parentRoles = new ArrayList<>();
                            for ( String role : childRole.getParents() )
                            {
                                Role parent = reviewMgr.readRole( new Role( role ) );
                                parentRoles.add( parent );
                            }
                            roles = parentRoles;
                        }
                    }
                    else if ( isAdmin )
                    {
                        roles = delReviewMgr.findRoles( roleSearchVal );
                    }

                    else
                        roles = reviewMgr.findRoles( roleSearchVal );
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }

                // sort list by name:
                if( CollectionUtils.isNotEmpty( roles ))
                {
                    Collections.sort( (List<Role>)roles, new Comparator<Role>()
                    {
                        @Override
                        public int compare(Role r1, Role r2)
                        {
                            return r1.getName().compareToIgnoreCase( r2.getName() );
                        }
                    } );
                }
                return roles;
            }
        };
    }

    public UserRole getRoleSelection()
    {
        UserRole userRoleSelection = new UserRole();
        if ( this.roleSelection != null )
        {
            userRoleSelection.setName( this.roleSelection.getName() );
            ConstraintUtil.copy( this.roleSelection, userRoleSelection );
        }
        return userRoleSelection;
    }


    public UserAdminRole getAdminRoleSelection()
    {
        UserAdminRole userRoleSelection = new UserAdminRole();
        if ( this.roleSelection != null )
        {
            userRoleSelection.setName( this.roleSelection.getName() );
            ConstraintUtil.copy( this.roleSelection, userRoleSelection );
        }
        return userRoleSelection;
    }


    public void setRoleSearchVal( String roleSearchVal )
    {
        this.roleSearchVal = roleSearchVal;
    }


    public boolean isAdmin()
    {
        return isAdmin;
    }


    public void setAdmin( boolean admin )
    {
        isAdmin = admin;
    }


    public boolean isParentSearch()
    {
        return isParentSearch;
    }


    public void setParentSearch( boolean parentSearch )
    {
        isParentSearch = parentSearch;
    }
}