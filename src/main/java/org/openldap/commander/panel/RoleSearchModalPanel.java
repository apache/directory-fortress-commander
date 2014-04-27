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
package org.openldap.commander.panel;

import java.util.ArrayList;
import java.util.List;

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
import org.openldap.commander.GlobalUtils;
import org.openldap.fortress.DelReviewMgr;
import org.openldap.fortress.ReviewMgr;
import org.openldap.fortress.rbac.Role;
import org.openldap.fortress.rbac.UserAdminRole;
import org.openldap.fortress.rbac.UserRole;
import org.openldap.fortress.util.time.CUtil;
import org.openldap.fortress.util.time.Constraint;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class RoleSearchModalPanel extends Panel
{
    @SpringBean
    private ReviewMgr reviewMgr;
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger(RoleSearchModalPanel.class.getName());
    private ModalWindow window;
    private Constraint roleSelection;
    private String roleSearchVal;
    private boolean isAdmin;

    private boolean isParentSearch;
    /**
     * @param id
     */
    public RoleSearchModalPanel( String id, ModalWindow window, boolean isAdmin)
    {
        super( id );
        this.window = window;
        this.isAdmin = isAdmin;
        this.delReviewMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
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
        final PageableListView listView = new PageableListView( "dataview", requests, 16 )
        {
            @Override
            protected void populateItem( final ListItem item )
            {
                final Constraint modelObject = ( Constraint ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
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
        return listView;
    }

    private LoadableDetachableModel getListViewModel()
    {
        final LoadableDetachableModel ret = new LoadableDetachableModel()
        {
            @Override
            protected Object load()
            {
                List<?> roles = null;
                try
                {
                    roleSelection = null;
                    if(roleSearchVal == null)
                        roleSearchVal = "";

                    if(isParentSearch)
                     {
                         Role childRole = reviewMgr.readRole( new Role(roleSearchVal) );
                         if(childRole != null)
                         {
                             List<Role> parentRoles = new ArrayList<Role>();
                             for(String role : childRole.getParents() )
                             {
                                 Role parent = reviewMgr.readRole( new Role(role) );
                                 parentRoles.add( parent );
                             }
                             roles = parentRoles;
                         }
                     }
                     else if(isAdmin)
                    {
                        roles = delReviewMgr.findRoles( roleSearchVal );
                    }

                    else
                        roles = reviewMgr.findRoles( roleSearchVal );
                }
                catch ( org.openldap.fortress.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return roles;
            }
        };
        return ret;
    }

    public UserRole getRoleSelection()
    {
        UserRole userRoleSelection = new UserRole(  );
        if(this.roleSelection != null)
        {
            userRoleSelection.setName( this.roleSelection.getName() );
            CUtil.copy(this.roleSelection, userRoleSelection);
        }
        return userRoleSelection;
    }

    public UserAdminRole getAdminRoleSelection()
    {
        UserAdminRole userRoleSelection = new UserAdminRole(  );
        if(this.roleSelection != null)
        {
            userRoleSelection.setName( this.roleSelection.getName() );
            CUtil.copy(this.roleSelection, userRoleSelection);
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