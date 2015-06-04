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
import org.apache.directory.fortress.core.DelReviewMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.AdminRole;
import org.apache.directory.fortress.core.model.Role;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RoleListModel extends Model<SerializableList<? extends Role>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ReviewMgr reviewMgr;
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger(RoleListModel.class.getName());
    private Role role;
    private SerializableList<? extends Role> roles = null;
    private boolean isAdmin;


    public RoleListModel( boolean isAdmin, Session session )
    {
        Injector.get().inject( this );
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param role
     */
    public RoleListModel( Role role, boolean isAdmin, Session session )
    {
        Injector.get().inject( this );
        this.role = role;
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public SerializableList<? extends Role> getObject()
    {
        if ( roles != null )
        {
            LOG.debug(".getObject count: " + roles.size() );
            return roles;
        }
        
        if ( role == null )
        {
            LOG.debug(".getObject null");
            roles = new SerializableList<>( new ArrayList<Role>() );
        }
        else
        {
            LOG.debug(".getObject roleNm: " + role.getName() );
            
            if ( isAdmin )
            {
                roles = new SerializableList<>( getAdminList( ( role ).getName() ) );
            }
            else
            {
                roles = new SerializableList<>( getList( role.getName() ) );
            }
        }
        
        return roles;
    }
    

    @Override
    public void setObject( SerializableList<? extends Role> object )
    {
        LOG.debug(".setObject count: " + object.size() );
        this.roles = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug(".detach");
        roles = null;
        role = null;
    }
    

    private List<Role> getList( String szRoleNm )
    {
        List<Role> rolesList = null;
        
        try
        {
            LOG.debug( ".getList roleNm: " + szRoleNm );
            rolesList = reviewMgr.findRoles( szRoleNm );
            // sort list by role name:
            if( CollectionUtils.isNotEmpty( rolesList ))
            {
                Collections.sort( rolesList, new Comparator<Role>()
                {
                    @Override
                    public int compare(Role r1, Role r2)
                    {
                        return r1.getName().compareToIgnoreCase( r2.getName() );
                    }
                } );
            }
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error) ;
        }
        
        return rolesList;
    }
    

    private List<AdminRole> getAdminList( String szRoleNm )
    {
        List<AdminRole> rolesList = null;
        
        try
        {
            LOG.debug( ".getList roleNm: " + szRoleNm );
            rolesList = delReviewMgr.findRoles( szRoleNm );
            if( CollectionUtils.isNotEmpty( rolesList ))
            {
                Collections.sort( rolesList, new Comparator<AdminRole>()
                {
                    @Override
                    public int compare(AdminRole r1, AdminRole r2)
                    {
                        return r1.getName().compareToIgnoreCase( r2.getName() );
                    }
                } );
            }
        }
        catch ( SecurityException se )
        {
            String error = ".getAdminList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return rolesList;
    }
}
