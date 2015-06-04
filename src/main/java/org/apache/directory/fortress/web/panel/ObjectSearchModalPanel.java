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
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.PermObj;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ObjectSearchModalPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger( ObjectSearchModalPanel.class.getName() );
    private ModalWindow window;
    private PermObj objectSelection;
    private String objectSearchVal;
    private boolean isAdmin;


    /**
     * @param id
     */
    public ObjectSearchModalPanel( String id, ModalWindow window, final boolean isAdmin )
    {
        super( id );
        this.reviewMgr.setAdmin( SecUtils.getSession( this ) );
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
        return new PageableListView( "dataview", requests, 16 )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem( final ListItem item )
            {
                final PermObj modelObject = ( PermObj ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;


                    @Override
                    public void onClick( AjaxRequestTarget target )
                    {
                        objectSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( "objName", new PropertyModel( item.getModel(), "objName" ) ) );
                item.add( new Label( "description", new PropertyModel( item.getModel(), "description" ) ) );
                item.add( new Label( "ou", new PropertyModel( item.getModel(), "ou" ) ) );
                item.add( new Label( "type", new PropertyModel( item.getModel(), "type" ) ) );
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
                List<?> objects = null;
                try
                {
                    objectSelection = null;
                    if ( objectSearchVal == null )
                        objectSearchVal = "";

                    PermObj permObj = new PermObj( objectSearchVal );
                    permObj.setAdmin( isAdmin );
                    objects = reviewMgr.findPermObjs( permObj );
                    // sort list by objName:
                    if( CollectionUtils.isNotEmpty( objects ))
                    {
                        Collections.sort( ( List<PermObj> ) objects, new Comparator<PermObj>()
                        {
                            @Override
                            public int compare(PermObj p1, PermObj p2)
                            {
                                return p1.getObjName().compareToIgnoreCase( p2.getObjName() );
                            }
                        } );
                    }

                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return objects;
            }
        };
    }


    public void setAdmin( boolean admin )
    {
        isAdmin = admin;
    }


    public PermObj getSelection()
    {
        return objectSelection;
    }


    public void setSearchVal( String objectSearchVal )
    {
        this.objectSearchVal = objectSearchVal;
    }
}