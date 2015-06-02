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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.AuditMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Mod;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.model.UserAudit;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AuditModListModel extends Model<SerializableList<Mod>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(AuditModListModel.class.getName());
    private UserAudit userAudit;
    private SerializableList<Mod> mods = null;

    /**
     * Default constructor
     */
    public AuditModListModel( final Session session )
    {
        Injector.get().inject(this);
        auditMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditModListModel( UserAudit userAudit, Session session )
    {
        Injector.get().inject(this);
        this.userAudit = userAudit;
        auditMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public SerializableList<Mod> getObject()
    {
        if (mods != null)
        {
            LOG.debug( ".getObject count: " + mods.size() );
            return mods;
        }
        
        // if caller did not set userId return an empty list:
        if ( ( userAudit == null ) ||
             ( 
                 !StringUtils.isNotEmpty( userAudit.getUserId() )  &&
                 !StringUtils.isNotEmpty( userAudit.getObjName() )  &&
                 !StringUtils.isNotEmpty( userAudit.getOpName() )  &&
                 ( userAudit.getBeginDate() == null ) &&
                 ( userAudit.getEndDate() == null )
             )
           )
        {
            LOG.debug( ".getObject null" );
            mods = new SerializableList<>( new ArrayList<Mod>() );
        }
        else
        {
            // do we need to retrieve the internalUserId (which is what maps to admin modification record in slapd audit log?
            if ( StringUtils.isNotEmpty( userAudit.getUserId() ) && !StringUtils.isNotEmpty( userAudit
                .getInternalUserId() ) )
            {
                User user = getUser( userAudit );
                
                if ( user == null )
                {
                    String warning = "Matching user not found for userId: " + userAudit.getUserId();
                    LOG.warn( warning );
                    throw new RuntimeException( warning );
                }

                userAudit.setInternalUserId( user.getInternalId() );
            }
            
            mods = new SerializableList<>( getList( userAudit ) );
        }
        
        return mods;
    }
    

    @Override
    public void setObject( SerializableList<Mod> object )
    {
        LOG.debug(".setObject count: " + object.size() );
        this.mods = object;
    }

    
    @Override
    public void detach()
    {
        this.mods = null;
        userAudit = null;
    }

    
    private List<Mod> getList( UserAudit userAudit )
    {
        List<Mod> modList = null;
        
        try
        {
            userAudit.setDn( "" );
            
            if ( StringUtils.isNotEmpty( userAudit.getObjName() ) )
            {
                userAudit.setObjName( getTruncatedObjName( userAudit.getObjName() ) );
            }
            
            modList = auditMgr.searchAdminMods( userAudit );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn(error);
        }
        
        return modList;
    }
    

    /**
     * Utility will parse a String containing objName.operationName and return the objName only.
     *
     * @param szObj contains raw data format.
     * @return String containing objName.
     */
    private String getTruncatedObjName( String szObj )
    {
        int indx = szObj.lastIndexOf( '.' );
        
        if ( indx == -1 )
        {
            return szObj;
        }
        
        return szObj.substring( indx + 1 );
    }
    

    private User getUser( UserAudit userAudit )
    {
        User user = null;
        
        try
        {
            user = reviewMgr.readUser( new User ( userAudit.getUserId() ) );
        }
        catch ( SecurityException se )
        {
            String error = ".getUser caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return user;
    }
}
