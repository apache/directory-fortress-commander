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


import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.core.model.Constraint;

import java.util.Date;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 5/24/13
 */
public class ConstraintRolePanel extends ConstraintBasePanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final String BEGIN_TIME_RC = "beginTimeRC";
    private static final String END_TIME_RC = "endTimeRC";
    private static final String BEGIN_DATE_RC = "beginDateRC";
    private static final String END_DATE_RC = "endDateRC";
    private static final String BEGIN_LOCK_DATE_RC = "beginLockDateRC";
    private static final String END_LOCK_DATE_RC = "endLockDateRC";


    /**
     * Constructor requires model to be passed in.
     *
     * @param id
     * @param constraint
     */
    public ConstraintRolePanel( String id, final IModel constraint )
    {
        super( id, constraint );
        beginTimeTP = new TimePicker( BEGIN_TIME_RC, new PropertyModel<Date>( this, BEGIN_TIME ) )
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
                    beginTime = renderTime( beginTime, constraint.getBeginTime() );
                }
            }
        };
        add( beginTimeTP );

        endTimeTP = new TimePicker( END_TIME_RC, new PropertyModel<Date>( this, END_TIME ) )
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
                    endTime = renderTime( endTime, constraint.getEndTime() );
                }
            }
        };
        add( endTimeTP );
        endTimeTP.setRequired( false );

        beginDateDP = new DatePicker( BEGIN_DATE_RC, new PropertyModel<Date>( this, BEGIN_DATE ) )
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
                    beginDate = renderDate( beginDate, constraint.getBeginDate() );
                }
            }
        };
        beginDateDP.setRequired( false );
        add( beginDateDP );

        endDateDP = new DatePicker( END_DATE_RC, new PropertyModel<Date>( this, END_DATE ) )
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
                    endDate = renderDate( endDate, constraint.getEndDate() );
                }
            }
        };
        endDateDP.setRequired( false );
        add( endDateDP );

        beginLockDateDP = new DatePicker( BEGIN_LOCK_DATE_RC, new PropertyModel<Date>( this, BEGIN_LOCK_DATE ) )
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
                    beginLockDate = renderDate( beginLockDate, constraint.getBeginLockDate() );
                }
            }
        };
        beginLockDateDP.setRequired( false );
        add( beginLockDateDP );

        endLockDateDP = new DatePicker( END_LOCK_DATE_RC, new PropertyModel<Date>( this, END_LOCK_DATE ) )
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
                    endLockDate = renderDate( endLockDate, constraint.getEndLockDate() );
                }
            }
        };
        endLockDateDP.setRequired( false );
        add( endLockDateDP );
    }
}