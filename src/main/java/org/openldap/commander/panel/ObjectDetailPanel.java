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

import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.openldap.commander.GlobalIds;
import org.openldap.commander.GlobalUtils;
import org.openldap.commander.SaveModelEvent;
import org.openldap.commander.SecureIndicatingAjaxButton;
import org.openldap.commander.SelectModelEvent;
import org.openldap.fortress.AdminMgr;
import org.openldap.fortress.rbac.OrgUnit;
import org.openldap.fortress.rbac.PermObj;


/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 7/27/13
 */
public class ObjectDetailPanel extends FormComponentPanel
{
    @SpringBean
    private AdminMgr adminMgr;
    private static final Logger log = Logger.getLogger( ObjectDetailPanel.class.getName() );
    private Form editForm;
    private Displayable display;
    private boolean isAdmin;
    private String objName;

    public Form getForm()
    {
        return this.editForm;
    }

    public ObjectDetailPanel( String id, Displayable display, boolean isAdmin )
    {
        super( id );
        this.isAdmin = isAdmin;
        if(isAdmin)
            objName = GlobalIds.DEL_ADMIN_MGR;
        else
            objName = GlobalIds.ADMIN_MGR;

        this.adminMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.editForm = new ObjectDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<PermObj>( new PermObj() ) );
        this.display = display;
        add( editForm );
    }

    public class ObjectDetailForm extends Form
    {
        private Component component;
        private TextField ouTF;

        public ObjectDetailForm( String id, final IModel<PermObj> model )
        {
            super( id, model );
            add( new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.ADMIN_MGR, "addPermObj" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Add" );
                    PermObj permObj = ( PermObj ) form.getModel().getObject();
                    permObj.setAdmin( isAdmin );
                    try
                    {
                        adminMgr.addPermObj( permObj );
                        component = editForm;
                        SaveModelEvent.send( getPage(), this, permObj, target, SaveModelEvent.Operations.ADD );
                        String msg = "Perm objName: " + permObj.getObjName() + " has been added";
                        display.setMessage( msg );
                    }
                    catch ( org.openldap.fortress.SecurityException se )
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
                    log.info( "ObjectDetailPanel.add.onError caught" );
                    target.add();
                }
                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.ADMIN_MGR, "updatePermObj" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    PermObj permObj = ( PermObj ) form.getModel().getObject();
                    permObj.setAdmin( isAdmin );
                    try
                    {
                        adminMgr.updatePermObj( permObj );
                        String msg = "PermObject objName: " + permObj.getObjName() + " has been updated";
                        SaveModelEvent.send( getPage(), this, permObj, target, SaveModelEvent.Operations.UPDATE );
                        component = editForm;
                        display.setMessage( msg );
                    }
                    catch ( org.openldap.fortress.SecurityException se )
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
                    log.warn( "ObjectDetailPanel.commit.onError" );
                }
                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.ADMIN_MGR, "deletePermObj" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    PermObj permObj = ( PermObj ) form.getModel().getObject();
                    permObj.setAdmin( isAdmin );
                    try
                    {
                        adminMgr.deletePermObj( permObj );
                        form.setModelObject( new PermObj() );
                        modelChanged();
                        String msg = "PermObject objName: " + permObj.getObjName() + " has been deleted";
                        SaveModelEvent.send( getPage(), this, permObj, target, SaveModelEvent.Operations.DELETE );
                        component = editForm;
                        display.setMessage( msg );
                    }
                    catch ( org.openldap.fortress.SecurityException se )
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
                    log.warn( "ObjectDetailPanel.delete.onError" );
                }
                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
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
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    setModelObject( new PermObj() );
                    modelChanged();
                    String msg = "Perm cancelled input form";
                    component = editForm;
                    display.setMessage( msg );
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "ObjectDetailPanel.cancel.onError" );
                }
                @Override
                protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
                {
                    super.updateAjaxAttributes( attributes );
                    AjaxCallListener ajaxCallListener = new AjaxCallListener()
                    {
                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );

            if(isAdmin)
            {
                add( new Label( "objAssignmentsLabel", "Administrative Permission Object Detail" ) );
            }
            else
            {
                add( new Label( "objAssignmentsLabel", "RBAC Permission Object Detail" ) );
            }

            TextField objName = new TextField( "objName" );
            add( objName );
            objName.setRequired( false );
            TextField type = new TextField( "type" );
            add( type );
            TextField description = new TextField( "description" );
            description.setRequired( false );
            add( description );
            type.setRequired( false );
            Label internalId = new Label( "internalId" );
            add( internalId );
            ouTF = new TextField( "ou" );
            // making this required prevents the modals from opening:
            //ouTF.setRequired( true );
            ouTF.setOutputMarkupId( true );
            add( ouTF );
            addOUSearchModal();
        }


        private void addOUSearchModal()
        {
            final ModalWindow ousModalWindow;
            add( ousModalWindow = new ModalWindow( "ousmodal" ) );
            final OUSearchModalPanel ouSearchModalPanel = new OUSearchModalPanel( ousModalWindow.getContentId(),
                ousModalWindow, false );
            ousModalWindow.setContent( ouSearchModalPanel );
            ousModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    OrgUnit ou = ouSearchModalPanel.getSelection();
                    if ( ou != null )
                    {
                        PermObj permObj = ( PermObj ) editForm.getModel().getObject();
                        permObj.setOu( ou.getName() );
                        target.add( ouTF );
                    }
                }
            } );

            add( new AjaxButton( "ou.search" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on OrgUnits search";
                    PermObj permObj = ( PermObj ) editForm.getModel().getObject();
                    msg += permObj.getOu() != null ? ": " + permObj.getOu() : "";
                    ouSearchModalPanel.setSearchVal( permObj.getOu() );
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
                        @Override
                        public CharSequence getFailureHandler( Component component )
                        {
                            return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                        }
                    };
                    attributes.getAjaxCallListeners().add( ajaxCallListener );
                }
            } );

            ousModalWindow.setTitle( "Permission Organizational Unit Selection Modal" );
            ousModalWindow.setInitialWidth( 450 );
            ousModalWindow.setInitialHeight( 450 );
            ousModalWindow.setCookieName( "userou-modal" );
        }


        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                PermObj permObj = ( PermObj ) modelEvent.getEntity();
                this.setModelObject(permObj);
                String msg = "PermObject Name: " + permObj.getObjName() + " has been selected";
                log.debug( msg );
                component = editForm;
            }
            else if ( event.getPayload() instanceof AjaxRequestTarget )
            {
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
    }
}