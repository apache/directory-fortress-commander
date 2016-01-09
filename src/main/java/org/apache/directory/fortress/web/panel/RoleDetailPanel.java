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


import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.combobox.ComboBox;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.model.AdminRole;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.directory.fortress.core.model.Role;
import org.apache.directory.fortress.core.model.UserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoleDetailPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private AdminMgr adminMgr;
    @SpringBean
    private DelAdminMgr delAdminMgr;
    private static final Logger log = Logger.getLogger( RoleDetailPanel.class.getName() );
    private static final String PARENTS_SELECTION = "parentsSelection";
    private Form editForm;
    private Displayable display;
    private boolean isAdmin;
    private String objName;


    public Form getForm()
    {
        return this.editForm;
    }


    public RoleDetailPanel( String id, Displayable display, final boolean isAdmin )
    {
        super( id );
        this.isAdmin = isAdmin;
        this.adminMgr.setAdmin( SecUtils.getSession( this ) );
        this.delAdminMgr.setAdmin( SecUtils.getSession( this ) );
        if ( isAdmin )
        {
            this.objName = GlobalIds.DEL_ADMIN_MGR;
            this.editForm = new RoleDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<>(
                new AdminRole() ) );
        }
        else
        {
            this.objName = GlobalIds.ADMIN_MGR;
            this.editForm = new RoleDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<>( new Role() ) );
        }

        this.display = display;
        add( editForm );
    }

    public class RoleDetailForm<T extends Serializable> extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private static final String TEMPORAL_CONSTRAINTS_LABEL = "temporalConstraintsLabel";
        private String temporalConstraintsLabel = "Temporal Constraints";
        private ConstraintPanel constraintPanel;
        private String internalId;
        private ComboBox<String> parentsCB;
        private Component component;
        private String parentsSelection;
        private List<String> parents = new ArrayList<>();
        private UserRole parentConstraint = new UserRole();
        private RoleAdminDetailPanel auxPanel;
        private TextField nameTF;
        private SecureIndicatingAjaxButton addPB;

        public RoleDetailForm( String id, final IModel<T> model )
        {
            super( id, model );
            if ( isAdmin )
            {
                auxPanel = new RoleAdminDetailPanel( GlobalIds.ROLEAUXPANEL, model );
                add( auxPanel );
            }
            else
            {
                add( new WebMarkupContainer( GlobalIds.ROLEAUXPANEL ) );
            }

            this.add( new JQueryBehavior( "#accordion", "accordion" ) );
            constraintPanel = new ConstraintPanel( "constraintpanel", model );
            add( constraintPanel );
            add( new Label( TEMPORAL_CONSTRAINTS_LABEL, new PropertyModel<String>( this,
                TEMPORAL_CONSTRAINTS_LABEL ) ) );
            nameTF = new TextField( "name" );
            add( nameTF );
            TextField description = new TextField( GlobalIds.DESCRIPTION );
            description.setRequired( false );
            add( description );
            Label iid = new Label( "id" );
            add( iid );
            parentsCB = new ComboBox<>( GlobalIds.PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),
                parents );
            add( parentsCB );

            add( addPB = new SecureIndicatingAjaxButton( GlobalIds.ADD, objName, GlobalIds.ADD_ROLE )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Add" );
                    T role = ( T ) form.getModel().getObject();
                    updateEntityWithComboData( ( Role ) role );
                    try
                    {
                        String szRoleName;
                        if ( isAdmin )
                        {
                            delAdminMgr.addRole( ( AdminRole ) role );
                            szRoleName = ( ( AdminRole ) role ).getName();
                        }
                        else
                        {
                            adminMgr.addRole( ( Role ) role );
                            szRoleName = ( ( Role ) role ).getName();
                        }

                        parentsSelection = "";
                        parents.add( parentsSelection );
                        SaveModelEvent.send( getPage(), this, ( FortEntity ) role, target,
                            SaveModelEvent.Operations.ADD );
                        component = editForm;
                        String msg = "Role: " + szRoleName + " has been added";
                        display.setMessage( msg );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.info( "RoleDetailPanel.add.onError caught" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, objName, GlobalIds.UPDATE_ROLE )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    T role = ( T ) form.getModel().getObject();
                    updateEntityWithComboData( ( Role ) role );
                    try
                    {
                        String szRoleName;
                        if ( isAdmin )
                        {
                            delAdminMgr.updateRole( ( AdminRole ) role );
                            szRoleName = ( ( AdminRole ) role ).getName();
                        }
                        else
                        {
                            adminMgr.updateRole( ( Role ) role );
                            szRoleName = ( ( Role ) role ).getName();
                        }
                        String msg = "Role: " + szRoleName + " has been updated";
                        SaveModelEvent.send( getPage(), this, ( FortEntity ) role, target,
                            SaveModelEvent.Operations.UPDATE );
                        parentsSelection = "";
                        component = editForm;
                        display.setMessage( msg );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "RoleDetailPanel.update.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, objName, GlobalIds.DELETE_ROLE )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Delete" );
                    T role = ( T ) form.getModel().getObject();
                    try
                    {
                        String szRoleName;
                        if ( isAdmin )
                        {
                            delAdminMgr.deleteRole( ( AdminRole ) role );
                            szRoleName = ( ( AdminRole ) role ).getName();
                        }
                        else
                        {
                            adminMgr.deleteRole( ( Role ) role );
                            szRoleName = ( ( Role ) role ).getName();
                        }
                        clearDetailFields();
                        String msg = "Role: " + szRoleName + " has been deleted";
                        SaveModelEvent.send( getPage(), this, ( FortEntity ) role, target,
                            SaveModelEvent.Operations.DELETE );
                        component = editForm;
                        display.setMessage( msg );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "RoleDetailPanel.delete.onError" );
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
            add( new AjaxSubmitLink( GlobalIds.CANCEL )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    clearDetailFields();
                    component = editForm;
                    String msg = "Role cancelled input form";
                    display.setMessage( msg );
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "RoleDetailPanel.cancel.onError" );
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

            if ( isAdmin )
            {
                add( new Label( "roleDetailLabel", "Admin Role Detail" ) );
            }
            else
            {
                add( new Label( "roleDetailLabel", "RBAC Role Detail" ) );
            }
            addRoleSearchModal();
            add( new AjaxButton( "roles.delete" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles.delete";
                    if ( StringUtils.isNotEmpty( parentsSelection ) )
                    {
                        msg += " selection:" + parentsSelection;
                        Role role = ( Role ) form.getModel().getObject();
                        if ( role.getParents() != null )
                        {
                            role.getParents().remove( parentsSelection );
                            parents.remove( parentsSelection );
                            parentsSelection = "";
                            component = editForm;
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because role does not have parent set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because parent selection is empty";
                    }
                    display.setMessage( msg );
                    log.debug( msg );
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
            setOutputMarkupId( true );
        }


        private void updateEntityWithComboData( Role role )
        {
            if ( StringUtils.isNotEmpty( parentsSelection ) )
            {
                role.setParent( parentsSelection );
                parents.add( parentsSelection );
            }
        }

        private void addRoleSearchModal()
        {
            final ModalWindow rolesModalWindow;
            add( rolesModalWindow = new ModalWindow( "parentrolesmodal" ) );
            final RoleSearchModalPanel roleSearchModalPanel = new RoleSearchModalPanel(
                rolesModalWindow.getContentId(), rolesModalWindow, isAdmin );
            rolesModalWindow.setContent( roleSearchModalPanel );
            rolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    parentConstraint = roleSearchModalPanel.getRoleSelection();
                    if ( parentConstraint != null )
                    {
                        parentsSelection = parentConstraint.getName();
                        Role role = ( Role ) getDefaultModelObject();
                        role.setParent( parentsSelection );
                        target.add( parentsCB );
                        component = editForm;
                    }
                }
            } );

            add( new AjaxButton( GlobalIds.PARENTROLES_SEARCH )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on parent roles search";
                    msg += parentsSelection != null ? ": " + parentsSelection : "";
                    roleSearchModalPanel.setRoleSearchVal( parentsSelection );
                    display.setMessage( msg );
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

            String modalLabel;
            if ( isAdmin )
            {
                modalLabel = "Admin Role Selection Modal";
            }
            else
            {
                modalLabel = "RBAC Role Selection Modal";
            }
            rolesModalWindow.setTitle( modalLabel );
            rolesModalWindow.setInitialWidth( 700 );
            rolesModalWindow.setInitialHeight( 450 );
            rolesModalWindow.setCookieName( "role-assign-modal" );
        }

        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                T role = ( T ) modelEvent.getEntity();
                this.setModelObject( role );
                parentsSelection = "";
                if ( CollectionUtils.isNotEmpty( ( ( Role ) role ).getParents() ) )
                {
                    parents = new ArrayList<>( ( ( Role ) role ).getParents() );
                    parentsCB = new ComboBox<>( GlobalIds.PARENTS, new PropertyModel<String>( this,
                        PARENTS_SELECTION ), parents );
                }
                else
                {
                    parents = new ArrayList<>();
                    parentsCB = new ComboBox<>( GlobalIds.PARENTS, new PropertyModel<String>( this,
                        PARENTS_SELECTION ), parents );
                }
                nameTF.setEnabled( false );
                addPB.setEnabled( false );
                editForm.addOrReplace( parentsCB );
                String msg = "Role: " + ( ( Role ) role ).getName() + " has been selected";
                log.debug( msg );
                display.setMessage( msg );
                component = editForm;
            }
            else if ( event.getPayload() instanceof AjaxRequestTarget )
            {
                // only add the form to ajax target if something has changed...
                if ( component != null )
                {
                    AjaxRequestTarget target = ( ( AjaxRequestTarget ) event.getPayload() );
                    log.debug( ".onEvent AjaxRequestTarget: " + target.toString() );
                    target.add( component );
                    component = null;
                }
                display.display( ( AjaxRequestTarget ) event.getPayload() );
            }
        }

        @Override
        protected void onBeforeRender()
        {
            if ( getModel() != null )
            {
                // push the 'changed' model down into the constraint panel:
                constraintPanel.setDefaultModel( getModel() );
                if ( isAdmin )
                {
                    // push the 'changed' model down into the admin role detail panel:
                    auxPanel.setDefaultModel( getModel() );
                    AdminRole role = ( AdminRole ) getModel().getObject();
                    if ( role != null )
                    {
                        if ( role.getOsPSet() != null )
                        {
                            auxPanel.setPermous( new ArrayList<>( role.getOsPSet() ) );
                        }
                        else
                        {
                            auxPanel.setPermous( new ArrayList<String>() );
                        }
                        if ( role.getOsUSet() != null )
                        {
                            auxPanel.setUserous( new ArrayList<>( role.getOsUSet() ) );
                        }
                        else
                        {
                            auxPanel.setUserous( new ArrayList<String>() );
                        }
                    }
                }
            }
            else
            {
                log.info( ".onBeforeRender null model object" );
            }
            super.onBeforeRender();
        }

        private void clearDetailFields()
        {
            if ( isAdmin )
            {
                setModelObject( new AdminRole() );
            }
            else
            {
                setModelObject( new Role() );
            }
            parentsSelection = "";
            parents = new ArrayList<>();
            parentsCB = new ComboBox<>( GlobalIds.PARENTS, new PropertyModel<String>( this,
                PARENTS_SELECTION ), parents );
            modelChanged();
            nameTF.setEnabled( true );
            addPB.setEnabled( true );
            editForm.addOrReplace( parentsCB );
        }
    }
}