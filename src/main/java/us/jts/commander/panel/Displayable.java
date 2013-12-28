/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/20/13
 */
public interface Displayable
{
    public void setMessage(String message);
    public void display(AjaxRequestTarget target);
    public void display();
}
