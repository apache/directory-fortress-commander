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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.model.OrgUnit;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 7/27/13
 */
public class OUDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private DelAdminMgr delAdminMgr;
    private static final Logger log = Logger.getLogger( OUDetailPanel.class.getName() );
    private Form editForm;
    private Displayable display;
    private boolean isUser;


    public Form getForm()
    {
        return this.editForm;
    }


    public OUDetailPanel( String id, Displayable display, boolean isUser )
    {
        super( id );
        this.delAdminMgr.setAdmin( SecUtils.getSession( this ) );
        this.isUser = isUser;
        OrgUnit ou = new OrgUnit();
        if ( isUser )
            ou.setType( OrgUnit.Type.USER );
        else
            ou.setType( OrgUnit.Type.PERM );
        this.editForm = new OUDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<>( ou ) );
        this.display = display;
        add( editForm );
    }

    public class OUDetailForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private static final String PARENTS = "parents";
        private static final String PARENTS_SELECTION = "parentsSelection";
        private String internalId;
        private ComboBox<String> parentsCB;
        private String parentsSelection;
        private Component component;
        private List<String> parents = new ArrayList<>();
        private OrgUnit parent = new OrgUnit();
        private TextField nameTF;
        private SecureIndicatingAjaxButton addPB;


        public OUDetailForm( String id, final IModel<OrgUnit> model )
        {
            super( id, model );

            if ( isUser )
                parent.setType( OrgUnit.Type.USER );
            else
                parent.setType( OrgUnit.Type.PERM );

            add( addPB = new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.DEL_ADMIN_MGR, "addOU" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Add" );
                    OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                    updateEntityWithComboData( orgUnit );
                    try
                    {
                        delAdminMgr.add( orgUnit );
                        SaveModelEvent.send( getPage(), this, orgUnit, target, SaveModelEvent.Operations.ADD );
                        component = editForm;
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been added";
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
                    log.info( "OUDetailPanel.add.onError caught" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.DEL_ADMIN_MGR, "updateOU" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                    try
                    {
                        updateEntityWithComboData( orgUnit );
                        delAdminMgr.update( orgUnit );
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been updated";
                        SaveModelEvent.send( getPage(), this, orgUnit, target, SaveModelEvent.Operations.UPDATE );
                        component = editForm;
                        display.setMessage( msg );
                        parentsSelection = "";
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
                    log.warn( "OUDetailPanel.update.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.DEL_ADMIN_MGR, "deleteOU" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit delete" );
                    OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                    try
                    {
                        delAdminMgr.delete( orgUnit );
                        clearDetailFields();
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been deleted";
                        component = editForm;
                        SaveModelEvent.send( getPage(), this, orgUnit, target, SaveModelEvent.Operations.DELETE );
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
                    log.warn( "OUDetailPanel.delete.onError" );
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
                    String msg = "OU Detail cancelled input form";
                    display.setMessage( msg );
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "OUDetailPanel.cancel.onError" );
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

            if ( isUser )
            {
                add( new Label( "ouAssignmentsLabel", "User Organizational Detail" ) );
            }
            else
            {
                add( new Label( "ouAssignmentsLabel", "Permission Organizational Detail" ) );
            }

            nameTF = new TextField( "name" );
            add( nameTF );
            TextField description = new TextField( "description" );
            description.setRequired( false );
            add( description );
            Label iid = new Label( "id" );
            add( iid );
            parentsCB = new ComboBox<>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ), parents );
            add( parentsCB );
            setOutputMarkupId( true );
            addParentSearchModal();

            add( new AjaxButton( "parents.delete" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on members.delete";
                    if ( StringUtils.isNotEmpty( parentsSelection ) )
                    {
                        msg += " selection:" + parentsSelection;
                        OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                        if ( orgUnit.getParents() != null )
                        {
                            orgUnit.getParents().remove( parentsSelection );
                            parents.remove( parentsSelection );
                            parentsSelection = "";
                            parents = new ArrayList<>();
                            parentsCB = new ComboBox<>( PARENTS, new PropertyModel<String>( this,
                                PARENTS_SELECTION ), parents );
                            component = editForm;
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because org unit does not have parent set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because parents selection is empty";
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
        }


        private void addParentSearchModal()
        {
            final ModalWindow parentsModalWindow;
            add( parentsModalWindow = new ModalWindow( "ouparentsmodal" ) );
            final OUSearchModalPanel parentSearchModalPanel = new OUSearchModalPanel(
                parentsModalWindow.getContentId(), parentsModalWindow, isUser );
            parentsModalWindow.setContent( parentSearchModalPanel );
            parentsModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    parent = parentSearchModalPanel.getSelection();
                    if ( parent != null )
                    {
                        parentsSelection = parent.getName();
                        component = editForm;
                    }
                }
            } );

            add( new AjaxButton( "parents.search" )
            {
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on parents search";
                    msg += parentsSelection != null ? ": " + parentsSelection : "";
                    parentSearchModalPanel.setSearchVal( parentsSelection );
                    display.setMessage( msg );
                    log.debug( msg );
                    target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                    parentsModalWindow.show( target );
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

            parentsModalWindow.setTitle( "OU Parent Selection Modal" );
            parentsModalWindow.setInitialWidth( 550 );
            parentsModalWindow.setInitialHeight( 450 );
            parentsModalWindow.setCookieName( "parent-assign-modal" );
        }


        private void updateEntityWithComboData( OrgUnit orgUnit )
        {
            if ( StringUtils.isNotEmpty( parentsSelection ) )
            {
                orgUnit.setParent( parentsSelection );
                parents.add( parentsSelection );
            }
        }


        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                OrgUnit orgUnit = ( OrgUnit ) modelEvent.getEntity();
                this.setModelObject( orgUnit );
                if ( CollectionUtils.isNotEmpty( orgUnit.getParents() ) )
                {
                    parents = new ArrayList<>( orgUnit.getParents() );
                    parentsCB = new ComboBox<>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),
                        parents );
                }
                else
                {
                    parents = new ArrayList<>();
                    parentsCB = new ComboBox<>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),
                        parents );
                }
                nameTF.setEnabled( false );
                addPB.setEnabled( false );
                editForm.addOrReplace( parentsCB );
                String msg = "OrgUnit: " + orgUnit.getName() + " has been selected";
                log.debug( ".onEvent SelectModelEvent: " + orgUnit.getName() );
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

        private void clearDetailFields()
        {
            OrgUnit ou = new OrgUnit();
            if ( isUser )
                ou.setType( OrgUnit.Type.USER );
            else
                ou.setType( OrgUnit.Type.PERM );

            setModelObject( ou );
            parentsSelection = "";
            parents = new ArrayList<>();
            parentsCB = new ComboBox<>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),
                parents );
            nameTF.setEnabled( true );
            addPB.setEnabled( true );
            editForm.addOrReplace( parentsCB );
        }
    }
}
