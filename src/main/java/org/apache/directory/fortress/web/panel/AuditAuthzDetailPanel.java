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

import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.model.AuthZ;
import org.apache.directory.fortress.core.model.User;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 8/6/13
 */
public class AuditAuthzDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger( AuditAuthzDetailPanel.class.getName() );
    private Form detailForm;
    private Displayable display;
    private UserAuditDetailPanel userPanel;


    public Form getForm()
    {
        return this.detailForm;
    }


    public AuditAuthzDetailPanel( String id, Displayable display )
    {
        super( id );
        this.auditMgr.setAdmin( SecUtils.getSession( this ) );
        this.reviewMgr.setAdmin( SecUtils.getSession( this ) );
        this.detailForm = new AuditAuthzDetailForm( GlobalIds.DETAIL_FIELDS, new CompoundPropertyModel<>(
            new AuthZ() ) );
        this.display = display;
        add( detailForm );
    }

    public class AuditAuthzDetailForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private Component component;


        public AuditAuthzDetailForm( String id, final IModel<AuthZ> model )
        {
            super( id, model );
            add( new Label( GlobalIds.REQ_RESULT ) );
            add( new Label( GlobalIds.REQ_START ) );
            add( new Label( GlobalIds.REQ_ATTR ) );
            add( new Label( GlobalIds.REQ_ATTRS_ONLY ) );
            add( new Label( GlobalIds.REQ_DEREF_ALIASES ) );
            userPanel = new UserAuditDetailPanel( GlobalIds.USERAUDITDETAILPANEL, new CompoundPropertyModel<>(
                new User() ) );
            add( userPanel );
            setOutputMarkupId( true );
        }


        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                AuthZ authZ = ( AuthZ ) modelEvent.getEntity();
                this.setModelObject( authZ );
                String msg = "AuthZ: " + authZ.getReqAuthzID() + " has been selected";
                LOG.debug( ".onEvent SelectModelEvent: " + authZ.getReqAuthzID() );
                AuditUtils.getAuthZPerm( authZ.getReqDN() );
                display.setMessage( msg );
                component = detailForm;

            }
            else if ( event.getPayload() instanceof AjaxRequestTarget )
            {
                // only add the form to ajax target if something has changed...
                if ( component != null )
                {
                    AjaxRequestTarget target = ( ( AjaxRequestTarget ) event.getPayload() );
                    LOG.debug( ".onEvent AjaxRequestTarget: " + target.toString() );
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
                User user = null;
                // necessary to push the 'changed' model down into the aggregated panel:
                AuthZ authZ = ( AuthZ ) detailForm.getModelObject();
                if ( StringUtils.isNotBlank( authZ.getReqAuthzID() ) )
                {
                    user = AuditUtils.getUser( reviewMgr, authZ.getReqAuthzID() );
                }
                if ( user == null )
                {
                    user = new User();
                }
                IModel<User> userModel = new CompoundPropertyModel<>( user );
                userPanel.setDefaultModel( userModel );
            }
            else
            {
                LOG.info( ".onBeforeRender null model object" );
            }
            super.onBeforeRender();
        }
    }
}
