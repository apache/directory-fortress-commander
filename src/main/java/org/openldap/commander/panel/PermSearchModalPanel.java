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
import org.openldap.fortress.ReviewMgr;
import org.openldap.fortress.rbac.Permission;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class PermSearchModalPanel extends Panel
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(PermSearchModalPanel.class.getName());
    private ModalWindow window;
    private Permission permSelection;
    private String objectSearchVal;
    private String opSearchVal;
    private boolean isAdmin;

    /**
     * @param id
     */
    public PermSearchModalPanel( String id, ModalWindow window, final boolean isAdmin )
    {
        super( id );
        this.reviewMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.isAdmin = isAdmin;
        this.window = window;
        loadPanel();
    }

    public void loadPanel()
    {
        LoadableDetachableModel requests = getListViewModel();
        PageableListView ouView = createListView( requests );
        add( ouView );
        add( new AjaxPagingNavigator( "navigator", ouView ) );
    }

    private PageableListView createListView( final LoadableDetachableModel requests )
    {
        final PageableListView listView = new PageableListView( "dataview", requests, 16 )
        {
            @Override
            protected void populateItem( final ListItem item )
            {
                final Permission modelObject = ( Permission ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        permSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( "objName", new PropertyModel( item.getModel(), "objName" ) ) );
                item.add( new Label( "objId", new PropertyModel( item.getModel(), "objId" ) ) );
                item.add( new Label( "opName", new PropertyModel( item.getModel(), "opName" ) ) );
                item.add( new Label( "type", new PropertyModel( item.getModel(), "type" ) ) );
                item.add( new Label( "admin", new PropertyModel( item.getModel(), "admin" ) ) );
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
                List<?> objects = null;
                try
                {
                    permSelection = null;
                    if(objectSearchVal == null)
                        objectSearchVal = "";

                    Permission permission = new Permission( objectSearchVal, "" );
                    permission.setAdmin( isAdmin );
                    objects = reviewMgr.findPermissions( permission );
                }
                catch ( org.openldap.fortress.SecurityException se )
                {
                    String error = "getListViewModel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return objects;
            }
        };
        return ret;
    }

    public void setAdmin( boolean admin )
    {
        isAdmin = admin;
    }

    public Permission getSelection()
    {
        return permSelection;
    }

    public void setSearchVal( String objectSearchVal )
    {
        this.objectSearchVal = objectSearchVal;
    }
}