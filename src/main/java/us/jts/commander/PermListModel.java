/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.Role;
import us.jts.fortress.rbac.Session;

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
            log.debug(" .getObject perm objectNm: " + perm != null ? perm.getObjectName() : "null");
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
            String szObjectNm = perm != null ? perm.getObjectName() : "";
            String szOpNm = perm != null ? perm.getOpName() : "";
            log.debug(".getList objectNm: " + szObjectNm + " opNm: " + szOpNm);
            perm.setAdmin( isAdmin );
            permsList = reviewMgr.findPermissions(perm);
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return permsList;
    }
}
