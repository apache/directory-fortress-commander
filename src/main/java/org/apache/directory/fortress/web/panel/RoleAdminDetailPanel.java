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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.core.model.AdminRole;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.UserRole;

import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 *          Date: 8/12/13
 */
public class RoleAdminDetailPanel extends Panel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( RoleAdminDetailPanel.class.getName() );
    public static final String PERMOU_SELECTION = "permouSelection";
    public static final String USEROU_SELECTION = "userouSelection";
    public static final String ROLE_ASSIGN_MODAL = "role-assign-modal";
    private List<String> permous;
    private List<String> userous;
    private ComboBox<String> userouCB;
    private ComboBox<String> permouCB;
    private TextField beginRangeTF;
    private TextField endRangeTF;
    private String userouSelection;
    private String permouSelection;


    public RoleAdminDetailPanel( String id, final IModel roleModel )
    {
        super( id, roleModel );

        permouCB = new ComboBox<>( GlobalIds.OS_P, new PropertyModel<String>( this, PERMOU_SELECTION ), permous );
        permouCB.setOutputMarkupId( true );
        add( permouCB );
        addPermOUSearchModal();

        userouCB = new ComboBox<>( GlobalIds.OS_U, new PropertyModel<String>( this, USEROU_SELECTION ), userous );
        userouCB.setOutputMarkupId( true );
        add( userouCB );
        addUserOUSearchModal();

        beginRangeTF = new TextField( GlobalIds.BEGIN_RANGE );
        beginRangeTF.setRequired( false );
        beginRangeTF.setOutputMarkupId( true );
        add( beginRangeTF );
        addBeginRoleSearchModal();

        CheckBox beginInclusiveCB = new CheckBox( GlobalIds.BEGIN_INCLUSIVE );
        beginInclusiveCB.setRequired( false );
        add( beginInclusiveCB );

        endRangeTF = new TextField( GlobalIds.END_RANGE );
        endRangeTF.setRequired( false );
        endRangeTF.setOutputMarkupId( true );
        add( endRangeTF );
        addEndRoleSearchModal();

        CheckBox endInclusiveCB = new CheckBox( GlobalIds.END_INCLUSIVE );
        endInclusiveCB.setRequired( false );
        add( endInclusiveCB );

        setOutputMarkupId( true );
    }


    private void addPermOUSearchModal()
    {
        final ModalWindow permousModalWindow;
        add( permousModalWindow = new ModalWindow( "permoumodal" ) );
        final OUSearchModalPanel permouSearchModalPanel = new OUSearchModalPanel( permousModalWindow.getContentId(),
            permousModalWindow, false );
        permousModalWindow.setContent( permouSearchModalPanel );
        permousModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                OrgUnit ou = permouSearchModalPanel.getSelection();
                if ( ou != null )
                {
                    permouSelection = ou.getName();
                    AdminRole adminRole = ( AdminRole ) getDefaultModelObject();
                    adminRole.setOsP( permouSelection );
                    target.add( permouCB );
                }
            }
        } );

        add( new AjaxButton( GlobalIds.PERMOU_SEARCH )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                String msg = "clicked on permission OU search";
                msg += permouSelection != null ? ": " + permouSelection : "";
                permouSearchModalPanel.setSearchVal( permouSelection );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                permousModalWindow.show( target );
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

        permousModalWindow.setTitle( "Perm Organization Selection Modal" );
        permousModalWindow.setInitialWidth( 450 );
        permousModalWindow.setInitialHeight( 450 );
        permousModalWindow.setCookieName( "permou-modal" );

        add( new AjaxButton( "permou.delete" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                String msg = "clicked on permous.delete";
                if ( StringUtils.isNotEmpty( permouSelection ) )
                {
                    msg += " selection:" + permouSelection;
                    AdminRole adminRole = ( AdminRole ) form.getModel().getObject();
                    if ( adminRole.getOsPSet() != null )
                    {
                        adminRole.getOsPSet().remove( permouSelection );
                        permous.remove( permouSelection );
                        permouSelection = "";
                        target.add( permouCB );
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
                LOG.debug( msg );
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


    private void addUserOUSearchModal()
    {
        final ModalWindow userousModalWindow;
        add( userousModalWindow = new ModalWindow( "useroumodal" ) );
        final OUSearchModalPanel userouSearchModalPanel = new OUSearchModalPanel( userousModalWindow.getContentId(),
            userousModalWindow, true );
        userousModalWindow.setContent( userouSearchModalPanel );
        userousModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                OrgUnit ou = userouSearchModalPanel.getSelection();
                if ( ou != null )
                {
                    userouSelection = ou.getName();
                    AdminRole adminRole = ( AdminRole ) getDefaultModelObject();
                    adminRole.setOsU( userouSelection );
                    target.add( userouCB );
                }
            }
        } );

        add( new AjaxButton( GlobalIds.USEROU_SEARCH )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                String msg = "clicked on user OU search";
                msg += userouSelection != null ? ": " + userouSelection : "";
                userouSearchModalPanel.setSearchVal( userouSelection );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                userousModalWindow.show( target );
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

        userousModalWindow.setTitle( "User Organization Selection Modal" );
        userousModalWindow.setInitialWidth( 450 );
        userousModalWindow.setInitialHeight( 450 );
        userousModalWindow.setCookieName( "permou-modal" );

        add( new AjaxButton( "userou.delete" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                String msg = "clicked on userous.delete";
                if ( StringUtils.isNotEmpty( userouSelection ) )
                {
                    msg += " selection:" + userouSelection;
                    AdminRole adminRole = ( AdminRole ) form.getModel().getObject();
                    if ( adminRole.getOsUSet() != null )
                    {
                        adminRole.getOsUSet().remove( userouSelection );
                        userous.remove( userouSelection );
                        userouSelection = "";
                        target.add( userouCB );
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
                LOG.debug( msg );
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


    private void addBeginRoleSearchModal()
    {
        final ModalWindow beginRoleModalWindow;
        add( beginRoleModalWindow = new ModalWindow( "beginrolesmodal" ) );
        final RoleSearchModalPanel beginRoleSearchModalPanel = new RoleSearchModalPanel(
            beginRoleModalWindow.getContentId(), beginRoleModalWindow, false );
        beginRoleModalWindow.setContent( beginRoleSearchModalPanel );
        beginRoleModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                UserRole userRole = beginRoleSearchModalPanel.getRoleSelection();
                if ( userRole != null )
                {
                    AdminRole adminRole = ( AdminRole ) getDefaultModelObject();
                    adminRole.setBeginRange( userRole.getName() );
                    target.add( beginRangeTF );
                }
            }
        } );

        add( new AjaxButton( GlobalIds.BEGIN_RANGE_SEARCH )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                AdminRole adminRole = ( AdminRole ) form.getModel().getObject();
                beginRoleSearchModalPanel.setRoleSearchVal( adminRole.getBeginRange() );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                beginRoleModalWindow.show( target );
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

        beginRoleModalWindow.setTitle( "Begin Range Role Selection Modal" );
        beginRoleModalWindow.setInitialWidth( 700 );
        beginRoleModalWindow.setInitialHeight( 450 );
        beginRoleModalWindow.setCookieName( ROLE_ASSIGN_MODAL );
    }


    private void addEndRoleSearchModal()
    {
        final ModalWindow endRoleModalWindow;
        add( endRoleModalWindow = new ModalWindow( "endrolesmodal" ) );
        final RoleSearchModalPanel endRoleSearchModalPanel = new RoleSearchModalPanel(
            endRoleModalWindow.getContentId(), endRoleModalWindow, false );
        endRoleModalWindow.setContent( endRoleSearchModalPanel );
        endRoleModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                UserRole userRole = endRoleSearchModalPanel.getRoleSelection();
                if ( userRole != null )
                {
                    AdminRole adminRole = ( AdminRole ) getDefaultModelObject();
                    adminRole.setEndRange( userRole.getName() );
                    target.add( endRangeTF );
                }
            }
        } );

        add( new AjaxButton( GlobalIds.END_RANGE_SEARCH )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form )
            {
                AdminRole adminRole = ( AdminRole ) form.getModel().getObject();
                endRoleSearchModalPanel.setRoleSearchVal( adminRole.getBeginRange() );
                endRoleSearchModalPanel.setParentSearch( true );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                endRoleModalWindow.show( target );
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

        endRoleModalWindow.setTitle( "End Range Role Selection Modal" );
        endRoleModalWindow.setInitialWidth( 700 );
        endRoleModalWindow.setInitialHeight( 450 );
        endRoleModalWindow.setCookieName( ROLE_ASSIGN_MODAL );
    }


    /**
     * This api is needed for this class {@link org.apache.directory.fortress.web.panel.RoleDetailPanel} to 'push' its model value into this panel's combo box.
     *
     * @param permous
     */
    void setPermous( List<String> permous )
    {
        this.permous = permous;
        permouCB = new ComboBox<>( GlobalIds.OS_P, new PropertyModel<String>( this, PERMOU_SELECTION ), permous );
        permouSelection = "";
        addOrReplace( permouCB );
    }


    /**
     * This api is needed for this class {@link org.apache.directory.fortress.web.panel.RoleDetailPanel} to 'push' its model value into this panel's combo box.
     *
     * @param userous
     */
    void setUserous( List<String> userous )
    {
        this.userous = userous;
        userouCB = new ComboBox<>( GlobalIds.OS_U, new PropertyModel<String>( this, USEROU_SELECTION ), userous );
        userouSelection = "";
        addOrReplace( userouCB );
    }
}