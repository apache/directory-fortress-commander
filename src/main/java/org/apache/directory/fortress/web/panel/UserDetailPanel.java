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
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.combobox.ComboBox;
import com.googlecode.wicket.kendo.ui.form.dropdown.AjaxDropDownList;
import com.googlecode.wicket.kendo.ui.form.dropdown.DropDownList;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.PwPolicy;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.model.UserAdminRole;
import org.apache.directory.fortress.core.model.UserRole;
import org.apache.directory.fortress.core.model.Constraint;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
*/
public class UserDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private AdminMgr adminMgr;
    @SpringBean
    private DelAdminMgr delAdminMgr;
    private static final Logger log = Logger.getLogger( UserDetailPanel.class.getName() );
    private Form editForm;
    private Displayable display;

    public UserDetailPanel( String id, Displayable display )
    {
        super( id );
        this.editForm = new UserDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<>( new User() ) );
        this.display = display;
        this.adminMgr.setAdmin( SecUtils.getSession( this ) );
        this.delAdminMgr.setAdmin( SecUtils.getSession( this ) );
        add( editForm );
    }

    /**
     *
     */
    public class UserDetailForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        // form constants:
        private static final String OU = "ou";
        private static final String ROLES = "roles";
        private static final String ROLE_SELECTION = "roleSelection";
        private static final String ROLECONSTRAINTPANEL = "roleconstraintpanel";
        private static final String ROLE_CONSTRAINT = "roleConstraint";
        private static final String ADMIN_ROLE_SELECTION = "adminRoleSelection";
        private static final String ADMINROLECONSTRAINTPANEL = "adminroleconstraintpanel";
        private static final String ADMIN_ROLE_CONSTRAINT = "adminRoleConstraint";
        private static final String EMAILS_SELECTION = "emailsSelection";
        private static final String PHONES_SELECTION = "phonesSelection";
        private static final String MOBILES_SELECTION = "mobilesSelection";
        private static final String ADDRESS_SELECTION = "addressSelection";

        private static final String USER_DETAIL_LABEL = "userDetailLabel";
        private static final String ROLE_ASSIGNMENTS_LABEL = "roleAssignmentsLabel";
        private static final String ADMIN_ROLE_ASSIGNMENTS_LABEL = "adminRoleAssignmentsLabel";
        private static final String ADDRESS_ASSIGNMENTS_LABEL = "addressAssignmentsLabel";
        private static final String CONTACT_INFORMATION_LABEL = "contactInformationLabel";
        private static final String TEMPORAL_CONSTRAINTS_LABEL = "temporalConstraintsLabel";
        private static final String SYSTEM_INFO_LABEL = "systemInfoLabel";
        private static final String IMPORT_PHOTO_LABEL = "importPhotoLabel";
        private static final String UPLOAD = "upload";
        private static final String ADMIN_ROLES = "adminRoles";
        private static final String DEFAULT_JPG = "GenericAvatar.jpg";

        // form model attributes:
        private String newUserRole;
        private String newUserAdminRole;
        private String userDetailLabel;
        private String lockLabel = "Lock";
        private String roleAssignmentsLabel;
        private String adminRoleAssignmentsLabel;
        private String addressAssignmentsLabel;
        private String contactInformationLabel;
        private String temporalConstraintsLabel = "Temporal Constraints";
        private String systemInfoLabel = "System Information";
        private String importPhotoLabel = "Import Photo";
        private UserRole userRoleSelection = new UserRole();
        private UserAdminRole userAdminRoleSelection = new UserAdminRole();

        private String addressSelection;
        private String phonesSelection;
        private String mobilesSelection;
        private String emailsSelection;
        private UserRole roleConstraint = new UserRole();
        private UserAdminRole adminRoleConstraint = new UserAdminRole();
        private Constraint userConstraint;
        private byte[] defaultImage;

        // form view components:
        private Component component;
        private ConstraintPanel constraintPanel;
        private ConstraintRolePanel roleConstraintPanel;
        private ConstraintAdminRolePanel adminRoleConstraintPanel;
        private ComboBox<String> emailsCB;
        private ComboBox<String> phonesCB;
        private ComboBox<String> mobilesCB;
        private ComboBox<String> addressCB;
        private DropDownList<UserRole> rolesDD;
        private DropDownList<UserAdminRole> adminRolesDD;
        private FileUploadField upload;
        private TextField pwPolicyTF;
        private TextField ouTF;
        private TextField userIdTF;
        private TextField newUserRoleTF;
        private TextField newUserAdminRoleTF;
        private SecureIndicatingAjaxButton addPB;

        public UserDetailForm( String id, final IModel<User> model )
        {
            super( id, model );
            editForm = this;
            add( new JQueryBehavior( "#accordion", "accordion" ) );
            addButtons();
            addLabels();
            addDetailFields( model );
            initAccordionLabels();
            addPhoto();
            setOutputMarkupId( true );
        }

        private void addDetailFields( final IModel<User> model )
        {
            // Add the User page required attributes:
            userIdTF = new TextField( GlobalIds.USER_ID );
            add( userIdTF );
            PasswordTextField pw = new PasswordTextField( GlobalIds.PASSWORD );
            pw.setRequired( false );
            add( pw );
            TextField descriptionTF = new TextField( GlobalIds.DESCRIPTION );
            descriptionTF.setRequired( false );
            add( descriptionTF );
            ouTF = new TextField( OU );
            // making this required prevents the modals from opening:
            //ouTF.setRequired( true );
            ouTF.setOutputMarkupId( true );
            add( ouTF );
            CheckBox resetCB = new CheckBox( "reset" );
            resetCB.setEnabled( false );
            add( resetCB );
            CheckBox lockedCB = new CheckBox( "locked" );
            lockedCB.setEnabled( false );
            add( lockedCB );
            pwPolicyTF = new TextField( "pwPolicy" );
            pwPolicyTF.setRequired( false );
            pwPolicyTF.setOutputMarkupId( true );
            add( pwPolicyTF );

            // Add the role assignment values & temporal constraint panel:
            newUserRoleTF = new TextField( GlobalIds.NEW_USER_ROLE_FIELD, new PropertyModel<String>( this,
                GlobalIds.NEW_USER_ROLE_FIELD ) );
            newUserRoleTF.setRequired( false );
            newUserRoleTF.setOutputMarkupId( true );
            add( newUserRoleTF );

            rolesDD = new AjaxDropDownList<>(ROLES, new CompoundPropertyModel<>( new UserRole() ), Model.ofList(new ArrayList<UserRole>()));
            add( rolesDD );

            roleConstraintPanel = new ConstraintRolePanel( ROLECONSTRAINTPANEL, new PropertyModel<UserRole>( this,
                ROLE_CONSTRAINT ) );
            roleConstraintPanel.setOutputMarkupId( true );
            add( roleConstraintPanel );

            // Add the adminRole assignment values & temporal constraint panel:
            newUserAdminRoleTF = new TextField( GlobalIds.NEW_USER_ADMIN_ROLE_FIELD, new PropertyModel<String>( this,
                GlobalIds.NEW_USER_ADMIN_ROLE_FIELD ) );
            newUserAdminRoleTF.setRequired( false );
            newUserAdminRoleTF.setOutputMarkupId( true );
            add( newUserAdminRoleTF );

            adminRolesDD = new AjaxDropDownList<>(ADMIN_ROLES, new CompoundPropertyModel<>( new UserAdminRole() ), Model.ofList(new ArrayList<UserAdminRole>()));
            add( adminRolesDD );

            adminRoleConstraintPanel = new ConstraintAdminRolePanel( ADMINROLECONSTRAINTPANEL,
                new PropertyModel<UserAdminRole>( this, ADMIN_ROLE_CONSTRAINT ) );
            adminRoleConstraintPanel.setOutputMarkupId( true );
            add( adminRoleConstraintPanel );

            // Contact ComboBoxes and edit fields
            TextField employeeType = new TextField( GlobalIds.EMPLOYEE_TYPE );
            employeeType.setRequired( false );
            add( employeeType );
            TextField title = new TextField( GlobalIds.TITLE );
            title.setRequired( false );
            add( title );

            // TODO: add email validator:
            //add(new TextField("email").add( EmailAddressValidator.getInstance()));

            emailsCB = new ComboBox<>( GlobalIds.EMAILS, new PropertyModel<String>( this, EMAILS_SELECTION ),
                model.getObject().getEmails() );
            add( emailsCB );
            phonesCB = new ComboBox<>( GlobalIds.PHONES, new PropertyModel<String>( this, PHONES_SELECTION ),
                model.getObject().getPhones() );
            add( phonesCB );
            mobilesCB = new ComboBox<>( GlobalIds.MOBILES, new PropertyModel<String>( this, MOBILES_SELECTION ),
                model.getObject().getMobiles() );
            add( mobilesCB );

            // TODO: name not mapped correctly in fortress so can't be used here:
            TextField name = new TextField( "displayName" );
            name.setRequired( false );
            add( name );

            // Address ComboBoxes and edit fields
            addressCB = new ComboBox<>( GlobalIds.ADDRESSES,
                new PropertyModel<String>( this, ADDRESS_SELECTION ),
                model.getObject().getAddress().getAddresses() );
            add( addressCB );
            TextField city = new TextField( GlobalIds.ADDRESS_CITY );
            city.setRequired( false );
            add( city );
            TextField state = new TextField( GlobalIds.ADDRESS_STATE );
            state.setRequired( false );
            add( state );
            TextField country = new TextField( GlobalIds.ADDRESS_COUNTRY );
            country.setRequired( false );
            add( country );
            TextField postalCode = new TextField( GlobalIds.ADDRESS_POSTAL_CODE );
            postalCode.setRequired( false );
            add( postalCode );
            TextField postOfficeBox = new TextField( GlobalIds.ADDRESS_POST_OFFICE_BOX );
            postOfficeBox.setRequired( false );
            add( postOfficeBox );
            TextField building = new TextField( GlobalIds.ADDRESS_BUILDING );
            building.setRequired( false );
            add( building );
            TextField departmentNumber = new TextField( GlobalIds.ADDRESS_DEPARTMENT_NUMBER );
            departmentNumber.setRequired( false );
            add( departmentNumber );
            TextField roomNumber = new TextField( GlobalIds.ADDRESS_ROOM_NUMBER );
            roomNumber.setRequired( false );
            add( roomNumber );

            // Add System fields:
            CheckBox system = new CheckBox( "system" );
            system.setRequired( false );
            add( system );
            Label internalid = new Label( "internalId" );
            add( internalid );
            TextField cn = new TextField( "cn" );
            cn.setRequired( false );
            add( cn );
            Label dn = new Label( "dn" );
            add( dn );
            TextField sn = new TextField( "sn" );
            sn.setRequired( false );
            add( sn );

            // Add the temporal constraint panel:
            constraintPanel = new ConstraintPanel( "constraintpanel", model );
            add( constraintPanel );

        }


        private void addButtons()
        {
            add( addPB = new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.ADMIN_MGR, GlobalIds.ADD_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Add" );
                    User user = ( User ) form.getModel().getObject();
                    updateEntityWithComboData( user );
                    try
                    {
                        adminMgr.addUser( user );
                        SaveModelEvent.send( getPage(), this, user, target, SaveModelEvent.Operations.ADD );
                        component = editForm;
                        String msg = "User: " + user.getUserId() + " has been added";
                        display.setMessage( msg );
                        initAccordionLabels( user );
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
                    log.info( "UserDetailPanel.add.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.ADMIN_MGR, GlobalIds.UPDATE_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    log.debug( ".onSubmit commit" );
                    User user = ( User ) form.getModel().getObject();
                    // todo: fix this, going from string to char back to string (in ldap)?
/*
                    if ( pswdField != null )
                    {
                        user.setPassword( pswdField.toCharArray() );
                    }
                    else
                    {
                        user.setPassword( "".toCharArray() );
                    }
*/
                    updateEntityWithComboData( user );
                    try
                    {
                        adminMgr.updateUser( user );
                        editForm.setOutputMarkupId( true );
                        component = editForm;
                        String msg = "User: " + user.getUserId() + " has been updated";
                        display.setMessage( msg );
                        initAccordionLabels( user );
                        initSelectionModels();
                        SaveModelEvent.send( getPage(), this, user, target, SaveModelEvent.Operations.UPDATE );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = "commit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "UserDetailPanel.commit.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.ADMIN_MGR, GlobalIds.DELETE_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    User user = ( User ) form.getModel().getObject();
                    try
                    {
                        adminMgr.deleteUser( user );
                        clearDetailPanel();
                        String msg = "User: " + user.getUserId() + " has been deleted";
                        display.setMessage( msg );
                        SaveModelEvent.send( getPage(), this, user, target, SaveModelEvent.Operations.DELETE );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "UserDetailPanel.delete.onError" );
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
                    clearDetailPanel();
                    String msg = "User cancelled input form";
                    display.setMessage( msg );
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "UserDetailPanel.cancel.onError" );
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
            add( new AjaxSubmitLink( "save" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    FileUpload fileUpload = upload.getFileUpload();
                    log.debug( ".onSubmit Save" );
                    User user = ( User ) form.getModel().getObject();
                    user.setJpegPhoto( fileUpload.getBytes() );
                    component = editForm;
                    String msg = "User: " + user.getUserId() + " photo uploaded successfully.  Must commit for photo " +
                        "to be persisted on record.";
                    display.setMessage( msg );
                    initAccordionLabels( user );
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "save.onError" );
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
            Label lockBtnLbl = new Label( "lockLabel", new PropertyModel<String>( this, "lockLabel" ) );
            SecureIndicatingAjaxButton lock = new SecureIndicatingAjaxButton( "lockbtn", GlobalIds.ADMIN_MGR,
                "lockUserAccount" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    User user = ( User ) form.getModel().getObject();
                    String msg = "User: " + user.getUserId();
                    try
                    {
                        if ( user.isLocked() )
                        {
                            adminMgr.unlockUserAccount( user );
                            user.setLocked( false );
                            msg += " account has been unlocked";
                        }
                        else
                        {
                            adminMgr.lockUserAccount( user );
                            user.setLocked( true );
                            msg += " account has been locked";
                        }
                        display.setMessage( msg );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                    }

                    component = editForm;
                    initAccordionLabels( user );
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "ControlPanel.lock/unlock error" );
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
            };
            lock.add( lockBtnLbl );
            add( lock );
            add( new SecureIndicatingAjaxButton( "resetbtn", GlobalIds.ADMIN_MGR, "resetPassword" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    User user = ( User ) form.getModel().getObject();
                    String msg = "User: " + user.getUserId();
                    try
                    {
                        adminMgr.resetPassword( user, user.getPassword() );
                        user.setReset( true );
                        msg += " account has been reset";
                        display.setMessage( msg );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                    }

                    component = editForm;
                    initAccordionLabels( user );
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "ControlPanel.reset error" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.ASSIGN, GlobalIds.ADMIN_MGR, GlobalIds.ASSIGN_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit assign" );
                    User user = ( User ) form.getModel().getObject();
                    if( StringUtils.isNotEmpty( newUserRole ))
                    {
                        if ( assignRole( user, newUserRole ) )
                        {
                            String msg = "User: " + user.getUserId() + " has been assigned role: " + newUserRole;
                            display.setMessage( msg );
                            component = editForm;
                            initAccordionLabels( user );
                            userRoleSelection = new UserRole();
                            roleConstraint = new UserRole();
                            newUserRole = "";
                        }
                        else
                        {
                            String msg = "User: " + user.getUserId() + " assign failed for role: " + newUserRole;
                            display.setMessage( msg );
                            log.debug( msg );
                        }
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "assign.onError" );
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
            add( new SecureIndicatingAjaxButton( "assignAdminRole", GlobalIds.DEL_ADMIN_MGR, GlobalIds.ASSIGN_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit assignAdminRole" );
                    User user = ( User ) form.getModel().getObject();
                    if( StringUtils.isNotEmpty( newUserAdminRole ))
                    {
                        if ( assignAdminRole( user, newUserAdminRole ) )
                        {
                            String msg = "User: " + user.getUserId() + " has been assigned adminRole: " +
                                newUserAdminRole;
                            display.setMessage( msg );
                            component = editForm;
                            initAccordionLabels( user );
                            userAdminRoleSelection = new UserAdminRole();
                            adminRoleConstraint = new UserAdminRole();
                            newUserAdminRole = "";
                        }
                        else
                        {
                            String msg = "User: " + user.getUserId() + " assign failed for admin role: " + newUserAdminRole;
                            display.setMessage( msg );
                            log.debug( msg );
                        }
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "assignAdminRole.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DEASSIGN, GlobalIds.ADMIN_MGR, GlobalIds.DEASSIGN_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit deassign" );
                    User user = ( User ) form.getModel().getObject();
                    UserRole userRole = new UserRole( user.getUserId(), userRoleSelection.getName() );
                    if ( deassignRole( user, userRole ) )
                    {
                        user.delRole( userRoleSelection );
                        String msg = "User: " + user.getUserId() + " has been deassigned role: " + userRoleSelection.getName();
                        display.setMessage( msg );
                        component = editForm;
                        initAccordionLabels( user );
                    }
                    userRoleSelection = new UserRole();
                    roleConstraint = new UserRole();
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "deassign.onError" );
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
            add( new SecureIndicatingAjaxButton( "deassignAdminRole", GlobalIds.DEL_ADMIN_MGR, GlobalIds.DEASSIGN_USER )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit deassignAdminRole" );
                    User user = ( User ) form.getModel().getObject();
                    UserAdminRole userAdminRole = new UserAdminRole( user.getUserId(), userAdminRoleSelection.getName() );
                    if ( deassignAdminRole( user, userAdminRole ) )
                    {
                        user.delAdminRole( userAdminRole );
                        String msg = "User: " + user.getUserId() + " has been deassigned adminRole: " +
                            userAdminRoleSelection.getName();
                        display.setMessage( msg );
                        component = editForm;
                        initAccordionLabels( user );
                    }
                    userAdminRoleSelection = new UserAdminRole();
                    adminRoleConstraint = new UserAdminRole();
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "ControlPanel.deassignAdminRole.onError" );
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
            add( new AjaxButton( "address.delete" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on address.delete";
                    if ( StringUtils.isNotEmpty( addressSelection ) )
                    {
                        msg += " selection:" + addressSelection;
                        User user = ( User ) form.getModel().getObject();
                        if ( user.getAddress() != null && user.getAddress().getAddresses() != null )
                        {
                            user.getAddress().getAddresses().remove( addressSelection );
                            addressSelection = "";
                            component = editForm;
                            initAccordionLabels( user );
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because user does not have address set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because address selection is empty";
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
            add( new AjaxButton( "emails.delete" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on emails.delete";
                    if ( StringUtils.isNotEmpty( emailsSelection ) )
                    {
                        msg += " selection:" + emailsSelection;
                        User user = ( User ) form.getModel().getObject();
                        if ( user.getEmails() != null )
                        {
                            user.getEmails().remove( emailsSelection );
                            emailsSelection = "";
                            component = editForm;
                            initAccordionLabels( user );
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because user does not have emails set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because emails selection is empty";
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
            add( new AjaxButton( "phones.delete" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on phones.delete";
                    if ( StringUtils.isNotEmpty( phonesSelection ) )
                    {
                        msg += " selection:" + phonesSelection;
                        User user = ( User ) form.getModel().getObject();
                        if ( user.getPhones() != null )
                        {
                            user.getPhones().remove( phonesSelection );
                            phonesSelection = "";
                            component = editForm;
                            initAccordionLabels( user );
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because user does not have phones set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because phones selection is empty";
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
            add( new AjaxButton( "mobiles.delete" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on mobiles.delete";
                    if ( StringUtils.isNotEmpty( mobilesSelection ) )
                    {
                        msg += " selection:" + mobilesSelection;
                        User user = ( User ) form.getModel().getObject();
                        if ( user.getMobiles() != null )
                        {
                            user.getMobiles().remove( mobilesSelection );
                            mobilesSelection = "";
                            component = editForm;
                            initAccordionLabels( user );
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because user does not have mobiles set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because mobiles selection is empty";
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

            addRoleSearchModal();
            addAdminRoleSearchModal();
            addPolicySearchModal();
            addOUSearchModal();
        }


        private void addRoleSearchModal()
        {
            final ModalWindow rolesModalWindow;
            add( rolesModalWindow = new ModalWindow( "rolesmodal" ) );
            final RoleSearchModalPanel roleSearchModalPanel = new RoleSearchModalPanel(
                rolesModalWindow.getContentId(), rolesModalWindow, false );
            rolesModalWindow.setContent( roleSearchModalPanel );
            rolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    roleConstraint = roleSearchModalPanel.getRoleSelection();
                    newUserRole = roleConstraint.getName();
                    target.add( roleConstraintPanel );
                    target.add( newUserRoleTF );
                }
            } );

            add( new AjaxButton( GlobalIds.ROLES_SEARCH )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles search";
                    msg += userRoleSelection != null ? ": " + newUserRole : "";
                    roleSearchModalPanel.setRoleSearchVal( newUserRole );
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

            rolesModalWindow.setTitle( "Role Selection Modal" );
            rolesModalWindow.setInitialWidth( 700 );
            rolesModalWindow.setInitialHeight( 450 );
            rolesModalWindow.setCookieName( "role-assign-modal" );
        }


        private void addAdminRoleSearchModal()
        {
            final ModalWindow adminRolesModalWindow;
            add( adminRolesModalWindow = new ModalWindow( "adminrolesmodal" ) );
            final RoleSearchModalPanel adminRoleSearchModalPanel = new RoleSearchModalPanel(
                adminRolesModalWindow.getContentId(), adminRolesModalWindow, true );
            adminRolesModalWindow.setContent( adminRoleSearchModalPanel );
            adminRolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    adminRoleConstraint = adminRoleSearchModalPanel.getAdminRoleSelection();
                    newUserAdminRole = adminRoleConstraint.getName();
                    target.add( adminRoleConstraintPanel );
                    target.add( newUserAdminRoleTF );
                }
            } );

            add( new AjaxButton( "adminRoles.search" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles search";
                    msg += userAdminRoleSelection.getName() != null ? ": " + newUserAdminRole : "";
                    adminRoleSearchModalPanel.setRoleSearchVal( newUserAdminRole );
                    display.setMessage( msg );
                    log.debug( msg );
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

            adminRolesModalWindow.setTitle( "Admin Role Selection Modal" );
            adminRolesModalWindow.setInitialWidth( 700 );
            adminRolesModalWindow.setInitialHeight( 450 );
            adminRolesModalWindow.setCookieName( "adminrole-assign-modal" );
        }


        private void addPolicySearchModal()
        {
            final ModalWindow policiesModalWindow;
            add( policiesModalWindow = new ModalWindow( "policiesmodal" ) );
            final PwPolicySearchModalPanel policySearchModalPanel = new PwPolicySearchModalPanel(
                policiesModalWindow.getContentId(), policiesModalWindow );
            policiesModalWindow.setContent( policySearchModalPanel );
            policiesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    PwPolicy pwPolicy = policySearchModalPanel.getPolicySelection();
                    if ( pwPolicy != null )
                    {
                        User user = ( User ) editForm.getModel().getObject();
                        user.setPwPolicy( pwPolicy.getName() );
                        target.add( pwPolicyTF );
                    }
                }
            } );

            add( new AjaxButton( GlobalIds.POLICY_SEARCH )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on policies search";
                    User user = ( User ) editForm.getModel().getObject();
                    msg += user.getPwPolicy() != null ? ": " + user.getPwPolicy() : "";
                    policySearchModalPanel.setSearchVal( user.getPwPolicy() );
                    display.setMessage( msg );
                    log.debug( msg );
                    target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                    policiesModalWindow.show( target );
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

            policiesModalWindow.setTitle( "Password Policy Selection Modal" );
            policiesModalWindow.setInitialWidth( 900 );
            policiesModalWindow.setInitialHeight( 450 );
            policiesModalWindow.setCookieName( "policy-modal" );
        }


        private void addOUSearchModal()
        {
            final ModalWindow ousModalWindow;
            add( ousModalWindow = new ModalWindow( "ousmodal" ) );
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
                        User user = ( User ) editForm.getModel().getObject();
                        user.setOu( ou.getName() );
                        target.add( ouTF );
                    }
                }
            } );

            add( new AjaxButton( GlobalIds.OU_SEARCH )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on OrgUnits search";
                    User user = ( User ) editForm.getModel().getObject();
                    msg += user.getOu() != null ? ": " + user.getOu() : "";
                    ouSearchModalPanel.setSearchVal( user.getOu() );
                    display.setMessage( msg );
                    log.debug( msg );
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

            ousModalWindow.setTitle( "User Organizational Unit Selection Modal" );
            ousModalWindow.setInitialWidth( 450 );
            ousModalWindow.setInitialHeight( 450 );
            ousModalWindow.setCookieName( "userou-modal" );
        }

        private void info( ComboBox<String> comboBox )
        {
            String choice = comboBox.getModelObject();
            String msg = choice != null ? choice : "no choice";
            this.debug( msg );
            log.debug( msg );
        }

        private void clearDetailPanel()
        {
            setModelObject( new User() );
            userRoleSelection = new UserRole();
            userAdminRoleSelection = new UserAdminRole();
            newUserRole = "";
            newUserAdminRole = "";
            addressSelection = "";
            phonesSelection = "";
            roleConstraint = new UserRole();
            adminRoleConstraint = new UserAdminRole();

            userIdTF.setEnabled( true );
            addPB.setEnabled( true );

            emailsCB = new ComboBox<>( GlobalIds.EMAILS, new PropertyModel<String>( this, EMAILS_SELECTION ),
                new ArrayList<String>() );
            editForm.addOrReplace( emailsCB );
            phonesCB = new ComboBox<>( GlobalIds.PHONES, new PropertyModel<String>( this, PHONES_SELECTION ),
                new ArrayList<String>() );
            editForm.addOrReplace( phonesCB );
            mobilesCB = new ComboBox<>( GlobalIds.MOBILES, new PropertyModel<String>( this, MOBILES_SELECTION ),
                new ArrayList<String>() );
            editForm.addOrReplace( mobilesCB );
            addressCB = new ComboBox<>( GlobalIds.ADDRESSES,
                new PropertyModel<String>( this, ADDRESS_SELECTION ),
                new ArrayList<String>() );

            editForm.addOrReplace( addressCB );
            rolesDD = new AjaxDropDownList<>(ROLES, new CompoundPropertyModel<>( new UserRole() ), Model.ofList(new ArrayList<UserRole>()));
            rolesDD.setOutputMarkupId( true );
            editForm.addOrReplace( rolesDD );

            adminRolesDD = new AjaxDropDownList<>(ADMIN_ROLES, new CompoundPropertyModel<>( new UserAdminRole() ), Model.ofList(new ArrayList<UserAdminRole>()));
            adminRolesDD.setOutputMarkupId( true );
            editForm.addOrReplace( adminRolesDD );

            modelChanged();
            component = editForm;
            editForm.setOutputMarkupId( true );
            initAccordionLabels();
        }


        private void addLabels()
        {
            // Add the page labels:
            add( new Label( USER_DETAIL_LABEL, new PropertyModel<String>( this, USER_DETAIL_LABEL ) ) );
            add( new Label( ROLE_ASSIGNMENTS_LABEL, new PropertyModel<String>( this, ROLE_ASSIGNMENTS_LABEL ) ) );
            add( new Label( ADMIN_ROLE_ASSIGNMENTS_LABEL, new PropertyModel<String>( this,
                ADMIN_ROLE_ASSIGNMENTS_LABEL ) ) );
            add( new Label( ADDRESS_ASSIGNMENTS_LABEL, new PropertyModel<String>( this, ADDRESS_ASSIGNMENTS_LABEL ) ) );
            add( new Label( CONTACT_INFORMATION_LABEL, new PropertyModel<String>( this, CONTACT_INFORMATION_LABEL ) ) );
            add( new Label( TEMPORAL_CONSTRAINTS_LABEL, new PropertyModel<String>( this,
                TEMPORAL_CONSTRAINTS_LABEL ) ) );
            add( new Label( SYSTEM_INFO_LABEL, new PropertyModel<String>( this, SYSTEM_INFO_LABEL ) ) );
            add( new Label( IMPORT_PHOTO_LABEL, new PropertyModel<String>( this, IMPORT_PHOTO_LABEL ) ) );
        }


        private void addPhoto()
        {
            this.defaultImage = readJpegFile( DEFAULT_JPG );
            // Add the photograph controls:
            add( new JpegImage( GlobalIds.JPEGPHOTO )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected byte[] getPhoto()
                {
                    byte[] photo;
                    User user = ( User ) getModel().getObject();
                    photo = user.getJpegPhoto();
                    if ( photo == null || photo.length == 0 )
                    {
                        photo = defaultImage;
                    }
                    return photo;
                }
            } );
            add( new JpegImage( GlobalIds.JPEGPHOTO + "2" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected byte[] getPhoto()
                {
                    byte[] photo;
                    User user = ( User ) getModel().getObject();
                    photo = user.getJpegPhoto();
                    if ( photo == null || photo.length == 0 )
                    {
                        photo = defaultImage;
                    }
                    return photo;
                }
            } );
            upload = new FileUploadField( UPLOAD, new Model( UPLOAD ) );
            add( upload );
        }


        private void updateEntityWithComboData( User user )
        {
            if ( StringUtils.isNotEmpty( emailsSelection ) )
            {
                user.setEmail( emailsSelection );
            }
            if ( StringUtils.isNotEmpty( phonesSelection ) )
            {
                user.setPhone( phonesSelection );
            }
            if ( StringUtils.isNotEmpty( mobilesSelection ) )
            {
                user.setMobile( mobilesSelection );
            }
            if ( StringUtils.isNotEmpty( addressSelection ) )
            {
                user.getAddress().setAddress( addressSelection );
            }
        }


        private boolean assignRole( User user, String szRoleName )
        {
            boolean success = false;
            if ( StringUtils.isNotEmpty( szRoleName ) )
            {
                UserRole userRole = roleConstraint;
                userRole.setUserId( user.getUserId() );
                userRole.setName( szRoleName );
                if ( !user.getRoles().contains( userRole ) )
                {
                    try
                    {
                        adminMgr.assignUser( userRole );
                        success = true;
                        user.setRole( userRole );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String warning = "AssignUser caught SecurityException=" + se;
                        log.warn( warning );
                        error( "Error assigning role: " + szRoleName + " error id: " + se.getErrorId() );
                        component = editForm;
                    }
                }
                else
                {
                    String warning = ".assignUser user [" + user.getUserId() + "] already assigned role [" +
                        szRoleName + "]";
                    log.warn( warning );
                    error( "User already assigned role: " + szRoleName );
                    component = editForm;
                }
            }
            return success;
        }


        private boolean deassignRole( User user, UserRole userRole )
        {
            boolean success = false;
            if ( user.getRoles().contains( userRole ) )
            {
                try
                {
                    adminMgr.deassignUser( userRole );
                    success = true;
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String warning = "AssignUser caught SecurityException=" + se;
                    log.warn( warning );
                    error( "Error deassigning role: " + user.getName() + " error id: " + se.getErrorId() );
                    component = editForm;
                }
            }
            return success;
        }


        private boolean assignAdminRole( User user, String szAdminRoleName )
        {
            boolean success = false;
            if ( StringUtils.isNotEmpty( szAdminRoleName ) )
            {
                UserAdminRole userAdminRole = adminRoleConstraint;
                userAdminRole.setUserId( user.getUserId() );
                userAdminRole.setName( szAdminRoleName );
                if ( !user.getAdminRoles().contains( userAdminRole ) )
                {
                    try
                    {
                        delAdminMgr.assignUser( userAdminRole );
                        success = true;
                        user.setAdminRole( userAdminRole );
                    }
                    catch ( org.apache.directory.fortress.core.SecurityException se )
                    {
                        String warning = "AssignAdminUser caught SecurityException=" + se;
                        log.warn( warning );
                        error( "Error assigning adminRole: " + szAdminRoleName + " error id: " + se.getErrorId() );
                        component = editForm;
                    }
                }
                else
                {
                    String warning = ".assignUser user [" + user.getUserId() + "] already assigned adminRole" +
                        " [" +
                        szAdminRoleName + "]";
                    log.warn( warning );
                    error( "User already assigned adminRole: " + szAdminRoleName );
                    component = editForm;
                }
            }
            return success;
        }


        private boolean deassignAdminRole( User user, UserAdminRole userAdminRole )
        {
            boolean success = false;
            if ( user.getAdminRoles().contains( userAdminRole ) )
            {
                try
                {
                    delAdminMgr.deassignUser( userAdminRole );
                    success = true;
                }
                catch ( org.apache.directory.fortress.core.SecurityException se )
                {
                    String warning = "AssignAdminUser caught SecurityException=" + se;
                    log.warn( warning );
                    error( "Error assigning adminRole: " + userAdminRole.getName() + " error id: " + se.getErrorId() );
                    component = editForm;
                }
            }
            return success;
        }

        @Override
        public void onEvent( final IEvent<?> event )
        {

            if ( event.getPayload() instanceof SelectModelEvent )
            {
                clearDetailPanel();
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                final User user = ( User ) modelEvent.getEntity();
                // strip the attribute name from value.  e.g. cn=value:
                user.setPwPolicy( getPolicyName( user.getPwPolicy() ) );
                this.setModelObject( user );
                initAccordionLabels( user );
                String msg = "User: " + user.getUserId() + " has been selected";
                log.debug( msg );
                display.setMessage( msg );
                userIdTF.setEnabled( false );
                addPB.setEnabled( false );

                rolesDD = new AjaxDropDownList<UserRole>(ROLES, new CompoundPropertyModel<>( new UserRole() ), Model.ofList(user.getRoles()))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSelectionChanged(AjaxRequestTarget target)
                    {
                        userRoleSelection =  this.getModelObject();
                        if ( userRoleSelection != null )
                        {
                            info( "roleNm=" + userRoleSelection.getName() );
                            roleConstraint = userRoleSelection;
                            roleConstraintPanel.setOutputMarkupId( true );
                            target.add( roleConstraintPanel );
                            // TODO: should we clear out any unfinished work here:
                            //userAdminRoleSelection = new UserAdminRole();
                            //target.add( adminRolesDD );
                        }
                    }
                };
                this.rolesDD.setOutputMarkupId( true );
                editForm.addOrReplace( rolesDD );

                adminRolesDD = new AjaxDropDownList<UserAdminRole>(ADMIN_ROLES, new CompoundPropertyModel<>( new UserAdminRole() ), Model.ofList(user.getAdminRoles()))
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSelectionChanged(AjaxRequestTarget target)
                    {
                        userAdminRoleSelection =  this.getModelObject();
                        if ( userAdminRoleSelection != null )
                        {
                            info( "roleNm=" + userRoleSelection.getName() );
                            adminRoleConstraint = userAdminRoleSelection;
                            adminRoleConstraintPanel.setOutputMarkupId( true );
                            target.add( adminRoleConstraintPanel );
                            // TODO: should we clear out any unfinished work here:
                            // userRoleSelection = new UserRole();
                            // target.add( rolesDD );
                        }
                    }
                };
                this.adminRolesDD.setOutputMarkupId( true );
                editForm.addOrReplace( adminRolesDD );

                emailsCB = new ComboBox<>( GlobalIds.EMAILS, new PropertyModel<String>( this, EMAILS_SELECTION ),
                    user.getEmails() );
                editForm.addOrReplace( emailsCB );
                phonesCB = new ComboBox<>( GlobalIds.PHONES, new PropertyModel<String>( this, PHONES_SELECTION ),
                    user.getPhones() );
                editForm.addOrReplace( phonesCB );
                mobilesCB = new ComboBox<>( GlobalIds.MOBILES,
                    new PropertyModel<String>( this, MOBILES_SELECTION ),
                    user.getMobiles() );
                editForm.addOrReplace( mobilesCB );
                addressCB = new ComboBox<>( GlobalIds.ADDRESSES, new PropertyModel<String>( this,
                    ADDRESS_SELECTION ),
                    user.getAddress().getAddresses() );
                editForm.addOrReplace( addressCB );
                roleConstraint = new UserRole();
                editForm.addOrReplace( roleConstraintPanel );
                adminRoleConstraint = new UserAdminRole();
                editForm.addOrReplace( adminRoleConstraintPanel );
                component = editForm;
            }
            // TODO: fix me... don't want to add detail form to every ajax request target that passes though:
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
                // necessary to push the 'changed' model down into the aggregated constraint panel:
                constraintPanel.setDefaultModel( getModel() );
            }
            else
            {
                log.info( ".onBeforeRender null model object" );
            }
            super.onBeforeRender();
        }

        private void initAccordionLabels()
        {
            userDetailLabel = "User Detail";
            roleAssignmentsLabel = "RBAC Role Assignments";
            adminRoleAssignmentsLabel = "Admin Role Assignments";
            addressAssignmentsLabel = "Address Assignments";
            contactInformationLabel = "Contact Information";
        }

        private void initAccordionLabels( User user )
        {
            boolean isSet = false;
            if ( user.isLocked() )
            {
                lockLabel = "Unlock";
            }
            else
            {
                lockLabel = "Lock";
            }
            userDetailLabel = "User Detail: " + user.getUserId();
            roleAssignmentsLabel = "RBAC Role Assignments";
            if ( CollectionUtils.isNotEmpty( user.getRoles() ) )
            {
                roleAssignmentsLabel += ": " + user.getRoles().get( 0 ) + " + " + ( user.getRoles().size() - 1 );
            }
            adminRoleAssignmentsLabel = "Admin Role Assignments";
            if ( CollectionUtils.isNotEmpty( user.getAdminRoles() ) )
            {
                adminRoleAssignmentsLabel += ": " + user.getAdminRoles().get( 0 ) + " + " + ( user.getAdminRoles()
                    .size() - 1 );
            }
            if ( user.getAddress() != null )
            {
                addressAssignmentsLabel = "Address Assignments: ";
                if ( CollectionUtils.isNotEmpty( user.getAddress().getAddresses() ) )
                {
                    int ctr = 0;
                    for ( String street : user.getAddress().getAddresses() )
                    {
                        if ( ctr++ > 0 )
                        {
                            addressAssignmentsLabel += "," + street;
                        }
                        else
                        {
                            addressAssignmentsLabel += street;
                        }
                    }
                    isSet = true;
                }
                if ( StringUtils.isNotBlank( user.getAddress().getCity() ) )
                {
                    if ( isSet )
                    {
                        addressAssignmentsLabel += ",";
                    }

                    addressAssignmentsLabel += user.getAddress().getCity();
                    isSet = true;
                }
                if ( StringUtils.isNotEmpty( user.getAddress().getState() ) )
                {
                    if ( isSet )
                    {
                        addressAssignmentsLabel += ",";
                    }

                    addressAssignmentsLabel += user.getAddress().getState();
                    isSet = true;
                }
                if ( StringUtils.isNotEmpty( user.getAddress().getPostalCode() ) )
                {
                    if ( isSet )
                    {
                        addressAssignmentsLabel += " ";
                    }

                    addressAssignmentsLabel += user.getAddress().getPostalCode();
                }
            }
            String szName = user.getDisplayName();
            if ( !StringUtils.isNotEmpty( szName ) )
            {
                szName = user.getCn();
            }
            contactInformationLabel = "Contact Information for: " + szName;
        }


        private void initSelectionModels()
        {
            userRoleSelection = new UserRole();
            userAdminRoleSelection = new UserAdminRole();
            addressSelection = "";
            phonesSelection = "";
            mobilesSelection = "";
            emailsSelection = "";
            newUserRole = "";
            newUserAdminRole = "";
        }
    }


    private byte[] readJpegFile( String fileName )
    {
        URL fUrl = UserDetailPanel.class.getClassLoader().getResource( fileName );
        //File file = new File("./src/test/resources/p1.jpeg");
        //File file = new File("./org.apache.directory.fortress.web/panel/GenericAvatar.jpg");
        byte[] image = null;
        try
        {
            if ( fUrl != null )
            {
                image = org.apache.commons.io.FileUtils.readFileToByteArray( new File( fUrl.toURI() ) );
                //image = org.apache.commons.io.FileUtils.readFileToByteArray( file );
            }
        }
        catch ( URISyntaxException se )
        {
            String error = "readJpegFile caught URISyntaxException=" + se;
            log.error( error );
        }
        catch ( IOException ioe )
        {
            String error = "readJpegFile caught IOException=" + ioe;
            log.error( error );
        }
        return image;
    }

    /**
     * Strip and reutrn the value of an openldap pw policy attribute from its rdn name.
     *
     * @param rDn in the format of rdn name=value
     * @return value
     */
    private String getPolicyName(String rDn)
    {
        String name = null;
        if(StringUtils.isNotEmpty( rDn ))
        {
            int indx = rDn.indexOf( '=' );
            if( indx != -1 )
            {
                name = rDn.substring( indx + 1 );
            }
        }
        return name;
    }
}