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

import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.core.model.Constraint;

import java.util.Calendar;
import java.util.Date;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 5/24/13
 */
public class ConstraintBasePanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final String CLS_NM = ConstraintPanel.class.getName();
    private static final Logger log = Logger.getLogger( CLS_NM );

    protected static final String TIMEOUT = "timeout";
    protected static final String SUNDAY = "sunday";
    protected static final String MONDAY = "monday";
    protected static final String TUESDAY = "tuesday";
    protected static final String WEDNESDAY = "wednesday";
    protected static final String THURSDAY = "thursday";
    protected static final String FRIDAY = "friday";
    protected static final String SATURDAY = "saturday";

    protected static final String BEGIN_TIME = "beginTime";
    protected static final String END_TIME = "endTime";
    protected static final String BEGIN_DATE = "beginDate";
    protected static final String END_DATE = "endDate";
    protected static final String BEGIN_LOCK_DATE = "beginLockDate";
    protected static final String END_LOCK_DATE = "endLockDate";

    protected static final String DAY1 = "1";
    protected static final String DAY2 = "2";
    protected static final String DAY3 = "3";
    protected static final String DAY4 = "4";
    protected static final String DAY5 = "5";
    protected static final String DAY6 = "6";
    protected static final String DAY7 = "7";
    // These are used by this panel component's PropertyModel objects:

    protected Date beginTime;
    protected Date beginDate;
    protected Date endTime;
    protected Date endDate;
    protected Date beginLockDate;
    protected Date endLockDate;

    // These are the actual Wicket JQuery controls to process the input:
    protected TimePicker beginTimeTP;
    protected TimePicker endTimeTP;
    protected DatePicker beginDateDP;
    protected DatePicker endDateDP;
    protected DatePicker beginLockDateDP;
    protected DatePicker endLockDateDP;

    // The Wicket checkBoxes are used for constructing a {@link us.uts.fortress.rbac.User#dayMask} entity attribute into model model:
    protected CheckBox sundayCB;
    protected CheckBox mondayCB;
    protected CheckBox tuesdayCB;
    protected CheckBox wednesdayCB;
    protected CheckBox thursdayCB;
    protected CheckBox fridayCB;
    protected CheckBox saturdayCB;

    // These are used by CheckBox control to store the dayMask fields until mapped to {@link us.uts.fortress.rbac.User#dayMask} into model object:
    protected Boolean sunday = false;
    protected Boolean monday = false;
    protected Boolean tuesday = false;
    protected Boolean wednesday = false;
    protected Boolean thursday = false;
    protected Boolean friday = false;
    protected Boolean saturday = false;


    public ConstraintBasePanel( String id, final IModel constraint )
    {
        super( id, constraint );
        final Spinner<Integer> timeout = new Spinner<>( TIMEOUT );
        timeout.setRequired( false );
        add( timeout );

        // Add the dayMask's day of week CheckBoxes:
        sundayCB = new CheckBox( SUNDAY, new PropertyModel<Boolean>( this, SUNDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setSunday( isDayOfWeek( constraint, DAY1 ) );
                }
            }
        };
        add( sundayCB );
        mondayCB = new CheckBox( "monday", new PropertyModel<Boolean>( this, MONDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setMonday( isDayOfWeek( constraint, DAY2 ) );
                }
            }
        };
        add( mondayCB );
        tuesdayCB = new CheckBox( TUESDAY, new PropertyModel<Boolean>( this, TUESDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setTuesday( isDayOfWeek( constraint, DAY3 ) );
                }
            }
        };
        add( tuesdayCB );
        wednesdayCB = new CheckBox( WEDNESDAY, new PropertyModel<Boolean>( this, WEDNESDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setWednesday( isDayOfWeek( constraint, DAY4 ) );
                }
            }
        };
        add( wednesdayCB );
        thursdayCB = new CheckBox( THURSDAY, new PropertyModel<Boolean>( this, THURSDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setThursday( isDayOfWeek( constraint, DAY5 ) );
                }
            }
        };
        add( thursdayCB );
        fridayCB = new CheckBox( FRIDAY, new PropertyModel<Boolean>( this, FRIDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setFriday( isDayOfWeek( constraint, DAY6 ) );
                }
            }
        };
        add( fridayCB );
        saturdayCB = new CheckBox( SATURDAY, new PropertyModel<Boolean>( this, SATURDAY ) )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onBeforeRender()
            {
                if ( this.getParent().getDefaultModelObject() != null )
                {
                    super.onBeforeRender();
                    Constraint constraint = ( Constraint ) this.getParent().getDefaultModelObject();
                    setSaturday( isDayOfWeek( constraint, DAY7 ) );
                }
            }
        };
        add( saturdayCB );
    }


    /**
     * This method is used to convert from the panel component model to the domain model:
     */
    @Override
    public void convertInput()
    {
        Constraint constraint = ( Constraint ) getDefaultModelObject();
        if ( constraint != null )
        {
            constraint.setBeginTime( convertTime( beginTimeTP ) );
            constraint.setEndTime( convertTime( endTimeTP ) );
            constraint.setBeginDate( convertDate( beginDateDP ) );
            constraint.setEndDate( convertDate( endDateDP ) );
            constraint.setBeginLockDate( convertDate( beginLockDateDP ) );
            constraint.setEndLockDate( convertDate( endLockDateDP ) );

            setSunday( sundayCB.getConvertedInput() );
            setMonday( mondayCB.getConvertedInput() );
            setTuesday( tuesdayCB.getConvertedInput() );
            setWednesday( wednesdayCB.getConvertedInput() );
            setThursday( thursdayCB.getConvertedInput() );
            setFriday( fridayCB.getConvertedInput() );
            setSaturday( saturdayCB.getConvertedInput() );

            String szDayMask = "";
            if ( sunday )
                szDayMask += DAY1;
            if ( monday )
                szDayMask += DAY2;
            if ( tuesday )
                szDayMask += DAY3;
            if ( wednesday )
                szDayMask += DAY4;
            if ( thursday )
                szDayMask += DAY5;
            if ( friday )
                szDayMask += DAY6;
            if ( saturday )
                szDayMask += DAY7;

            constraint.setDayMask( szDayMask );
            setConvertedInput( constraint );
        }
        else
        {
            log.warn( "constraint was null" );
        }
    }


    protected boolean isDayOfWeek( Constraint constraint, String szDay )
    {
        boolean isSet = false;
        if ( constraint != null && constraint.getDayMask() != null
            && ( constraint.getDayMask().contains( szDay ) || constraint.getDayMask().equals( "all" ) ) )
        {
            isSet = true;
        }
        return isSet;
    }


    protected String convertTime( TimePicker time )
    {
        String szTime = null;
        if ( time != null )
        {
            Date localDate = time.getConvertedInput();
            if ( localDate != null )
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime( localDate );
                log.debug( "localDate=" + localDate.toString() );
                if ( calendar.get( Calendar.HOUR_OF_DAY ) < 10 )
                    szTime = "0" + calendar.get( Calendar.HOUR_OF_DAY );
                else
                    szTime = "" + calendar.get( Calendar.HOUR_OF_DAY );
                if ( calendar.get( Calendar.MINUTE ) < 10 )
                    szTime += "0" + calendar.get( Calendar.MINUTE );
                else
                    szTime += "" + calendar.get( Calendar.MINUTE );
            }
        }
        return szTime;
    }


    protected String convertDate( DatePicker date )
    {
        String szDate = null;
        if ( date != null )
        {
            Date localDate = date.getConvertedInput();
            if ( localDate != null )
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime( localDate );
                log.debug( "localDate=" + localDate.toString() );
                szDate = "" + calendar.get( Calendar.YEAR );

                if ( ( calendar.get( Calendar.MONTH ) + 1 ) < 10 )
                    szDate += "0" + ( calendar.get( Calendar.MONTH ) + 1 );
                else
                    szDate += "" + ( calendar.get( Calendar.MONTH ) + 1 );
                if ( calendar.get( Calendar.DAY_OF_MONTH ) < 10 )
                    szDate += "0" + calendar.get( Calendar.DAY_OF_MONTH );
                else
                    szDate += "" + calendar.get( Calendar.DAY_OF_MONTH );
            }
        }
        return szDate;
    }


    protected Date renderTime( Date date, String szTime )
    {
        if ( szTime != null )
        {
            Calendar calendar = Calendar.getInstance();
            try
            {
                int hours = Integer.valueOf( szTime.substring( 0, 2 ) );
                int minutes = Integer.valueOf( szTime.substring( 2, 4 ) );
                // zero hours convert to 24 for calendar:
                if(hours == 0)
                {
                    hours = 24;
                }
                calendar.set( 0, 0, 0, hours, minutes );
                date = calendar.getTime();
            }
            catch ( StringIndexOutOfBoundsException e )
            {
                String warning = CLS_NM + ".renderTime bad time: " + szTime;
                log.warn( warning );
                //warn(warning);
            }
        }
        else
        {
            date = null;
        }
        return date;
    }


    protected Date renderDate( Date date, String szDate )
    {
        if ( szDate != null && !szDate.equalsIgnoreCase( "none" ) )
        {
            Calendar calendar = Calendar.getInstance();
            try
            {
                int years = Integer.valueOf( szDate.substring( 0, 4 ) );
                int months = Integer.valueOf( szDate.substring( 4, 6 ) );
                // Convert months because the Calendar uses 0 - 11:
                months = months - 1;
                int days = Integer.valueOf( szDate.substring( 6, 8 ) );
                calendar.set( years, months, days, 0, 0 );
                date = calendar.getTime();
            }
            catch ( StringIndexOutOfBoundsException e )
            {
                String warning = CLS_NM + ".renderDate bad date: " + szDate;
                log.warn( warning );
                //warn(warning);
            }
        }
        else
        {
            date = null;
        }
        return date;
    }


    protected Boolean getSunday()
    {
        return sunday;
    }


    protected void setSunday( Boolean sunday )
    {
        this.sunday = sunday;
    }


    protected Boolean getMonday()
    {
        return monday;
    }


    protected void setMonday( Boolean monday )
    {
        this.monday = monday;
    }


    protected Boolean getTuesday()
    {
        return tuesday;
    }


    protected void setTuesday( Boolean tuesday )
    {
        this.tuesday = tuesday;
    }


    protected Boolean getWednesday()
    {
        return wednesday;
    }


    protected void setWednesday( Boolean wednesday )
    {
        this.wednesday = wednesday;
    }


    protected Boolean getThursday()
    {
        return thursday;
    }


    protected void setThursday( Boolean thursday )
    {
        this.thursday = thursday;
    }


    protected Boolean getFriday()
    {
        return friday;
    }


    protected void setFriday( Boolean friday )
    {
        this.friday = friday;
    }


    protected Boolean getSaturday()
    {
        return saturday;
    }


    protected void setSaturday( Boolean saturday )
    {
        this.saturday = saturday;
    }
}