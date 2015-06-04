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
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.model.SDListModel;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxLink;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.model.SDSet;
import org.apache.directory.fortress.core.model.UserRole;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SDListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( SDListPanel.class.getName() );
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String searchVal;
    private char selectedRadioButton;
    private TextField searchValFld;
    private RadioGroup radioGroup;
    private static final char NAMES = 'N';
    private static final char ROLES = 'R';


    public SDListPanel( String id, final boolean isStatic )
    {
        super( id );
        SDSet sdSet = new SDSet();
        sdSet.setName( "" );
        String searchLabel;
        String opName;
        if ( isStatic )
        {
            sdSet.setType( SDSet.SDType.STATIC );
            searchLabel = "SSD Name";
            opName = "ssdRoleSets";
        }
        else
        {
            sdSet.setType( SDSet.SDType.DYNAMIC );
            searchLabel = "DSD Name";
            opName = "dsdRoleSets";
        }
        SDListModel sdListModel = new SDListModel( sdSet, SecUtils.getSession( this ) );
        setDefaultModel( sdListModel );
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<>();
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( searchLabel ), "userObject.name" ) );

        PropertyColumn description = new PropertyColumn<>(
            Model.of( "Description" ), "userObject.Description" );

        description.setInitialSize( 300 );
        columns.add( description );

        PropertyColumn cardinality = new PropertyColumn<>(
            Model.of( "Cardinality" ), "userObject.Cardinality" );
        cardinality.setInitialSize( 100 );
        columns.add( cardinality );

        PropertyColumn members = new PropertyColumn<>(
            Model.of( "Members" ), "userObject.members" );
        members.setInitialSize( 600 );
        columns.add( members );

        List<SDSet> sdSets = ( List<SDSet> ) getDefaultModel().getObject();
        treeModel = createTreeModel( sdSets );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "sdtreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    SDSet sdSet = ( SDSet ) node.getUserObject();
                    log.debug( "TreeGrid.addGrid.selectItem selected sdSet =" + sdSet.getName() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, sdSet );
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
        grid.getTreeState().expandAll();
        this.listForm = new Form( "form" );
        this.listForm.add( grid );
        grid.setOutputMarkupId( true );

        radioGroup = new RadioGroup( "searchOptions", new PropertyModel( this, "selectedRadioButton" ) );
        add( radioGroup );
        Radio nameRb = new Radio( "nameRb", new Model( Character.valueOf( NAMES ) ) );
        radioGroup.add( nameRb );
        Radio roleRb = new Radio( "roleRb", new Model( Character.valueOf( ROLES ) ) );
        radioGroup.add( roleRb );
        addRoleSearchModal( roleRb );
        radioGroup.setOutputMarkupId( true );
        radioGroup.setRenderBodyOnly( false );
        searchValFld = new TextField( GlobalIds.SEARCH_VAL, new PropertyModel<String>( this, GlobalIds.SEARCH_VAL ) );
        searchValFld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( searchValFld );
            }
        };
        searchValFld.add( ajaxUpdater );
        radioGroup.add( searchValFld );
        this.listForm.add( radioGroup );
        selectedRadioButton = NAMES;
        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.REVIEW_MGR, opName )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search onSubmit" );
                info( "Searching SDSets..." );
                if ( !StringUtils.isNotBlank( searchVal ) )
                {
                    searchVal = "";
                }
                final SDSet srchSd = new SDSet();
                if ( isStatic )
                {
                    srchSd.setType( SDSet.SDType.STATIC );
                }
                else
                {
                    srchSd.setType( SDSet.SDType.DYNAMIC );
                }
                switch ( selectedRadioButton )
                {
                    case NAMES:
                        log.debug( ".onSubmit NAMES RB selected" );
                        srchSd.setName( searchVal );
                        break;
                    case ROLES:
                        log.debug( ".onSubmit ROLES RB selected" );
                        srchSd.setMember( searchVal );
                        break;
                }

                setDefaultModel( new SDListModel( srchSd, SecUtils.getSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<SDSet> sdSets = ( List<SDSet> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( sdSets ) )
                {
                    for ( SDSet sdSet : sdSets )
                        rootNode.add( new DefaultMutableTreeNode( sdSet ) );
                    info( "Search returned " + sdSets.size() + " matching objects" );
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


    private void addRoleSearchModal( Radio roleRb )
    {
        final ModalWindow rolesModalWindow;
        listForm.add( rolesModalWindow = new ModalWindow( "rolesearchmodal" ) );
        final RoleSearchModalPanel roleSearchModalPanel = new RoleSearchModalPanel( rolesModalWindow.getContentId(),
            rolesModalWindow, false );
        rolesModalWindow.setContent( roleSearchModalPanel );
        rolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                UserRole roleConstraint = roleSearchModalPanel.getRoleSelection();
                if ( roleConstraint != null )
                {
                    log.debug( "modal selected:" + roleConstraint.getName() );
                    searchVal = roleConstraint.getName();
                    selectedRadioButton = ROLES;
                    target.add( radioGroup );
                }
            }
        } );

        roleRb.add( new SecureIndicatingAjaxLink( "roleAssignLinkLbl", GlobalIds.REVIEW_MGR, "findRoles" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on roles search";
                msg += "roleSelection: " + searchVal;
                roleSearchModalPanel.setRoleSearchVal( searchVal );
                log.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                rolesModalWindow.show( target );
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

        rolesModalWindow.setTitle( "Role Search Modal" );
        rolesModalWindow.setInitialWidth( 700 );
        rolesModalWindow.setInitialHeight( 450 );
        rolesModalWindow.setCookieName( "role-assign-modal" );
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


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            SDSet sdSet = ( SDSet ) node.getUserObject();
            log.debug( ".removeSelectedItems sdset node: " + sdSet.getName() );
            List<SDSet> sdSets = ( ( List<SDSet> ) getDefaultModel().getObject() );
            sdSets.remove( sdSet );
        }
    }


    private DefaultTreeModel createTreeModel( List<SDSet> sdSets )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( sdSets == null )
            log.debug( "no SDSets found" );
        else
        {
            log.debug( "SDSets found:" + sdSets.size() );
            for ( SDSet sdSet : sdSets )
                rootNode.add( new DefaultMutableTreeNode( sdSet ) );
        }
        return model;
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<SDSet> sdSets = ( ( List<SDSet> ) getDefaultModelObject() );
            sdSets.add( ( SDSet ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, sdSets.size() );
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }
}
