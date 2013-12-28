/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.basic.Label;


/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/20/13
 */
public class InfoPanel extends FormComponentPanel
{
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
        init( "ready to accept input" );
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