/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import com.googlecode.wicket.jquery.ui.kendo.datetime.DatePicker;
import com.googlecode.wicket.jquery.ui.kendo.datetime.TimePicker;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import us.jts.commander.GlobalIds;
import us.jts.fortress.util.time.Constraint;

import java.util.Date;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/24/13
 */
public class ConstraintAdminRolePanel extends ConstraintBasePanel
{
    private static final String BEGIN_TIME_RC = GlobalIds.BEGIN_TIME_ARC;
    private static final String END_TIME_RC = GlobalIds.END_TIME_ARC;
    private static final String BEGIN_DATE_RC = GlobalIds.BEGIN_DATE_ARC;
    private static final String END_DATE_RC = GlobalIds.END_DATE_ARC;
    private static final String BEGIN_LOCK_DATE_RC = GlobalIds.BEGIN_LOCK_DATE_ARC;
    private static final String END_LOCK_DATE_RC = GlobalIds.END_LOCK_DATE_ARC;

    /**
     * Constructor requires model to be passed in.
     *
     * @param id
     * @param constraint
     */
    public ConstraintAdminRolePanel( String id, final IModel constraint )
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