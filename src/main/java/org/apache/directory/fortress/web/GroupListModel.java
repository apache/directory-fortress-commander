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
package org.apache.directory.fortress.web;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.ldap.group.Group;
import org.apache.directory.fortress.core.ldap.group.GroupMgr;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.User;
import org.apache.directory.fortress.core.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class GroupListModel<T extends Serializable> extends Model
{
    @SpringBean
    private GroupMgr groupMgr;
    private static final Logger log = Logger.getLogger(GroupListModel.class.getName());
    private transient Group group;
    private transient List<Group> groups = null;

    /**
     * Default constructor
     */
    public GroupListModel(final Session session)
    {
        Injector.get().inject(this);
        this.groupMgr.setAdmin( session );
    }

    /**
     * Group contains the search arguments.
     *
     * @param group
     */
    public GroupListModel(Group group, final Session session)
    {
        Injector.get().inject(this);
        this.group = group;
        this.groupMgr.setAdmin( session );
    }

    /**
     * This data is bound for {@link org.apache.directory.fortress.web.panel.ObjectListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (groups != null)
        {
            log.debug(".getObject count: " + group != null ? groups.size() : "null");
            return (T) groups;
        }
        if (group == null)
        {
            log.debug(".getObject null");
            groups = new ArrayList<Group>();
        }
        else
        {
            log.debug(".getObject group name: " + group != null ? group.getName() : "null");
            groups = getList(group);
        }
        return (T) groups;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<Group>)object).size() : "null");
        this.groups = (List<Group>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.groups = null;
        this.group = null;
    }

    public List<Group> getList(Group group)
    {
        List<Group> groupList = null;
        try
        {
            if( VUtil.isNotNullOrEmpty( group.getMembers() ))
            {
                String userId = group.getMembers().get( 0 );
                log.debug(".getList group name: " + group != null ? group.getName() : "null");
                groupList = groupMgr.find( new User( userId ) );
            }
            else
            {
                log.debug(".getList group name: " + group != null ? group.getName() : "null");
                groupList = groupMgr.find( group );
            }
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return groupList;
    }
}
