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
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class GroupListModel extends Model<SerializableList<Group>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private GroupMgr groupMgr;
    private static final Logger LOG = Logger.getLogger( GroupListModel.class.getName() );
    private transient Group group;
    private transient SerializableList<Group> groups = null;

    /**
     * Default constructor
     */
    public GroupListModel( Session session )
    {
        Injector.get().inject( this );
        groupMgr.setAdmin( session );
    }
    

    /**
     * Group contains the search arguments.
     *
     * @param group
     */
    public GroupListModel( Group group, Session session )
    {
        Injector.get().inject( this );
        this.group = group;
        groupMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for {@link org.apache.directory.fortress.web.panel.ObjectListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public SerializableList<Group> getObject()
    {
        if ( groups != null )
        {
            LOG.debug( ".getObject count: " + group != null ? groups.size() : "null" );
            return groups;
        }
        
        if ( group == null )
        {
            LOG.debug( ".getObject null" );
            groups = new SerializableList<Group>( new ArrayList<Group>() );
        }
        else
        {
            LOG.debug( ".getObject group name: " + group != null ? group.getName() : "null" );
            groups = new SerializableList<Group>( getList( group ) );
        }
        return groups;
    }
    

    @Override
    public void setObject( SerializableList<Group> object )
    {
        LOG.debug(".setObject count: " + object != null ? object.size() : "null");
        groups = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug(".detach");
        groups = null;
        group = null;
    }
    

    public List<Group> getList( Group group )
    {
        List<Group> groupList = null;
        
        try
        {
            if ( VUtil.isNotNullOrEmpty( group.getMembers() ) )
            {
                String userId = group.getMembers().get( 0 );
                LOG.debug( ".getList group name: " + group != null ? group.getName() : "null" );
                groupList = groupMgr.find( new User( userId ) );
            }
            else
            {
                LOG.debug( ".getList group name: " + group != null ? group.getName() : "null" );
                groupList = groupMgr.find( group );
            }
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return groupList;
    }
}
