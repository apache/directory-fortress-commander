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
package org.apache.directory.fortress.web.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.model.Group;
import org.apache.directory.fortress.core.GroupMgr;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private Group group;
    private SerializableList<Group> groups = null;

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
            LOG.debug( ".getObject count: " + groups.size() );
            return groups;
        }
        
        if ( group == null )
        {
            LOG.debug( ".getObject null" );
            groups = new SerializableList<>( new ArrayList<Group>() );
        }
        else
        {
            LOG.debug( ".getObject group name: " + group.getName() );
            List<Group> foundGroups = getList( group );
            if( CollectionUtils.isNotEmpty( foundGroups ))
            {
                groups = new SerializableList<>( foundGroups );
            }
            else
            {
                groups = new SerializableList<>( new ArrayList<Group>() );
            }
        }
        return groups;
    }
    

    @Override
    public void setObject( SerializableList<Group> object )
    {
        LOG.debug(".setObject count: " + object.size() );
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
            if ( CollectionUtils.isNotEmpty( group.getMembers() ) )
            {
                String userId = group.getMembers().get( 0 );
                LOG.debug( ".getList userId name: " + userId );
                groupList = groupMgr.find( new User( userId ) );
            }
            else
            {
                LOG.debug( ".getList group name: " + group.getName() );
                groupList = groupMgr.find( group );
            }
            // sort list by name:
            if( CollectionUtils.isNotEmpty( groupList ))
            {
                Collections.sort( groupList, new Comparator<Group>()
                {
                    @Override
                    public int compare(Group g1, Group g2)
                    {
                        return g1.getName().compareToIgnoreCase( g2.getName() );
                    }
                } );
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
