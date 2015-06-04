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
import org.apache.directory.fortress.core.model.OrgUnit;
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
public class OUListModel extends Model<SerializableList<OrgUnit>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger( OUListModel.class.getName() );
    private OrgUnit orgUnit;
    private SerializableList<OrgUnit> orgUnits = null;

    /**
     * Default constructor
     */
    public OUListModel( boolean isUser, Session session )
    {
        Injector.get().inject( this );
        delReviewMgr.setAdmin( session );
    }

    
    /**
     * User contains the search arguments.
     *
     * @param orgUnit
     */
    public OUListModel( OrgUnit orgUnit, Session session )
    {
        Injector.get().inject( this );
        this.orgUnit = orgUnit;
        delReviewMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for SDListPanel
     *
     * @return T extends List<OrgUnit> orgUnits data will be bound to panel data view component.
     */
    @Override
    public SerializableList<OrgUnit> getObject()
    {
        if ( orgUnits != null )
        {
            LOG.debug( ".getObject count: " + orgUnits.size() );
            return orgUnits;
        }
        
        if ( orgUnit == null )
        {
            LOG.debug( ".getObject null" );
            orgUnits = new SerializableList<>( new ArrayList<OrgUnit>() );
        }
        else
        {
            LOG.debug( ".getObject orgUnitNm: " + orgUnit.getName() );
            orgUnits = new SerializableList<>( getList( orgUnit ) );
        }
        
        return orgUnits;
    }

    
    @Override
    public void setObject( SerializableList<OrgUnit> object )
    {
        LOG.debug( ".setObject count: " + ( object ).size() );
        orgUnits = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug( ".detach" );
        orgUnits = null;
        orgUnit = null;
    }
    

    private List<OrgUnit> getList( OrgUnit orgUnit )
    {
        List<OrgUnit> orgUnitList = null;
        if( orgUnit == null || orgUnit.getType() == null )
        {
            throw new RuntimeException( "Orgunit invalid state" );
        }
        try
        {
            String szOrgUnitNm = orgUnit.getName();
            LOG.debug( ".getList orgUnitNm: " + szOrgUnitNm );
            orgUnitList = delReviewMgr.search( orgUnit.getType(), orgUnit.getName() );
            // sort list by name:
            if( CollectionUtils.isNotEmpty( orgUnitList ))
            {
                Collections.sort( ( orgUnitList ), new Comparator<OrgUnit>()
                {
                    @Override
                    public int compare(OrgUnit o1, OrgUnit o2)
                    {
                        return o1.getName().compareToIgnoreCase( o2.getName() );
                    }
                } );
            }
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return orgUnitList;
    }
}
