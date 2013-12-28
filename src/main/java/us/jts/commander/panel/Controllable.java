/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/16/13
 */
public interface Controllable
{
    public enum Operations
    {
        ADD,
        UPDATE,
        DELETE,
        SEARCH,
        EXPORT,
        CANCEL
    }

    public void setOperation(Operations operations);
    public Operations getOperation();
}
