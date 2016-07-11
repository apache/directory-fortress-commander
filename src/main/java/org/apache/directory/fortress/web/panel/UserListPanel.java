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
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxLink;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.web.model.UserListModel;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.web.UserPage;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.model.UserRole;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
//public class UserListPanel extends FormComponentPanel
public class UserListPanel<T extends Serializable> extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( UserListPanel.class.getName() );
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String selectedRadioButton;
    private TextField f1Fld;
    private TextField f2Fld;
    private Label f1Lbl;
    private Label f2Lbl;
    private Label searchFieldsLbl;
    private WebMarkupContainer userformsearchfields;
    private WebMarkupContainer searchFields;
    private RadioGroup radioGroup;
    private static final String USERS = "U";
    private static final String ROLES = "R";
    private static final String ADMIN_ROLES = "A";
    private static final String OUS = "O";
    private static final String PERMS = "P";
    private Permission permission;
    private SearchFields searchData = new SearchFields();
    private static final String USER_LABEL = "User ID";
    private String field1Label = USER_LABEL;
    private String field2Label;
    private String searchFieldsLabel = USER_SEARCH_LABEL;
    private static final String USER_SEARCH_LABEL = "Search By User";


    public UserListPanel( String id )
    {
        super( id );
        UserListModel userListModel = new UserListModel( new User(), SecUtils.getSession( this ) );
        setDefaultModel( userListModel );
        addGrid();
        userformsearchfields = new WebMarkupContainer( "userformsearchfields" );
        userformsearchfields.setOutputMarkupId( true );
        listForm.add( userformsearchfields );
        addRadioButtons();
        addSearchFields();
        addButtons();
    }


    private void addRadioButtons()
    {
        radioGroup = new RadioGroup( "searchOptions", new PropertyModel( this, "selectedRadioButton" ) );
        AjaxFormComponentUpdatingBehavior ajaxRadioUpdater = new AjaxFormChoiceComponentUpdatingBehavior()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                processRadioButton( target );
            }
        };
        radioGroup.add( ajaxRadioUpdater );
        add( radioGroup );
        Radio userRb = new Radio( "userRb", new Model( USERS ) );
        radioGroup.add( userRb );
        Radio roleRb = new Radio( "roleRb", new Model( ROLES ) );
        radioGroup.add( roleRb );
        Radio adminRoleRb = new Radio( "adminRoleRb", new Model( ADMIN_ROLES ) );
        radioGroup.add( adminRoleRb );
        Radio ouRb = new Radio( "ouRb", new Model( OUS ) );
        radioGroup.add( ouRb );
        Radio permRb = new Radio( "permRb", new Model( PERMS ) );
        radioGroup.add( permRb );
        radioGroup.setOutputMarkupId( true );
        radioGroup.setRenderBodyOnly( false );

        userformsearchfields.add( radioGroup );
        selectedRadioButton = USERS;

        addRoleSearchModal( roleRb );
        addAdminRoleSearchModal( adminRoleRb );
        addOUSearchModal( ouRb );
        addPermSearchModal( permRb );
    }


    private void addSearchFields()
    {
        searchFields = new WebMarkupContainer( "searchfields" );
        searchFieldsLbl = new Label( "searchFieldslabel", new PropertyModel<String>( this, "searchFieldsLabel" ) );
        searchFields.add( searchFieldsLbl );
        f1Lbl = new Label( "field1label", new PropertyModel<String>( this, "field1Label" ) );
        searchFields.add( f1Lbl );
        f2Lbl = new Label( "field2label", new PropertyModel<String>( this, "field2Label" ) );
        searchFields.add( f2Lbl );

        f1Fld = new TextField( GlobalIds.FIELD_1, new PropertyModel<String>( this, "searchData.field1" ) );
        f1Fld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( f1Fld );
            }
        };
        f1Fld.add( ajaxUpdater );
        searchFields.add( f1Fld );

        f2Fld = new TextField( GlobalIds.FIELD_2, new PropertyModel<String>( this, "searchData.field2" ) );
        f2Fld.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( f2Fld );
            }
        };
        f2Fld.add( ajaxUpdater );
        f2Fld.setVisible( false );
        searchFields.add( f2Fld );
        searchFields.setOutputMarkupId( true );
        userformsearchfields.add( searchFields );
    }


    private void addButtons()
    {
        userformsearchfields.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.REVIEW_MGR,
            GlobalIds.FIND_USERS )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                LOG.debug( ".search.onSubmit selected radio button: " + selectedRadioButton );
                info( "Searching Users..." );
                String searchVal = "";
                if ( StringUtils.isNotEmpty( searchData.getField1() ) )
                {
                    searchVal = searchData.getField1();
                }
                if ( selectedRadioButton.equals( PERMS ) )
                {
                    LOG.debug( ".onSubmit PERMS RB selected" );

                    Permission srchPerm = new Permission();
                    srchPerm.setObjName( searchData.getField1() );
                    srchPerm.setOpName( searchData.getField2() );
                    setDefaultModel( new UserListModel( srchPerm, SecUtils.getSession( this ) ) );
                }
                else
                {
                    User srchUser = new User();
                    if ( selectedRadioButton.equals( USERS ) )
                    {
                        LOG.debug( ".onSubmit USERS_PAGE RB selected" );
                        srchUser.setUserId( searchVal );
                    }
                    else if ( selectedRadioButton.equals( ROLES ) )
                    {
                        LOG.debug( ".onSubmit ROLES RB selected" );
                        srchUser.setRoleName( searchVal );
                    }
                    else if ( selectedRadioButton.equals( ADMIN_ROLES ) )
                    {
                        LOG.debug( ".onSubmit ADMIN ROLES RB selected" );
                        srchUser.setAdminRoleName( searchVal );
                    }
                    else if ( selectedRadioButton.equals( OUS ) )
                    {
                        LOG.debug( ".onSubmit OUS RB selected" );
                        srchUser.setOu( searchVal );
                    }
                    else if ( selectedRadioButton.equals( PERMS ) )
                    {
                        LOG.debug( ".onSubmit PERMS RB selected" );
                    }
                    setDefaultModel( new UserListModel( srchUser, SecUtils.getSession( this ) ) );
                }

                treeModel.reload();
                rootNode.removeAllChildren();
                List<User> users = ( List<User> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( users ) )
                {
                    for ( User user : users )
                    {
                        rootNode.add( new DefaultMutableTreeNode( user ) );
                    }
                    info( "Search returned " + users.size() + " matching objects" );
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
                LOG.warn( ".search.onError" );
                target.add();
            }
        } );
        userformsearchfields.add( new AjaxSubmitLink( GlobalIds.CLEAR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                setResponsePage( new UserPage() );
            }


            @Override
            public void onError( AjaxRequestTarget target, Form form )
            {
                LOG.warn( "UserListPanel.clear.onError" );
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
    }


    private void addRoleSearchModal( Radio roleRb )
    {
        final ModalWindow rolesModalWindow;
        userformsearchfields.add( rolesModalWindow = new ModalWindow( "rolesearchmodal" ) );
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
                    LOG.debug( "modal selected:" + roleConstraint.getName() );
                    searchData.setField1( roleConstraint.getName() );
                    selectedRadioButton = ROLES;
                    enableRoleSearch();
                    target.add( searchFields );
                    target.add( radioGroup );
                }
            }
        } );

        roleRb.add( new SecureIndicatingAjaxLink( "roleAssignLinkLbl", GlobalIds.REVIEW_MGR, GlobalIds.FIND_ROLES )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;

            public void onClick( AjaxRequestTarget target )
            {
                roleSearchModalPanel.setRoleSearchVal( searchData.getField1() );
                roleSearchModalPanel.setAdmin( false );
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
        rolesModalWindow.setTitle( "RBAC Role Search Modal" );
        rolesModalWindow.setInitialWidth( 700 );
        rolesModalWindow.setInitialHeight( 450 );
        rolesModalWindow.setCookieName( "role-assign-modal" );
    }


    private void addAdminRoleSearchModal( Radio adminRoleRb )
    {
        final ModalWindow adminRolesModalWindow;
        userformsearchfields.add( adminRolesModalWindow = new ModalWindow( "adminrolesearchmodal" ) );
        final RoleSearchModalPanel adminRoleSearchModalPanel = new RoleSearchModalPanel( adminRolesModalWindow
            .getContentId(), adminRolesModalWindow, true );
        adminRolesModalWindow.setContent( adminRoleSearchModalPanel );
        adminRolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                UserRole roleConstraint = adminRoleSearchModalPanel.getRoleSelection();
                if ( roleConstraint != null )
                {
                    searchData.setField1( roleConstraint.getName() );
                    selectedRadioButton = ADMIN_ROLES;
                    enableAdminSearch();
                    target.add( searchFields );
                    target.add( radioGroup );
                }
            }
        } );

        adminRoleRb.add( new SecureIndicatingAjaxLink( "adminRoleAssignLinkLbl", GlobalIds.DEL_REVIEW_MGR,
            GlobalIds.FIND_ROLES )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on admin roles search";
                msg += "adminRoleSelection: " + searchData.getField1();
                adminRoleSearchModalPanel.setRoleSearchVal( searchData.getField1() );
                adminRoleSearchModalPanel.setAdmin( true );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                adminRolesModalWindow.show( target );
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
        adminRolesModalWindow.setTitle( "Admin Role Search Modal" );
        adminRolesModalWindow.setInitialWidth( 700 );
        adminRolesModalWindow.setInitialHeight( 450 );
        adminRolesModalWindow.setCookieName( "role-assign-modal" );
    }


    private void addOUSearchModal( Radio ouRb )
    {
        final ModalWindow ousModalWindow;
        userformsearchfields.add( ousModalWindow = new ModalWindow( "ousearchmodal" ) );
        final OUSearchModalPanel ouSearchModalPanel = new OUSearchModalPanel( ousModalWindow.getContentId(),
            ousModalWindow, true );
        ousModalWindow.setContent( ouSearchModalPanel );
        ousModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                OrgUnit ou = ouSearchModalPanel.getSelection();
                if ( ou != null )
                {
                    searchData.setField1( ou.getName() );
                }
                selectedRadioButton = OUS;
                enableOuSearch();
                target.add( searchFields );
                target.add( radioGroup );
            }
        } );
        ouRb.add( new SecureIndicatingAjaxLink( "ouAssignLinkLbl", GlobalIds.DEL_REVIEW_MGR, "searchOU" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;

            public void onClick( AjaxRequestTarget target )
            {
                ouSearchModalPanel.setSearchVal( searchData.getField1() );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                ousModalWindow.show( target );
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

        ousModalWindow.setTitle( "User Organizational Unit Search Modal" );
        ousModalWindow.setInitialWidth( 450 );
        ousModalWindow.setInitialHeight( 450 );
        ousModalWindow.setCookieName( "userou-modal" );
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
                    LOG.error( "onEvent caught invalid operation" );
                    break;
            }
            AjaxRequestTarget target = ( ( SaveModelEvent ) event.getPayload() ).getAjaxRequestTarget();
            target.add(grid);
            LOG.debug( ".onEvent AJAX - UserListPanel - SaveModelEvent: " + target.toString() );
        }
    }


    private void addPermSearchModal( Radio permRb )
    {
        final ModalWindow permsModalWindow;
        userformsearchfields.add( permsModalWindow = new ModalWindow( "permsearchmodal" ) );
        final PermSearchModalPanel permSearchModalPanel = new PermSearchModalPanel( permsModalWindow.getContentId(),
            permsModalWindow, false );
        permsModalWindow.setContent( permSearchModalPanel );
        permsModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                Permission permSelection = permSearchModalPanel.getSelection();
                if ( permSelection != null )
                {
                    searchData.setField1( permSelection.getObjName() );
                    searchData.setField2( permSelection.getOpName() );
                }
                selectedRadioButton = PERMS;
                enablePermSearch();
                target.add( searchFields );
                target.add( radioGroup );
            }
        } );
        permRb.add( new SecureIndicatingAjaxLink( "permLinkLbl", GlobalIds.REVIEW_MGR, GlobalIds.FIND_PERMISSIONS )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on perms search";
                msg += "permSelection: " + permission;
                String objectSearchVal = "";
                if ( StringUtils.isNotEmpty( searchData.getField1() ) )
                {
                    objectSearchVal = searchData.getField1();
                }
                permSearchModalPanel.setSearchVal( objectSearchVal );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                permsModalWindow.show( target );
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
        permsModalWindow.setTitle( "Permission Search Modal" );
        permsModalWindow.setInitialWidth( 650 );
        permsModalWindow.setInitialHeight( 450 );
        permsModalWindow.setCookieName( "perm-search-modal" );
    }


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            User user = ( User ) node.getUserObject();
            LOG.debug( ".removeSelectedItems user node: " + user.getUserId() );
            List<User> users = ( ( List<User> ) getDefaultModel().getObject() );
            users.remove( user );
        }
    }


    private DefaultTreeModel createTreeModel( List<T> users )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( users == null )
        {
            LOG.debug( "no Users found" );
        }
        else
        {
            LOG.debug( ".createTreeModel Users found:" + users.size() );
            for ( T user : users )
            {
                    rootNode.add( new DefaultMutableTreeNode( user ) );
            }
        }
        return model;
    }


    private void addGrid()
    {
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns = new ArrayList<>();
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "UserId" ), "userObject.UserId" ) );
        /*
                columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
                    String>( Model.of( "Name" ), "userObject.Name" ) );
        */
        PropertyColumn ou = new PropertyColumn<>( Model.of( "User Organization" ), "userObject.Ou" );
        ou.setInitialSize( 150 );
        columns.add( ou );
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "Description" ), "userObject.Description" ) );
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "Address" ), "userObject.Address.Addresses" ) );
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "City" ), "userObject.Address.City" ) );
        PropertyColumn state = new PropertyColumn<>( Model.of( "State" ), "userObject.Address.State" );
        state.setInitialSize( 50 );
        columns.add( state );
        /*
                PropertyColumn locked = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                            Model.of("Lock"), "userObject.locked");
                locked.setInitialSize(40);
                columns.add(locked);
                PropertyColumn reset = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                            Model.of("Reset"), "userObject.reset");
                reset.setInitialSize(40);
                columns.add(reset);
        */
        PropertyColumn roles = new PropertyColumn<>( Model.of( "RBAC Role Assignments" ), "userObject.Roles" );
        roles.setInitialSize( 400 );
        columns.add( roles );
        PropertyColumn adminRoles = new PropertyColumn<>( Model.of( "Admin Role Assignments" ), "userObject.AdminRoles" );
        adminRoles.setInitialSize( 400 );
        columns.add( adminRoles );

        //List<User> users = ( List<User> ) getDefaultModel().getObject();
        List<T> users = ( List<T> ) getDefaultModel().getObject();

        treeModel = createTreeModel( users );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "usertreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    T user = ( T ) node.getUserObject();
                    //LOG.debug( "TreeGrid.addGrid.selectItem selected user =" + user.getUserId() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        LOG.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, ( FortEntity ) user );
                    }
                }
            }
        };
        //grid.setContentHeight( 60, SizeUnit.EM );
        grid.setAllowSelectMultiple( false );
        grid.setClickRowToSelect( true );
        grid.setClickRowToDeselect( false );
        grid.setSelectToEdit( false );
        // expand the root node
        grid.getTreeState().expandAll();
        this.listForm = new Form( "userlistform" );
        this.listForm.add( grid );
        add( this.listForm );
        grid.setOutputMarkupId( true );
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<User> users = ( ( List<User> ) getDefaultModelObject() );
            users.add( ( User ) entity );
            LOG.debug( "UserListPanel.add tree depth: " + rootNode.getChildCount() );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, rootNode.getChildCount() );
            //treeModel.insertNodeInto(new DefaultMutableTreeNode(entity), rootNode, users.size());
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }


    private void enableOuSearch()
    {
        f2Fld.setVisible( false );
        f2Lbl.setVisible( false );
        String OU_LABEL = "Organization";
        field1Label = OU_LABEL;
        String OU_SEARCH_LABEL = "Search By User Organization";
        searchFieldsLabel = OU_SEARCH_LABEL;
    }


    private void enableAdminSearch()
    {
        f2Fld.setVisible( false );
        f2Lbl.setVisible( false );
        String ADMIN_LABEL = "Admin Role Name";
        field1Label = ADMIN_LABEL;
        String ADMIN_SEARCH_LABEL = "Search By Admininstrative Role";
        searchFieldsLabel = ADMIN_SEARCH_LABEL;
    }


    private void enableRoleSearch()
    {
        f2Fld.setVisible( false );
        f2Lbl.setVisible( false );
        String ROLE_LABEL = "Role Name";
        field1Label = ROLE_LABEL;
        String ROLE_SEARCH_LABEL = "Search By Role";
        searchFieldsLabel = ROLE_SEARCH_LABEL;
    }


    private void enableUserSearch()
    {
        f2Fld.setVisible( false );
        f2Lbl.setVisible( false );
        field1Label = USER_LABEL;
        searchFieldsLabel = USER_SEARCH_LABEL;
    }


    private void enablePermSearch()
    {
        f2Fld.setVisible( true );
        f2Lbl.setVisible( true );
        String PERM_OBJ_LABEL = "Object Name";
        field1Label = PERM_OBJ_LABEL;
        String PERM_OP_LABEL = "Operation Name";
        field2Label = PERM_OP_LABEL;
        String PERM_SEARCH_LABEL = "Search By Permission";
        searchFieldsLabel = PERM_SEARCH_LABEL;
    }


    private void processRadioButton( AjaxRequestTarget target )
    {
        LOG.debug( "RADIO Button: " + selectedRadioButton );
        if ( selectedRadioButton.equals( USERS ) )
        {
            enableUserSearch();
        }
        else if ( selectedRadioButton.equals( ROLES ) )
        {
            enableRoleSearch();
        }
        else if ( selectedRadioButton.equals( ADMIN_ROLES ) )
        {
            enableAdminSearch();
        }
        else if ( selectedRadioButton.equals( OUS ) )
        {
            enableOuSearch();
        }
        else if ( selectedRadioButton.equals( PERMS ) )
        {
            enablePermSearch();
        }
        searchData = new SearchFields();
        target.add( searchFields );
    }

    class SearchFields implements Serializable
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private String field1;
        private String field2;
        private String field3;


        String getField1()
        {
            return field1;
        }


        void setField1( String field1 )
        {
            this.field1 = field1;
        }


        String getField2()
        {
            return field2;
        }


        void setField2( String field2 )
        {
            this.field2 = field2;
        }


        String getField3()
        {
            return field3;
        }


        void setField3( String field3 )
        {
            this.field3 = field3;
        }
    }
}