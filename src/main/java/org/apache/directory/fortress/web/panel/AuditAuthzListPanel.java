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


import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.core.util.time.TUtil;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.directory.fortress.web.model.AuditAuthzListModel;
import org.apache.directory.fortress.web.AuditAuthzPage;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxLink;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.AuthZ;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.model.UserAudit;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 8/6/13
 */
public class AuditAuthzListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( AuditAuthzListPanel.class.getName() );
    private Form listForm;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private TextField userFld;
    private TextField objFld;
    private TextField opFld;
    protected DatePicker beginDateDP;
    protected DatePicker endDateDP;
    private Permission permission;


    public AuditAuthzListPanel( String id, UserAudit userAudit )
    {
        super( id );
        init( userAudit );
    }


    private void init( UserAudit userAudit )
    {
        AuditAuthzListModel pageModel = new AuditAuthzListModel( userAudit, SecUtils.getSession( this ) );
        setDefaultModel( pageModel );
        createAndLoadGrid();
        this.listForm = new Form( "authzform" );
        this.listForm.addOrReplace( grid );
        this.listForm.setModel( new CompoundPropertyModel<>( userAudit ) );
        addEditFields();
        addButtons();
        add( this.listForm );
    }


    private void addEditFields()
    {
        userFld = new TextField( GlobalIds.USER_ID );
        userFld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( userFld );
            }
        };
        userFld.add( ajaxUpdater );
        this.listForm.add( userFld );
        addUserSearchModal();

        objFld = new TextField( GlobalIds.OBJ_NAME );
        objFld.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( objFld );
            }
        };
        objFld.add( ajaxUpdater );
        this.listForm.add( objFld );

        opFld = new TextField( GlobalIds.OP_NAME );
        opFld.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( opFld );
            }
        };
        opFld.add( ajaxUpdater );
        this.listForm.add( opFld );

        final CheckBox isAdminCB = new CheckBox( GlobalIds.ADMIN );
        isAdminCB.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( isAdminCB );
            }
        };
        isAdminCB.add( ajaxUpdater );
        isAdminCB.setRequired( false );
        this.listForm.add( isAdminCB );
        addPermSearchModal();

        final CheckBox failedOnlyCB = new CheckBox( GlobalIds.FAILED_ONLY );
        failedOnlyCB.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( failedOnlyCB );
            }
        };
        failedOnlyCB.add( ajaxUpdater );
        failedOnlyCB.setRequired( false );
        this.listForm.add( failedOnlyCB );

        // TODO: fortress auditMgr authz search doesn't work with this flag:
        failedOnlyCB.setEnabled( false );

        // Begin Date
        beginDateDP = new DatePicker( GlobalIds.BEGIN_DATE );
        beginDateDP.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( beginDateDP );
            }
        };
        beginDateDP.add( ajaxUpdater );
        beginDateDP.setRequired( false );
        this.listForm.add( beginDateDP );

        // End Date
        endDateDP = new DatePicker( GlobalIds.END_DATE );
        endDateDP.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( endDateDP );
            }
        };
        endDateDP.add( ajaxUpdater );
        endDateDP.setRequired( false );
        this.listForm.add( endDateDP );
    }


    private void addButtons()
    {
        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.AUDIT_MGR,
            GlobalIds.GET_USER_AUTHZS )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                LOG.debug( ".search onSubmit" );
                UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                if ( !StringUtils.isNotEmpty( userAudit.getUserId() ) )
                {
                    userAudit.setUserId( "" );
                }
                if ( permission != null )
                {
                    userAudit.setDn( permission.getDn() );
                }
                setResponsePage( new AuditAuthzPage( userAudit ) );
            }


            @Override
            public void onError( AjaxRequestTarget target, Form form )
            {
                LOG.warn( ".search.onError" );
                throw new RuntimeException( "error submitting form" );
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
        this.listForm.add( new AjaxSubmitLink( GlobalIds.CLEAR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                setResponsePage( new AuditAuthzPage( new UserAudit() ) );
            }


            @Override
            public void onError( AjaxRequestTarget target, Form form )
            {
                LOG.warn( "AuditAuthzListPanel.clear.onError" );
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


    private void createAndLoadGrid()
    {
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<>();
        PropertyColumn reqStart = new PropertyColumn<>(
            Model.of( "Timestamp" ), "userObject.reqStart" );
        reqStart.setInitialSize( 200 );
        columns.add( reqStart );

        PropertyColumn requAuthzId = new PropertyColumn<>(
            Model.of( "User ID" ), "userObject.reqAuthzID" );
        requAuthzId.setInitialSize( 200 );
        columns.add( requAuthzId );

        PropertyColumn reqAttr = new PropertyColumn<>(
            Model.of( "Object Name" ), "userObject.reqAttr" );
        reqAttr.setInitialSize( 300 );
        columns.add( reqAttr );

        PropertyColumn reqDerefAliases = new PropertyColumn<>(
            Model.of( "Object ID" ), "userObject.reqDerefAliases" );
        reqDerefAliases.setInitialSize( 100 );
        columns.add( reqDerefAliases );

        PropertyColumn reqAttrsOnly = new PropertyColumn<>(
            Model.of( "Operation" ), "userObject.reqAttrsOnly" );
        reqAttrsOnly.setInitialSize( 120 );
        columns.add( reqAttrsOnly );

        PropertyColumn reqResult = new PropertyColumn<>(
            Model.of( "Result" ), "userObject.reqResult" );
        reqResult.setInitialSize( 80 );
        columns.add( reqResult );

        List<AuthZ> authZs = ( List<AuthZ> ) getDefaultModel().getObject();
        DefaultTreeModel treeModel = createTreeModel( authZs );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "authztreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    AuthZ authZ = ( AuthZ ) node.getUserObject();
                    LOG.debug( "TreeGrid.addGrid.selectItem selected authZ =" + authZ.getReqAuthzID() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        LOG.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, authZ );
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
        grid.setOutputMarkupId( true );
    }


    private void addUserSearchModal()
    {
        final ModalWindow usersModalWindow;
        listForm.add( usersModalWindow = new ModalWindow( "usersearchmodal" ) );
        final UserSearchModalPanel userSearchModalPanel = new UserSearchModalPanel( usersModalWindow.getContentId(),
            usersModalWindow );
        usersModalWindow.setContent( userSearchModalPanel );
        usersModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                User userSelection = userSearchModalPanel.getUserSelection();
                if ( userSelection != null )
                {
                    LOG.debug( "modal selected:" + userSelection.getUserId() );
                    UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                    userAudit.setUserId( userSelection.getUserId() );
                    target.add( userFld );
                }
            }
        } );
        listForm.add( new SecureIndicatingAjaxLink( "userAssignLinkLbl", GlobalIds.REVIEW_MGR, GlobalIds.FIND_USERS )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                String msg = "clicked on users search";
                msg += "userSelection: " + userAudit.getUserId();
                userSearchModalPanel.setSearchVal( userAudit.getUserId() );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                usersModalWindow.show( target );
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
        usersModalWindow.setTitle( "User Search Modal" );
        usersModalWindow.setInitialWidth( 1000 );
        usersModalWindow.setInitialHeight( 700 );
        usersModalWindow.setCookieName( "user-search-modal" );
    }


    private void addPermSearchModal()
    {
        final ModalWindow permsModalWindow;
        UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
        listForm.add( permsModalWindow = new ModalWindow( "permsearchmodal" ) );
        final PermSearchModalPanel permSearchModalPanel = new PermSearchModalPanel( permsModalWindow.getContentId(),
            permsModalWindow, userAudit.isAdmin() );
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
                    UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                    LOG.debug( "modal selected:" + permSelection.getAbstractName() );
                    permission = permSelection;
                    userAudit.setObjName( permSelection.getObjName() );
                    userAudit.setOpName( permSelection.getOpName() );
                    target.add( objFld );
                    target.add( opFld );
                }
            }
        } );
        listForm.add( new SecureIndicatingAjaxLink( "permLinkLbl", GlobalIds.REVIEW_MGR, GlobalIds.FIND_PERMISSIONS )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                String msg = "clicked on perms search";
                msg += "permSelection: " + permission;
                permSearchModalPanel.setSearchVal( userAudit.getObjName() );
                permSearchModalPanel.setAdmin( userAudit.isAdmin() );
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
        String title;
        if ( userAudit.isAdmin() )
            title = "Admin Permission Search Modal";
        else
            title = "Permission Search Modal";

        permsModalWindow.setTitle( title );
        permsModalWindow.setInitialWidth( 650 );
        permsModalWindow.setInitialHeight( 450 );
        permsModalWindow.setCookieName( "perm-search-modal" );
    }


    private DefaultTreeModel createTreeModel( List<AuthZ> authZs )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( authZs == null )
            LOG.debug( "no Authorizations found" );
        else
        {
            LOG.debug( "AuthZ found:" + authZs.size() );
            info( "Loading " + authZs.size() + " objects into list panel" );
            loadTree( authZs );
        }
        return model;
    }


    private void loadTree( List<AuthZ> authZs )
    {
        for ( AuthZ authZ : authZs )
        {
            Date start = null;
            try
            {
                start = TUtil.decodeGeneralizedTime( authZ.getReqStart() );
            }
            catch ( ParseException pe )
            {
                LOG.warn( "ParseException=" + pe.getMessage() );
            }
            if ( start != null )
            {
                SimpleDateFormat formatter = new SimpleDateFormat( GlobalIds.AUDIT_TIMESTAMP_FORMAT );
                String formattedDate = formatter.format( start );
                authZ.setReqStart( formattedDate );
            }
            authZ.setReqResult( GlobalIds.FAILURE );
            /*
                        TODO: On RC40 - Replace above line with the following:
                        if(StringUtils.isNotEmpty( authZ.getReqAssertion() ) && (authZ.getReqAssertion().equals( org.apache.directory.fortress.core.GlobalIds.AUTH_Z_FAILED_VALUE ) ) )
                        {
                            authZ.setReqResult( GlobalIds.FAILURE );
                        }
                        else
                        {
                            authZ.setReqResult( GlobalIds.SUCCESS );
                        }
            */
            authZ.setReqAuthzID( AuditUtils.getAuthZId( authZ.getReqAuthzID() ) );
            AuditUtils.mapAuthZPerm( authZ );
            rootNode.add( new DefaultMutableTreeNode( authZ ) );
        }
    }
}
