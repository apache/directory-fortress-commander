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
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.rbac.Role;
import org.apache.directory.fortress.core.rbac.SDSet;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.util.attr.VUtil;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SDListModel extends Model<SerializableList<SDSet>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(SDListModel.class.getName());
    private transient SDSet sdSet;
    private transient SerializableList<SDSet> sdSets = null;

    /**
     * Default constructor
     */
    public SDListModel( boolean isStatic, Session session )
    {
        Injector.get().inject( this );
        reviewMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param sdSet
     */
    public SDListModel( SDSet sdSet, final Session session )
    {
        Injector.get().inject( this );
        this.sdSet = sdSet;
        reviewMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for SDListPanel
     *
     * @return T extends List<SDSet> sdSets data will be bound to panel data view component.
     */
    @Override
    public SerializableList<SDSet> getObject()
    {
        if ( sdSets != null )
        {
            LOG.debug( ".getObject count: " + sdSet != null ? sdSets.size() : "null" );
            return sdSets;
        }
        
        if ( sdSet == null )
        {
            LOG.debug( ".getObject null" );
            sdSets = new SerializableList<SDSet>( new ArrayList<SDSet>() );
        }
        else
        {
            LOG.debug( ".getObject sdSetNm: " + sdSet != null ? sdSet.getName() : "null" );
            sdSets = new SerializableList<SDSet>( getList( sdSet ) );
        }
        
        return sdSets;
    }
    

    @Override
    public void setObject( SerializableList<SDSet> object )
    {
        LOG.debug( ".setObject count: " + object != null ? object.size() : "null" );
        sdSets = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug(".detach");
        sdSets = null;
        sdSet = null;
    }

    
    private List<SDSet> getList( SDSet sdSet )
    {
        List<SDSet> sdSetList = null;
        
        try
        {
            String szSdSetNm = sdSet != null && sdSet.getName() != null ? sdSet.getName() : "";
            LOG.debug( ".getList sdSetNm: " + szSdSetNm );
        
            if ( VUtil.isNotNullOrEmpty( sdSet.getMembers() ) )
            {
                Object[] roleNms = sdSet.getMembers().toArray();
                String szRoleNm = (String)roleNms[0];
                Role role = new Role( szRoleNm );
                
                if ( sdSet.getType().equals( SDSet.SDType.STATIC ) )
                {
                    sdSetList = reviewMgr.ssdRoleSets( role );
                }
                else
                {
                    sdSetList = reviewMgr.dsdRoleSets( role );
                }
            }
            else
            {
                if ( sdSet.getType().equals( SDSet.SDType.STATIC ) )
                {
                    sdSetList = reviewMgr.ssdSets( sdSet );
                }
                else
                {
                    sdSetList = reviewMgr.dsdSets( sdSet );
                }
            }
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return sdSetList;
    }
}
