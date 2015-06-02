/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License" ); you may not use this file except in compliance
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.AuditMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.AuthZ;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.UserAudit;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AuditAuthzListModel extends Model<SerializableList<AuthZ>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(AuditAuthzListModel.class.getName());
    private UserAudit userAudit;
    private SerializableList<AuthZ> authZs = null;

    /**
     * Default constructor
     */
    public AuditAuthzListModel( Session session )
    {
        Injector.get().inject( this );
        auditMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditAuthzListModel( UserAudit userAudit, Session session )
    {
        Injector.get().inject( this );
        this.userAudit = userAudit;
        auditMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public SerializableList<AuthZ> getObject()
    {
        if (authZs != null)
        {
            LOG.debug( ".getObject count: " + authZs.size() );
            return authZs;
        }
        
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !StringUtils.isNotEmpty( userAudit.getUserId() )   &&
               !StringUtils.isNotEmpty( userAudit.getObjName() )  &&
               !StringUtils.isNotEmpty( userAudit.getOpName() )  &&
               //!StringUtils.isNotEmpty( userAudit.getDn() )  &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
            ||
             ( !StringUtils.isNotEmpty( userAudit.getUserId() )   &&
                StringUtils.isNotEmpty( userAudit.getObjName() )  &&
                !StringUtils.isNotEmpty( userAudit.getOpName() )  &&
                userAudit.getBeginDate() == null  &&
                userAudit.getEndDate() == null
            )
           )

        {
            LOG.debug( ".getObject null" );
            authZs = new SerializableList<>( new ArrayList<AuthZ>() );
        }
        else
        {
            // get the list of matching authorization records from fortress:
            //log.debug( ".getObject authZ id: " + userAudit != null ? userAudit.getUserId() : "null" );
            if ( StringUtils.isNotEmpty( userAudit.getObjName() ) && StringUtils.isNotEmpty( userAudit.getOpName() ) && !StringUtils.isNotEmpty( userAudit.getDn() ) )
            {
                Permission permission = getPermission( userAudit );

                if ( permission == null)
                {
                    String warning = "Matching permission not found for object: " + userAudit.getObjName() + " operation: " + userAudit.getOpName();
                    LOG.warn( warning );
                    throw new RuntimeException( warning );
                }

                userAudit.setDn( permission.getDn() );
            }
            
            authZs = new SerializableList<>( getList( userAudit ) );
            userAudit.setDn( "" );
        }
        
        return authZs;
    }
    

    @Override
    public void setObject( SerializableList<AuthZ> object )
    {
        LOG.debug( ".setObject count: " + object.size() );
        authZs = object;
    }
    

    @Override
    public void detach()
    {
        authZs = null;
        userAudit = null;
    }
    

    private List<AuthZ> getList( UserAudit userAudit )
    {
        List<AuthZ> authZList = null;
        
        try
        {
            authZList = auditMgr.getUserAuthZs( userAudit );
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return authZList;
    }
    

    private Permission getPermission( UserAudit userAudit )
    {
        Permission permission = null;
        
        try
        {
            permission = reviewMgr.readPermission( new Permission ( userAudit.getObjName(), userAudit.getOpName(), userAudit.isAdmin()) );
        }
        catch ( SecurityException se )
        {
            String error = ".getPermission caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return permission;
    }
}
