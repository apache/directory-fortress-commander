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
import org.apache.directory.fortress.core.model.Bind;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.UserAudit;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AuditBindListModel extends Model<SerializableList<Bind>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private AuditMgr auditMgr;
    private static final Logger LOG = Logger.getLogger( AuditBindListModel.class.getName() );
    private UserAudit userAudit;
    private SerializableList<Bind> binds = null;

    /**
     * Default constructor
     */
    public AuditBindListModel( Session session )
    {
        Injector.get().inject( this );
        auditMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditBindListModel( UserAudit userAudit, Session session )
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
    public SerializableList<Bind> getObject()
    {
        if ( binds != null )
        {
            LOG.debug( ".getObject count: " + binds.size() );
            return binds;
        }
        
        // if caller did not set userId return an empty list:
        if ( ( userAudit == null ) || 
             ( 
                 !StringUtils.isNotEmpty( userAudit.getUserId() ) &&
                 ( userAudit.getBeginDate() == null ) && 
                 ( userAudit.getEndDate() == null )
             )
           )
        {
            LOG.debug(".getObject null");
            binds = new SerializableList<>( new ArrayList<Bind>() );
        }
        else
        {
            // get the list of matching bind records from fortress:
            binds = new SerializableList<>( getList(userAudit) );
        }
        
        return binds;
    }
    

    @Override
    public void setObject( SerializableList<Bind> object )
    {
        LOG.debug( ".setObject count: " + object.size() );
        this.binds = object;
    }
    

    @Override
    public void detach()
    {
        binds = null;
        userAudit = null;
    }
    

    private List<Bind> getList( UserAudit userAudit )
    {
        List<Bind> bindList = null;
        
        try
        {
            bindList = auditMgr.searchBinds( userAudit );
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return bindList;
    }
}
