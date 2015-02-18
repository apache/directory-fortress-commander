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


import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.basic.Label;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 5/20/13
 */
public class InfoPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final String CLS_NM = InfoPanel.class.getName();
    private static final Logger log = Logger.getLogger( CLS_NM );
    private Form infoForm;


    public Displayable getDisplay()
    {
        return ( Displayable ) this.infoForm;
    }


    public InfoPanel( String id )
    {
        super( id );
        init( "" );
    }


    public InfoPanel( String id, String msg )
    {
        super( id );
        init( msg );
    }


    private void init( String msg )
    {
        this.infoForm = new InfoForm( "infoFields", msg );
        this.infoForm.setOutputMarkupId( true );
        add( infoForm );
    }

    public class InfoForm extends Form implements Displayable
    {
        /** Default serialVersionUID */
        private static final long serialVersionUID = 1L;
        private Label infoTextField;
        private FeedbackPanel feedbackPanel;
        private String infoField;


        @Override
        public void onError()
        {
            log.debug( "InfoPanel.onError" );
        }


        @Override
        public void onSubmit()
        {
            log.debug( "InfoPanel.onSubmit message: " + infoField );
        }


        public InfoForm( String id, final String message )
        {
            super( id );
            setModel( new PropertyModel<String>( this, "infoField" ) );
            infoField = message;
            infoTextField = new Label( "infoField", new PropertyModel( this, "infoField" ) );
            add( infoTextField );
            infoTextField.setOutputMarkupId( true );
            feedbackPanel = new FeedbackPanel( "feedback" );
            feedbackPanel.setOutputMarkupId( true );
            add( feedbackPanel );
        }


        @Override
        public void setMessage( String message )
        {
            infoField = message;
        }


        @Override
        public void display()
        {
            log.debug( ".display message (no AJAX): " + infoField );
            add( infoTextField );
            add( feedbackPanel );
        }


        @Override
        public void display( AjaxRequestTarget target )
        {
            log.debug( ".display message (AJAX): " + infoField );
            target.add( infoTextField );
            target.add( feedbackPanel );
        }
    }
}