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


import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.datatable.DataTable;
import com.googlecode.wicket.kendo.ui.datatable.column.IColumn;
import com.googlecode.wicket.kendo.ui.datatable.column.PropertyColumn;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.AuditMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Mod;
import org.apache.directory.fortress.core.model.User;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 *          Date: 8/6/13
 */
public class AuditModDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    public static final int MOD_OFFSET = 3;
    public static final int ROWS = 5;
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger( AuditModDetailPanel.class.getName() );
    private Form detailForm;
    private Displayable display;
    private UserAuditDetailPanel userPanel;


    public Form getForm()
    {
        return this.detailForm;
    }


    public AuditModDetailPanel( String id, Displayable display )
    {
        super( id );
        this.auditMgr.setAdmin( SecUtils.getSession( this ) );
        this.reviewMgr.setAdmin( SecUtils.getSession( this ) );
        this.detailForm = new AuditAuthzDetailForm( GlobalIds.DETAIL_FIELDS, new CompoundPropertyModel<>( new Mod() ) );
        this.display = display;
        add( detailForm );
    }

    public class AuditAuthzDetailForm extends Form
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private Component component;
        private String ftModifier;
        private String ftModCode;
        private String ftModId;
        private String userId;
        private byte[] modPhoto;
        private DataTable<RequestMod> table;
        private List<IColumn> columns;
        private Options options;


        public AuditAuthzDetailForm( String id, final IModel<Mod> model )
        {
            super( id, model );
            add( new Label( GlobalIds.FT_MOD_ID, new PropertyModel<String>( this, GlobalIds.FT_MOD_ID ) ) );
            add( new Label( GlobalIds.FT_MOD_CODE, new PropertyModel<String>( this, GlobalIds.FT_MOD_CODE ) ) );
            // DataTable //
            columns = newColumnList();
            options = new Options();
            options.set( "height", 240 );
            options.set( "pageable", "{ pageSizes: [ 5, 10, 15, 20 ] }" );
            //table2 = new DataTable("modstable", columns, createDataProvider( null ), ROWS, options);

            table = new DataTable<>( "modstable", columns, createDataProvider( null ), ROWS, options );
            table.setOutputMarkupId( true );
            add( table );
            add( new Label( "reqType" ) );
            add( new Label( GlobalIds.REQ_DN ) );
            add( new Label( GlobalIds.REQ_START ) );
            userPanel = new UserAuditDetailPanel( GlobalIds.USERAUDITDETAILPANEL, new CompoundPropertyModel<>(
                new User()
                ) );
            add( userPanel );
            setOutputMarkupId( true );
        }


        @Override
        public void onEvent( final IEvent<?> event )
        {
            if ( event.getPayload() instanceof SelectModelEvent )
            {
                SelectModelEvent modelEvent = ( SelectModelEvent ) event.getPayload();
                Mod mod = ( Mod ) modelEvent.getEntity();
                this.setModelObject( mod );
                String msg = "Mod: " + mod.getReqAuthzID() + " has been selected";
                LOG.debug( ".onEvent SelectModelEvent: " + mod.getReqAuthzID() );
                List<RequestMod> modifications = parseRequestMods( mod.getReqMod() );
                table = new DataTable<>( "modstable", columns, createDataProvider( modifications ), ROWS,
                    options );
                User user = null;
                // necessary to push the 'changed' model down into the aggregated panel:
                int indx = modifications.indexOf( new RequestMod( GlobalIds.FT_MODIFIER ) );
                if ( indx != -1 )
                {
                    ftModifier = modifications.get( indx ).getValue();
                    if ( StringUtils.isNotEmpty( ftModifier ) )
                    {
                        user = AuditUtils.getUserByInternalId( reviewMgr, ftModifier );
                        userId = user.getUserId();
                    }
                }
                indx = modifications.indexOf( new RequestMod( GlobalIds.FT_MOD_CODE ) );
                if ( indx != -1 )
                {
                    ftModCode = modifications.get( indx ).getValue();
                }
                indx = modifications.indexOf( new RequestMod( GlobalIds.FT_MOD_ID ) );
                if ( indx != -1 )
                {
                    ftModId = modifications.get( indx ).getValue();
                }

                if ( user == null )
                {
                    user = new User();
                }
                IModel<User> userModel = new CompoundPropertyModel<>( user );
                userPanel.setDefaultModel( userModel );

                addOrReplace( table );
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


        private List<RequestMod> parseRequestMods( List<String> mods )
        {
            List<RequestMod> results = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( mods ) )
            {
                Mod mod = ( Mod ) detailForm.getModelObject();
                if ( mod != null && CollectionUtils.isNotEmpty( mod.getReqMod() ) )
                {
                    int ctr = 1;
                    for ( String szMod : mod.getReqMod() )
                    {
                        int indx = szMod.indexOf( ':' );
                        if ( indx != -1 )
                        {
                            String szName = szMod.substring( 0, indx );
                            String szValue = "";
                            // ensure value not blank:
                            if ( szMod.length() > indx + MOD_OFFSET && !szName.equalsIgnoreCase( GlobalIds.JPEGPHOTO ) )
                            {
                                szValue = szMod.substring( indx + MOD_OFFSET );
                            }
                            RequestMod requestMod = new RequestMod( ctr++, szName, szValue );
                            char type = szMod.charAt( indx + 1 );
                            if ( type == '=' )
                            {
                                requestMod.setType( RequestMod.TYPE.UPDATE );
                            }
                            else if ( type == '+' )
                            {
                                requestMod.setType( RequestMod.TYPE.ADD );
                            }
                            else if ( type == '-' )
                            {
                                requestMod.setType( RequestMod.TYPE.DELETE );
                            }
                            else
                            {
                                requestMod.setType( RequestMod.TYPE.UNKNOWN );
                            }
                            results.add( requestMod );
                        }
                    }
                }
            }
            return results;
        }
    }


    private IDataProvider<RequestMod> createDataProvider( List<RequestMod> mods )
    {
        ListDataProvider<RequestMod> results;
        if ( CollectionUtils.isNotEmpty( mods ) )
        {
            results = new ListDataProvider<>( mods );
        }
        else
        {
            results = new ListDataProvider<>( new ArrayList<RequestMod>() );
        }
        return results;
    }


    private List<IColumn> newColumnList()
    {
        List<IColumn> columns = new ArrayList<>();
        columns.add( new PropertyColumn( "#", "index", 30 ) );
        columns.add( new PropertyColumn( "Op", "type", 50 ) );
        columns.add( new PropertyColumn( "Name", "name", 80 ) );
        columns.add( new PropertyColumn( "Value", "value", 200 ) );
        return columns;
    }
}
