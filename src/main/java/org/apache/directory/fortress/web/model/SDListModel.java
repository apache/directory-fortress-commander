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
import org.apache.directory.fortress.core.model.Role;
import org.apache.directory.fortress.core.model.SDSet;
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
public class SDListModel extends Model<SerializableList<SDSet>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(SDListModel.class.getName());
    private SDSet sdSet;
    private SerializableList<SDSet> sdSets = null;

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
            LOG.debug( ".getObject count: " + sdSets.size() );
            return sdSets;
        }
        
        if ( sdSet == null )
        {
            LOG.debug( ".getObject null" );
            sdSets = new SerializableList<>( new ArrayList<SDSet>() );
        }
        else
        {
            LOG.debug( ".getObject sdSetNm: " + sdSet.getName() );
            sdSets = new SerializableList<>( getList( sdSet ) );
        }
        
        return sdSets;
    }
    

    @Override
    public void setObject( SerializableList<SDSet> object )
    {
        LOG.debug( ".setObject count: " + object.size() );
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

        if( sdSet == null )
        {
            throw new RuntimeException( "Invalid SDSet State" );
        }
        
        try
        {
            String szSdSetNm = sdSet.getName();
            LOG.debug( ".getList sdSetNm: " + szSdSetNm );

            if ( CollectionUtils.isNotEmpty( sdSet.getMembers() ) )
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
            // sort list by set name:
            if( CollectionUtils.isNotEmpty( sdSetList ))
            {
                Collections.sort( sdSetList, new Comparator<SDSet>()
                {
                    @Override
                    public int compare(SDSet s1, SDSet s2)
                    {
                        return s1.getName().compareToIgnoreCase( s2.getName() );
                    }
                } );
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
