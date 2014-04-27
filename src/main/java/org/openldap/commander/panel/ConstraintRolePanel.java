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

import com.googlecode.wicket.kendo.ui.form.datetime.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.openldap.fortress.util.time.Constraint;

import java.util.Date;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/24/13
 */
public class ConstraintRolePanel extends ConstraintBasePanel
{
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
        super(id, constraint);
        beginTimeTP = new TimePicker( BEGIN_TIME_RC, new PropertyModel<Date>(this, BEGIN_TIME ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    beginTime = renderTime(beginTime, constraint.getBeginTime());
                }
            }
        };
        add(beginTimeTP);

        endTimeTP = new TimePicker( END_TIME_RC, new PropertyModel<Date>(this, END_TIME ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    endTime = renderTime(endTime, constraint.getEndTime());
                }
            }
        };
        add(endTimeTP);
        endTimeTP.setRequired(false);

        beginDateDP = new DatePicker( BEGIN_DATE_RC, new PropertyModel<Date>(this, BEGIN_DATE ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    beginDate = renderDate(beginDate, constraint.getBeginDate());
                }
            }
        };
        beginDateDP.setRequired(false);
        add(beginDateDP);

        endDateDP = new DatePicker( END_DATE_RC, new PropertyModel<Date>(this, END_DATE ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    endDate = renderDate(endDate, constraint.getEndDate());
                }
            }
        };
        endDateDP.setRequired(false);
        add(endDateDP);

        beginLockDateDP = new DatePicker( BEGIN_LOCK_DATE_RC, new PropertyModel<Date>(this, BEGIN_LOCK_DATE ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    beginLockDate = renderDate(beginLockDate, constraint.getBeginLockDate());
                }
            }
        };
        beginLockDateDP.setRequired(false);
        add(beginLockDateDP);

        endLockDateDP = new DatePicker( END_LOCK_DATE_RC, new PropertyModel<Date>(this, END_LOCK_DATE ))
        {
            @Override
            protected void onBeforeRender()
            {
                if (this.getParent().getDefaultModelObject() != null)
                {
                    super.onBeforeRender();
                    Constraint constraint = (Constraint) this.getParent().getDefaultModelObject();
                    endLockDate = renderDate(endLockDate, constraint.getEndLockDate());
                }
            }
        };
        endLockDateDP.setRequired(false);
        add(endLockDateDP);
    }
}