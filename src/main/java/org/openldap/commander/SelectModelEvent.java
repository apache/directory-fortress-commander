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

package org.openldap.commander;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.ILogData;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openldap.fortress.rbac.FortEntity;

import java.util.Collection;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class SelectModelEvent extends AjaxUpdateEvent
{
    private int index = 0;
    private FortEntity entity;

    public SelectModelEvent(AjaxRequestTarget target)
    {
        super(target);
    }

    public SelectModelEvent(AjaxRequestTarget target, int index)
    {
        super(target);
        this.index = index;
    }

    public SelectModelEvent(AjaxRequestTarget target, FortEntity entity)
    {
        super(target);
        this.entity = entity;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public FortEntity getEntity()
    {
        return entity;
    }

    public void setEntity(FortEntity entity)
    {
        this.entity = entity;
    }

    public static void send(Page page, Component component, FortEntity entity)
    {
        AjaxRequestTarget target = new AjaxRequestTarget()
        {
            @Override
            public void add(Component component, String markupId)
            {
                //To change body of implemented methods use File | Settings | File Templates.
                //component.
            }
            @Override
            public void add(Component... components)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void addChildren(MarkupContainer parent, Class<?> childCriteria)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void addListener(IListener listener)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void appendJavaScript(CharSequence javascript)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void prependJavaScript(CharSequence javascript)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void registerRespondListener(ITargetRespondListener listener)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public Collection<? extends Component> getComponents()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void focusComponent(Component component)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public IHeaderResponse getHeaderResponse()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public String getLastFocusedElementId()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public Page getPage()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public ILogData getLogData()

            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public Integer getPageId()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public boolean isPageInstanceCreated()
            {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public Integer getRenderCount()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public Class<? extends IRequestablePage> getPageClass()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public PageParameters getPageParameters()
            {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void respond(IRequestCycle iRequestCycle)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
            @Override
            public void detach(IRequestCycle iRequestCycle)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        component.send(page, Broadcast.BREADTH, new SelectModelEvent(target, entity));
    }

    public static void send(Page page, Component component, FortEntity entity, AjaxRequestTarget target)
    {
        component.send(page, Broadcast.BREADTH, new SaveModelEvent(target, entity));
    }
}