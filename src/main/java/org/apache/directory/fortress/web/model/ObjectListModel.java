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
import org.apache.directory.api.util.Strings;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.PermObj;
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
public class ObjectListModel extends Model<SerializableList<PermObj>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger( ObjectListModel.class.getName() );
    private PermObj permObj;
    private SerializableList<PermObj> permObjs = null;
    private boolean isAdmin;

    /**
     * Default constructor
     */
    public ObjectListModel( boolean isAdmin, Session session)
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        reviewMgr.setAdmin( session );
    }
    

    /**
     * User contains the search arguments.
     *
     * @param permObj
     */
    public ObjectListModel( PermObj permObj, boolean isAdmin, Session session )
    {
        Injector.get().inject(this);
        this.permObj = permObj;
        this.isAdmin = isAdmin;
        reviewMgr.setAdmin( session );
    }


    /**
     * This data is bound for {@link org.apache.directory.fortress.web.panel.ObjectListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public SerializableList<PermObj> getObject()
    {
        if (permObjs != null)
        {
            LOG.debug(".getObject count: " + permObjs.size() );
            return permObjs;
        }
        
        if (permObj == null)
        {
            LOG.debug(".getObject null");
            permObjs = new SerializableList<>( new ArrayList<PermObj>());
        }
        else
        {
            LOG.debug(".getObject userId: " + permObj.getObjName());
            permObjs = new SerializableList<>( getList(permObj) );
        }
        
        return permObjs;
    }

    
    @Override
    public void setObject(SerializableList<PermObj> object)
    {
        LOG.debug(".setObject count: " + object.size() );
        this.permObjs = object;
    }

    
    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.permObjs = null;
        this.permObj = null;
    }

    
    public List<PermObj> getList(PermObj permObj)
    {
        List<PermObj> permObjList = null;
        
        try
        {
            LOG.debug( ".getList permObjectName:" + permObj.getObjName() );
            
            String ou = permObj.getOu();
            
            if ( Strings.isEmpty( ou ) )
            {
                if ( isAdmin )
                {
                    permObj.setAdmin( true );
                }
                
                permObjList = reviewMgr.findPermObjs( permObj );
            }
            else
            {
                // TODO: make this work with administrative permissions:
                permObjList = reviewMgr.findPermObjs( new OrgUnit( ou ) );
            }
            // sort list by objName:
            if( CollectionUtils.isNotEmpty( permObjList ))
            {
                Collections.sort( permObjList, new Comparator<PermObj>()
                {
                    @Override
                    public int compare(PermObj p1, PermObj p2)
                    {
                        return p1.getObjName().compareToIgnoreCase( p2.getObjName() );
                    }
                } );
            }
        }
        catch ( SecurityException se )
        {
            LOG.warn( ".getList caught SecurityException={}", se );
        }
        
        return permObjList;
    }
}
