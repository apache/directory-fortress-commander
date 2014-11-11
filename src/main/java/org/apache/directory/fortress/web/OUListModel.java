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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class OUListModel<T extends Serializable> extends Model
{
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger log = Logger.getLogger( OUListModel.class.getName() );
    private transient OrgUnit orgUnit;
    private transient List<OrgUnit> orgUnits = null;

    /**
     * Default constructor
     */
    public OUListModel( boolean isUser, final Session session )
    {
        Injector.get().inject( this );
        this.delReviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param orgUnit
     */
    public OUListModel( OrgUnit orgUnit, final Session session )
    {
        Injector.get().inject( this );
        this.orgUnit = orgUnit;
        this.delReviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for SDListPanel
     *
     * @return T extends List<OrgUnit> orgUnits data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if ( orgUnits != null )
        {
            log.debug( ".getObject count: " + orgUnit != null ? orgUnits.size() : "null" );
            return ( T ) orgUnits;
        }
        if ( orgUnit == null )
        {
            log.debug( ".getObject null" );
            orgUnits = new ArrayList<OrgUnit>();
        }
        else
        {
            log.debug( ".getObject orgUnitNm: " + orgUnit != null ? orgUnit.getName() : "null" );
            orgUnits = getList( orgUnit );
        }
        return ( T ) orgUnits;
    }

    @Override
    public void setObject( Object object )
    {
        log.debug( ".setObject count: " + object != null ? ( ( List<OrgUnit> ) object ).size() : "null" );
        this.orgUnits = ( List<OrgUnit> ) object;
    }

    @Override
    public void detach()
    {
        //log.debug( ".detach" );
        this.orgUnits = null;
        this.orgUnit = null;
    }

    private List<OrgUnit> getList( OrgUnit orgUnit )
    {
        List<OrgUnit> orgUnitList = null;
        try
        {
            String szOrgUnitNm = orgUnit != null && orgUnit.getName() != null ? orgUnit.getName() : "";
            log.debug( ".getList orgUnitNm: " + szOrgUnitNm );
            orgUnitList = delReviewMgr.search( orgUnit.getType(), orgUnit.getName() );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn( error );
        }
        return orgUnitList;
    }
}
