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


import com.googlecode.wicket.jquery.ui.form.spinner.Spinner;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.PwPolicyMgr;
import org.apache.directory.fortress.core.model.PwPolicy;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 6/12/13
 */
public class PwPolicyDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private PwPolicyMgr pwPolicyMgr;
    private static final Logger log = Logger.getLogger( PwPolicyDetailPanel.class.getName() );
    private Form editForm;
    private Displayable display;


    public Form getForm()
    {
        return this.editForm;
    }


    public PwPolicyDetailPanel( String id, Displayable display )
    {
        super( id );
        this.pwPolicyMgr.setAdmin( SecUtils.getSession( this ) );
        this.editForm = new PwPolicyDetailForm( GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<>(
            new PwPolicy() ) );
        this.display = display;
        add( editForm );
    }

    public class PwPolicyDetailForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private Component component;


        public PwPolicyDetailForm( String id, final IModel<PwPolicy> model )
        {
            super( id, model );

            add( new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.PWPOLICY_MGR, GlobalIds.ADD )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Add" );
                    PwPolicy policy = ( PwPolicy ) form.getModel().getObject();
                    try
                    {
                        policy.setCheckQuality( ( short ) 2 );
                        pwPolicyMgr.add( policy );
                        SaveModelEvent.send( getPage(), this, policy, target, SaveModelEvent.Operations.ADD );
                        component = editForm;
                        String msg = "Policy: " + policy.getName() + " has been added";
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
                    log.info( "PwPolicyPanel.add.onError caught" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.PWPOLICY_MGR, "update" )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    PwPolicy policy = ( PwPolicy ) form.getModel().getObject();
                    try
                    {
                        pwPolicyMgr.update( policy );
                        String msg = "Policy: " + policy.getName() + " has been updated";
                        SaveModelEvent.send( getPage(), this, policy, target, SaveModelEvent.Operations.UPDATE );
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
                    log.warn( "PwPolicyPanel.update.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.PWPOLICY_MGR, GlobalIds.DELETE )
            {
                /** Default serialVersionUID */
                private static final long serialVersionUID = 1L;


                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    PwPolicy role = ( PwPolicy ) form.getModel().getObject();

                    try
                    {
                        pwPolicyMgr.delete( role );
                        form.setModelObject( new PwPolicy() );
                        modelChanged();
                        String msg = "Role: " + role.getName() + " has been deleted";
                        SaveModelEvent.send( getPage(), this, role, target, SaveModelEvent.Operations.DELETE );
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
                    log.warn( "PwPolicyPanel.commit.onError" );
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
                    setModelObject( new PwPolicy() );
                    modelChanged();
                    component = editForm;
                    String msg = "Role cancelled input form";
                    display.setMessage( msg );
                }


                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "ControlPanel.cancel.onError" );
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

            add( new TextField( "name" ).setRequired( true ) );

            add( new TextField<Integer>( "minAge" ).add( new RangeValidator<>( 0, Integer.MAX_VALUE ) )
                .setRequired( true ) );

            add( new TextField<Long>( "maxAge" ).add( new RangeValidator<>( ( long ) 0, Long.MAX_VALUE ) )
                .setRequired( true ) );
            final Spinner<Integer> inHistorySP = new Spinner<>( "inHistory" );
            inHistorySP.setRequired( false );
            inHistorySP.add( new RangeValidator<>( ( short ) 0, ( short ) 100 ) );
            add( inHistorySP );

            final Spinner<Integer> minLengthSP = new Spinner<>( "minLength" );
            minLengthSP.setRequired( false );
            minLengthSP.add( new RangeValidator<>( ( short ) 0, ( short ) 100 ) );
            add( minLengthSP );

            add( new TextField<Long>( "expireWarning" ).add( new RangeValidator<>( ( long ) 0, Long.MAX_VALUE ) )
                .setRequired( true ) );
            final Spinner<Integer> graceLoginLimitSP = new Spinner<>( "graceLoginLimit" );
            graceLoginLimitSP.setRequired( false );
            graceLoginLimitSP.add( new RangeValidator<>( ( short ) 0, ( short ) 100 ) );
            add( graceLoginLimitSP );

            add( new CheckBox( "lockout" ).setRequired( true ) );
            add( new TextField<Integer>( "lockoutDuration" ).add( new RangeValidator<>( 0, Integer.MAX_VALUE ) )
                .setRequired( true ) );
            final Spinner<Integer> maxFailureSP = new Spinner<>( "maxFailure" );
            maxFailureSP.setRequired( false );
            maxFailureSP.add( new RangeValidator<>( ( short ) 0, ( short ) 100 ) );
            add( maxFailureSP );

            add( new TextField<Short>( "failureCountInterval" ).add(
                new RangeValidator<>( ( short ) 0, Short.MAX_VALUE ) ).setRequired( true ) );
            add( new CheckBox( "mustChange" ).setRequired( true ) );
            add( new CheckBox( "allowUserChange" ).setRequired( true ) );
            add( new CheckBox( "safeModify" ).setRequired( true ) );
            setOutputMarkupId( true );
        }


        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                PwPolicy policy = ( PwPolicy ) modelEvent.getEntity();
                this.setModelObject( policy );
                String msg = "Policy: " + policy.getName() + " has been selected";
                log.debug( ".onEvent SelectModelEvent: " + policy.getName() );
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
    }
}
