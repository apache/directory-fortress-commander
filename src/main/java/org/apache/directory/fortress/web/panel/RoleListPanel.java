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


import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.GlobalIds;
import org.apache.directory.fortress.web.GlobalUtils;
import org.apache.directory.fortress.web.RoleListModel;
import org.apache.directory.fortress.web.SaveModelEvent;
import org.apache.directory.fortress.web.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.SelectModelEvent;
import org.apache.directory.fortress.core.rbac.AdminRole;
import org.apache.directory.fortress.core.rbac.FortEntity;
import org.apache.directory.fortress.core.rbac.Role;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.util.attr.VUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoleListPanel<T extends Serializable> extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( RoleListPanel.class.getName() );
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String searchVal;
    private boolean isAdmin;


    public RoleListPanel( String id, final boolean isAdmin )
    {
        super( id );
        this.isAdmin = isAdmin;
        RoleListModel roleListModel = new RoleListModel( createRole( "" ), isAdmin, GlobalUtils.getRbacSession( this ) );
        setDefaultModel( roleListModel );
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>>();
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Name" ), "userObject.name" ) );

        PropertyColumn description = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Description" ), "userObject.Description" );
        description.setInitialSize( 300 );
        columns.add( description );

        PropertyColumn beginDate = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Begin Date" ), "userObject.BeginDate" );
        beginDate.setInitialSize( 80 );
        columns.add( beginDate );

        PropertyColumn endDate = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "End Date" ), "userObject.EndDate" );
        endDate.setInitialSize( 80 );
        columns.add( endDate );

        PropertyColumn beginLockDate = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Begin Lock Dt" ), "userObject.BeginLockDate" );
        beginLockDate.setInitialSize( 80 );
        columns.add( beginLockDate );

        PropertyColumn endLockDate = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "End Lock Dt" ), "userObject.EndLockDate" );
        endLockDate.setInitialSize( 80 );
        columns.add( endLockDate );

        PropertyColumn beginTime = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Begin Tm" ), "userObject.BeginTime" );
        beginTime.setInitialSize( 70 );
        columns.add( beginTime );

        PropertyColumn endTime = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "End Tm" ), "userObject.EndTime" );
        endTime.setInitialSize( 70 );
        columns.add( endTime );

        PropertyColumn dayMask = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "DayMask" ), "userObject.DayMask" );
        dayMask.setInitialSize( 80 );
        columns.add( dayMask );

        PropertyColumn parents = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Parents" ), "userObject.parents" );
        parents.setInitialSize( 250 );
        columns.add( parents );

        List<T> roles = ( List<T> ) getDefaultModel().getObject();
        treeModel = createTreeModel( roles );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "roletreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    T role = ( T ) node.getUserObject();
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, ( FortEntity ) role );
                    }
                }
            }
        };
        //grid.setContentHeight( 50, SizeUnit.EM );
        grid.setAllowSelectMultiple( false );
        grid.setClickRowToSelect( true );
        grid.setClickRowToDeselect( false );
        grid.setSelectToEdit( false );
        // expand the root node
        grid.getTreeState().expandAll();;
        this.listForm = new Form( "form" );
        this.listForm.add( grid );
        grid.setOutputMarkupId( true );
        TextField searchValFld = new TextField( GlobalIds.SEARCH_VAL, new PropertyModel<String>( this,
            GlobalIds.SEARCH_VAL ) );
        this.listForm.add( searchValFld );

        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.REVIEW_MGR, "findRoles" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search onSubmit" );
                info( "Searching Roles..." );
                if ( !VUtil.isNotNullOrEmpty( searchVal ) )
                {
                    searchVal = "";
                }

                Role srchRole = createRole( searchVal );
                setDefaultModel( new RoleListModel( srchRole, isAdmin, GlobalUtils.getRbacSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<Role> roles = ( List<Role> ) getDefaultModelObject();
                if ( VUtil.isNotNullOrEmpty( roles ) )
                {
                    for ( T role : ( List<T> ) roles )
                        rootNode.add( new DefaultMutableTreeNode( role ) );
                    info( "Search returned " + roles.size() + " matching objects" );
                }
                else
                {
                    info( "No matching objects found" );
                }
                target.add( grid );
            }


            @Override
            public void onError( AjaxRequestTarget target, Form form )
            {
                log.warn( ".search.onError" );
                target.add();
            }


            @Override
            protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
            {
                super.updateAjaxAttributes( attributes );
                AjaxCallListener ajaxCallListener = new AjaxCallListener()
                {
                    /** Default serialVersionUID */
                    private static final long serialVersionUID = 1L;


                    @Override
                    public CharSequence getFailureHandler( Component component )
                    {
                        return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                    }
                };
                attributes.getAjaxCallListeners().add( ajaxCallListener );
            }
        } );
        add( this.listForm );
    }


    @Override
    public void onEvent( IEvent event )
    {
        if ( event.getPayload() instanceof SaveModelEvent )
        {
            SaveModelEvent modelEvent = ( SaveModelEvent ) event.getPayload();
            switch ( modelEvent.getOperation() )
            {
                case ADD:
                    add( modelEvent.getEntity() );
                    break;
                case UPDATE:
                    modelChanged();
                    break;
                case DELETE:
                    prune();
                    break;
                default:
                    break;
            }
            AjaxRequestTarget target = ( ( SaveModelEvent ) event.getPayload() ).getAjaxRequestTarget();
            target.add( grid );
            log.debug( ".onEvent SaveModelEvent: " + target.toString() );
        }
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<Role> roles = ( ( List<Role> ) getDefaultModelObject() );
            roles.add( ( Role ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, roles.size() );
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            Role role = ( Role ) node.getUserObject();
            log.debug( ".removeSelectedItems role node: " + role.getName() );
            List<Role> roles = ( ( List<Role> ) getDefaultModel().getObject() );
            roles.remove( role.getName() );
        }
    }


    private DefaultTreeModel createTreeModel( List<T> roles )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( roles == null )
            log.debug( "no Roles found" );
        else
        {
            log.debug( "Roles found:" + roles.size() );
            for ( T role : roles )
                rootNode.add( new DefaultMutableTreeNode( role ) );
        }
        return model;
    }


    private Role createRole( String name )
    {
        Role role;

        if ( isAdmin )
        {
            role = new AdminRole( name );
        }
        else
        {
            role = new Role( name );
        }

        return role;
    }
}