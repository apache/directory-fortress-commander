/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.openldap.fortress.DelReviewMgr;
import org.openldap.fortress.rbac.OrgUnit;
import org.openldap.fortress.rbac.Session;

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
        catch ( org.openldap.fortress.SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn( error );
        }
        return orgUnitList;
    }
}
