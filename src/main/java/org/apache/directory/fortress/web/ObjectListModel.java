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
import org.apache.directory.fortress.core.rbac.OrgUnit;
import org.apache.directory.fortress.core.rbac.PermObj;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class ObjectListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(ObjectListModel.class.getName());
    private transient PermObj permObj;
    private transient List<PermObj> permObjs = null;
    private boolean isAdmin;

    /**
     * Default constructor
     */
    public ObjectListModel(final boolean isAdmin, final Session session)
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param permObj
     */
    public ObjectListModel(PermObj permObj, final boolean isAdmin, final Session session)
    {
        Injector.get().inject(this);
        this.permObj = permObj;
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }



    /**
     * This data is bound for {@link org.apache.directory.fortress.web.panel.ObjectListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (permObjs != null)
        {
            log.debug(".getObject count: " + permObj != null ? permObjs.size() : "null");
            return (T) permObjs;
        }
        if (permObj == null)
        {
            log.debug(".getObject null");
            permObjs = new ArrayList<PermObj>();
        }
        else
        {
            log.debug(".getObject userId: " + permObj != null ? permObj.getObjName() : "null");
            permObjs = getList(permObj);
        }
        return (T) permObjs;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<PermObj>)object).size() : "null");
        this.permObjs = (List<PermObj>) object;
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
            log.debug(".getList permObjectName: " + permObj != null ? permObj.getObjName() : "null");
            if(VUtil.isNotNullOrEmpty(permObj.getOu()))
            {
                // TODO: make this work with administrative permissions:
                permObjList = reviewMgr.findPermObjs( new OrgUnit( permObj.getOu() ) );
            }
            else
            {
                if(isAdmin)
                {
                    permObj.setAdmin( true );
                }
                permObjList = reviewMgr.findPermObjs( permObj );
            }
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return permObjList;
    }
}
