/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.AuditMgr;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.AuthZ;
import us.jts.fortress.rbac.Mod;
import us.jts.fortress.rbac.Session;
import us.jts.fortress.rbac.User;
import us.jts.fortress.rbac.UserAudit;
import us.jts.fortress.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class AuditModListModel<T extends Serializable> extends Model
{
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(AuditModListModel.class.getName());
    private transient UserAudit userAudit;
    private transient List<Mod> mods = null;

    /**
     * Default constructor
     */
    public AuditModListModel( final Session session )
    {
        Injector.get().inject(this);
        this.auditMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditModListModel( UserAudit userAudit, final Session session )
    {
        Injector.get().inject(this);
        this.userAudit = userAudit;
        this.auditMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (mods != null)
        {
            log.debug(".getObject count: " + userAudit != null ? mods.size() : "null");
            return (T) mods;
        }
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )  &&
               !VUtil.isNotNullOrEmpty( userAudit.getObjName() )  &&
               !VUtil.isNotNullOrEmpty( userAudit.getOpName() )  &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
           )
        {
            log.debug(".getObject null");
            mods = new ArrayList<Mod>();
        }
        else
        {
            // do we need to retrieve the internalUserId (which is what maps to admin modification record in slapd audit log?
            if(VUtil.isNotNullOrEmpty( userAudit.getUserId()) && !VUtil.isNotNullOrEmpty( userAudit.getInternalUserId()))
            {
                User user = getUser( userAudit );
                userAudit.setInternalUserId( user.getInternalId() );
                if(user == null)
                {
                    String warning = "Matching user not found for userId: " + userAudit.getUserId();
                    log.warn( warning );
                    throw new RuntimeException( warning );
                }
            }
            mods = getList(userAudit);
        }
        return (T) mods;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<AuthZ>)object).size() : "null");
        this.mods = (List<Mod>) object;
    }

    @Override
    public void detach()
    {
        this.mods = null;
        this.userAudit = null;
    }

    private List<Mod> getList(UserAudit userAudit)
    {
        List<Mod> modList = null;
        try
        {
            userAudit.setDn( "" );
            if(VUtil.isNotNullOrEmpty( userAudit.getObjName() ))
            {
                userAudit.setObjName( getTruncatedObjName( userAudit.getObjName() ) );
            }
            modList = auditMgr.searchAdminMods( userAudit );
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return modList;
    }

    /**
     * Utility will parse a String containing objName.operationName and return the objName only.
     *
     * @param szObj contains raw data format.
     * @return String containing objName.
     */
    private String getTruncatedObjName(String szObj)
    {
        int indx = szObj.lastIndexOf('.');
        if(indx == -1)
        {
            return szObj;
        }
        return szObj.substring(indx + 1);
    }

    private User getUser(UserAudit userAudit)
    {
        User user = null;
        try
        {
            user = reviewMgr.readUser( new User ( userAudit.getUserId() ) );
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getUser caught SecurityException=" + se;
            log.warn(error);
        }
        return user;
    }
}
