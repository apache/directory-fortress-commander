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
import org.apache.directory.fortress.core.DelReviewMgr;
import org.apache.directory.fortress.core.rbac.OrgUnit;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class OUListModel extends Model<SerializableList<OrgUnit>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger( OUListModel.class.getName() );
    private transient OrgUnit orgUnit;
    private transient SerializableList<OrgUnit> orgUnits = null;

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
            LOG.debug( ".getObject count: " + orgUnit != null ? orgUnits.size() : "null" );
            return orgUnits;
        }
        
        if ( orgUnit == null )
        {
            LOG.debug( ".getObject null" );
            orgUnits = new SerializableList<OrgUnit>( new ArrayList<OrgUnit>() );
        }
        else
        {
            LOG.debug( ".getObject orgUnitNm: " + orgUnit != null ? orgUnit.getName() : "null" );
            orgUnits = new SerializableList<OrgUnit>( getList( orgUnit ) );
        }
        
        return orgUnits;
    }

    
    @Override
    public void setObject( SerializableList<OrgUnit> object )
    {
        LOG.debug( ".setObject count: " + object != null ? ( ( List<OrgUnit> ) object ).size() : "null" );
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
        
        try
        {
            String szOrgUnitNm = orgUnit != null && orgUnit.getName() != null ? orgUnit.getName() : "";
            LOG.debug( ".getList orgUnitNm: " + szOrgUnitNm );
            orgUnitList = delReviewMgr.search( orgUnit.getType(), orgUnit.getName() );
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return orgUnitList;
    }
}
