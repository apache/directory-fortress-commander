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
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.Role;
import org.apache.directory.fortress.core.rbac.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class PermListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(PermListModel.class.getName());
    private transient Permission perm;
    private transient List<Permission> perms = null;
    private boolean isAdmin;

    public PermListModel(final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param perm
     */
    public PermListModel(Permission perm, final boolean isAdmin, final Session session )
    {
        Injector.get().inject(this);
        this.isAdmin = isAdmin;
        this.perm = perm;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Permission> perms data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (perms != null)
        {
            log.debug(".getObject count: " + perms != null ? perms.size() : "null");
            return (T) perms;
        }
        if (perm == null)
        {
            log.debug(".getObject null");
            perms = new ArrayList<Permission>();
        }
        else
        {
            log.debug(" .getObject perm objectNm: " + perm != null ? perm.getObjName() : "null");
            log.debug(" .getObject perm opNm: " + perm != null ? perm.getOpName() : "null");
            perms = getList(perm);
        }
        return (T) perms;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + perms != null ? ((List<Role>)object).size() : "null");
        this.perms = (List<Permission>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.perms = null;
        this.perm = null;
    }

    private List<Permission> getList(Permission perm)
    {
        List<Permission> permsList = null;
        try
        {
            String szObjectNm = perm != null ? perm.getObjName() : "";
            String szOpNm = perm != null ? perm.getOpName() : "";
            log.debug(".getList objectNm: " + szObjectNm + " opNm: " + szOpNm);
            perm.setAdmin( isAdmin );
            permsList = reviewMgr.findPermissions(perm);
        }
        catch (org.apache.directory.fortress.core.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return permsList;
    }
}
