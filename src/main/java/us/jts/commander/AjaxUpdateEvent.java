/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;


import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class AjaxUpdateEvent
{

    private final AjaxRequestTarget target;

    public AjaxUpdateEvent(AjaxRequestTarget target)
    {
        this.target = target;
    }

    public AjaxRequestTarget getAjaxRequestTarget()
    {
        return target;
    }
}