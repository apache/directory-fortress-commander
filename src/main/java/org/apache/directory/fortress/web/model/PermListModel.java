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
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Permission;
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
public class PermListModel extends Model<SerializableList<Permission>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(PermListModel.class.getName());
    private Permission perm;
    private SerializableList<Permission> perms = null;
    private boolean isAdmin;

    public PermListModel( boolean isAdmin, Session session )
    {
        Injector.get().inject( this );
        this.isAdmin = isAdmin;
        reviewMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param perm
     */
    public PermListModel( Permission perm, boolean isAdmin, Session session )
    {
        Injector.get().inject( this );
        this.isAdmin = isAdmin;
        this.perm = perm;
        reviewMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Permission> perms data will be bound to panel data view component.
     */
    @Override
    public SerializableList<Permission> getObject()
    {
        if ( perms != null )
        {
            LOG.debug( ".getObject count: " + perms.size() );
            
            return perms;
        }
        
        if (perm == null)
        {
            LOG.debug( ".getObject null ");
            perms = new SerializableList<>( new ArrayList<Permission>() );
        }
        else
        {
            LOG.debug( " .getObject perm objectNm: " + perm.getObjName() );
            LOG.debug( " .getObject perm opNm: " + perm.getOpName() );
            perms = new SerializableList<>( getList( perm ) );
        }
        
        return perms;
    }
    

    @Override
    public void setObject( SerializableList<Permission> object )
    {
        LOG.debug( ".setObject count: " + object.size() );
        this.perms = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.perms = null;
        this.perm = null;
    }
    

    private List<Permission> getList( Permission perm )
    {
        List<Permission> permsList = null;
        if( perm == null )
        {
            throw new RuntimeException( "Invalid permission state");
        }
        
        try
        {
            String szObjectNm = perm.getObjName();
            String szOpNm = perm.getOpName();
            LOG.debug( ".getList objectNm: " + szObjectNm + " opNm: " + szOpNm );
            perm.setAdmin( isAdmin );
            permsList = reviewMgr.findPermissions( perm );
            // sort list by abstract name:
            if( CollectionUtils.isNotEmpty( permsList ))
            {
                Collections.sort( permsList, new Comparator<Permission>()
                {
                    @Override
                    public int compare(Permission p1, Permission p2)
                    {
                        return p1.getAbstractName().compareToIgnoreCase( p2.getAbstractName() );
                    }
                } );
            }
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return permsList;
    }
}
