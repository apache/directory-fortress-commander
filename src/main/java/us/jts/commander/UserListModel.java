/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.commander.panel.UserListPanel;
import us.jts.fortress.DelReviewMgr;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.AdminRole;
import us.jts.fortress.rbac.OrgUnit;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.Role;
import us.jts.fortress.rbac.Session;
import us.jts.fortress.rbac.User;
import us.jts.fortress.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class UserListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger log = Logger.getLogger(UserListModel.class.getName());
    private transient User user;
    private transient Permission perm;
    private transient List<User> users = null;

    /**
     * Default constructor
     */
    public UserListModel( final Session session )
    {
        init( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param user
     */
    public UserListModel(User user, final Session session )
    {
        this.user = user;
        init( session );
        log.debug( "constructor userId: " + user != null ? user.getUserId() : "null" );
    }

    public UserListModel(Permission perm, final Session session )
    {
        this.perm = perm;
        init( session );
        log.debug( "constructor perm: " + perm != null ? perm.getObjectName() : "null" );
    }

    private void init(final Session session )
    {
        Injector.get().inject( this );
        this.reviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for {@link UserListPanel}
     *
     * @return T extends List<User> users data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (users != null)
        {
            log.debug(".getObject count: " + user != null ? users.size() : "null");
            return (T) users;
        }
        if (user == null && perm == null)
        {
            log.debug(".getObject null");
            users = new ArrayList<User>();
        }
        else
        {
            //log.debug(".getObject userId: " + user != null ? user.getUserId() : "null");
            users = getList(user);
        }
        return (T) users;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<User>)object).size() : "null");
        this.users = (List<User>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.users = null;
        this.user = null;
    }

    public List<User> getList(User user)
    {
        List<User> usersList = null;
        try
        {
            if(perm != null)
            {
                Set<String> users = reviewMgr.authorizedPermissionUsers( perm );
                if(VUtil.isNotNullOrEmpty( users ))
                {
                    usersList = new ArrayList<User>();
                    for(String userId : users)
                    {
                        User user1 = reviewMgr.readUser( new User(userId) );
                        usersList.add( user1 );
                    }
                }
            }
            else if(VUtil.isNotNullOrEmpty(user.getOu()))
            {
                usersList = reviewMgr.findUsers(new OrgUnit(user.getOu(), OrgUnit.Type.USER));
            }
            else if(VUtil.isNotNullOrEmpty(user.getRoles()))
            {
                usersList = reviewMgr.assignedUsers(new Role(user.getRoles().get(0).getName()));
            }
            else if(VUtil.isNotNullOrEmpty(user.getAdminRoles()))
            {
                usersList = delReviewMgr.assignedUsers(new AdminRole(user.getAdminRoles().get(0).getName()));
            }
            else
            {
                usersList = reviewMgr.findUsers(user);
            }
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return usersList;
    }
}
