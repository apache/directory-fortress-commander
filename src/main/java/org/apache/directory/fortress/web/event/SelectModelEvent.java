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
package org.apache.directory.fortress.web.event;

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
import org.apache.directory.fortress.core.model.FortEntity;

import java.util.Collection;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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