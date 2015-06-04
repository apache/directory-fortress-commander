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
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.User;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class UserSearchModalPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger( UserSearchModalPanel.class.getName() );
    private ModalWindow window;
    private User userSelection;
    private String userSearchVal;


    /**
     * @param id
     */
    public UserSearchModalPanel( String id, ModalWindow window )
    {
        super( id );
        // TODO: add later:
        this.reviewMgr.setAdmin( SecUtils.getSession( this ) );
        this.window = window;
        loadPanel();
    }


    public void loadPanel()
    {
        LoadableDetachableModel requests = getListViewModel();
        PageableListView policyView = createListView( requests );
        add( policyView );
        add( new AjaxPagingNavigator( "usernavigator", policyView ) );
    }


    private PageableListView createListView( final LoadableDetachableModel requests )
    {
        return new PageableListView( "userdataview", requests, 10 )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem( final ListItem item )
            {
                final User modelObject = ( User ) item.getModelObject();
                item.add( new AjaxLink<Void>( GlobalIds.SELECT )
                {
                    private static final long serialVersionUID = 1L;


                    @Override
                    public void onClick( AjaxRequestTarget target )
                    {
                        userSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( GlobalIds.USER_ID, new PropertyModel( item.getModel(), GlobalIds.USER_ID ) ) );
                item.add( new Label( GlobalIds.DESCRIPTION, new PropertyModel( item.getModel(), GlobalIds.DESCRIPTION ) ) );
                item.add( new Label( GlobalIds.NAME, new PropertyModel( item.getModel(), GlobalIds.NAME ) ) );
                item.add( new Label( GlobalIds.OU, new PropertyModel( item.getModel(), GlobalIds.OU ) ) );
                item.add( new Label( GlobalIds.TITLE, new PropertyModel( item.getModel(), GlobalIds.TITLE ) ) );
                item.add( new JpegImage( GlobalIds.JPEGPHOTO )
                {
                    /** Default serialVersionUID */
                    private static final long serialVersionUID = 1L;


                    @Override
                    protected byte[] getPhoto()
                    {
                        byte[] photo;
                        photo = modelObject.getJpegPhoto();
                        return photo;
                    }
                } );
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
                List<User> users = null;
                try
                {
                    userSelection = null;
                    if ( userSearchVal == null )
                        userSearchVal = "";
                    users = reviewMgr.findUsers( new User( userSearchVal ) );
                    // sort list by userId:
                    if( CollectionUtils.isNotEmpty( users ))
                    {
                        Collections.sort( ( List<User> ) users, new Comparator<User>()
                        {
                            @Override
                            public int compare(User u1, User u2)
                            {
                                return u1.getUserId().compareToIgnoreCase( u2.getUserId() );
                            }
                        } );
                    }

                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return users;
            }
        };
    }


    public User getUserSelection()
    {
        return userSelection;
    }


    public void setSearchVal( String searchVal )
    {
        this.userSearchVal = searchVal;
    }
}