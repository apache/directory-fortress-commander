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
import org.openldap.fortress.ldap.group.Group;
import org.openldap.fortress.ldap.group.GroupMgr;
import org.openldap.fortress.rbac.Session;
import org.openldap.fortress.rbac.User;
import org.openldap.fortress.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class GroupListModel<T extends Serializable> extends Model
{
    @SpringBean
    private GroupMgr groupMgr;
    private static final Logger log = Logger.getLogger(GroupListModel.class.getName());
    private transient Group group;
    private transient List<Group> groups = null;

    /**
     * Default constructor
     */
    public GroupListModel(final Session session)
    {
        Injector.get().inject(this);
        this.groupMgr.setAdmin( session );
    }

    /**
     * Group contains the search arguments.
     *
     * @param group
     */
    public GroupListModel(Group group, final Session session)
    {
        Injector.get().inject(this);
        this.group = group;
        this.groupMgr.setAdmin( session );
    }

    /**
     * This data is bound for {@link org.openldap.commander.panel.ObjectListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (groups != null)
        {
            log.debug(".getObject count: " + group != null ? groups.size() : "null");
            return (T) groups;
        }
        if (group == null)
        {
            log.debug(".getObject null");
            groups = new ArrayList<Group>();
        }
        else
        {
            log.debug(".getObject group name: " + group != null ? group.getName() : "null");
            groups = getList(group);
        }
        return (T) groups;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<Group>)object).size() : "null");
        this.groups = (List<Group>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.groups = null;
        this.group = null;
    }

    public List<Group> getList(Group group)
    {
        List<Group> groupList = null;
        try
        {
            if( VUtil.isNotNullOrEmpty( group.getMembers() ))
            {
                String userId = group.getMembers().get( 0 );
                log.debug(".getList group name: " + group != null ? group.getName() : "null");
                groupList = groupMgr.find( new User( userId ) );
            }
            else
            {
                log.debug(".getList group name: " + group != null ? group.getName() : "null");
                groupList = groupMgr.find( group );
            }
        }
        catch (org.openldap.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return groupList;
    }
}
