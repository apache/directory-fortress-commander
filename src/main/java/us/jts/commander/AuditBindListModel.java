/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.AuditMgr;
import us.jts.fortress.rbac.Bind;
import us.jts.fortress.rbac.Session;
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
public class AuditBindListModel<T extends Serializable> extends Model
{
    @SpringBean
    private AuditMgr auditMgr;
    private static final Logger log = Logger.getLogger(AuditBindListModel.class.getName());
    private transient UserAudit userAudit;
    private transient List<Bind> binds = null;

    /**
     * Default constructor
     */
    public AuditBindListModel( final Session session )
    {
        Injector.get().inject(this);
        this.auditMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param userAudit
     */
    public AuditBindListModel( UserAudit userAudit, final Session session )
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
        if (binds != null)
        {
            log.debug(".getObject count: " + userAudit != null ? binds.size() : "null");
            return (T) binds;
        }
        // if caller did not set userId return an empty list:
        if (userAudit == null ||
             ( !VUtil.isNotNullOrEmpty( userAudit.getUserId() )   &&
               userAudit.getBeginDate() == null  &&
               userAudit.getEndDate() == null
             )
           )
        {
            log.debug(".getObject null");
            binds = new ArrayList<Bind>();
        }
        else
        {
            // get the list of matching bind records from fortress:
            binds = getList(userAudit);
        }
        return (T) binds;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<Bind>)object).size() : "null");
        this.binds = (List<Bind>) object;
    }

    @Override
    public void detach()
    {
        this.binds = null;
        this.userAudit = null;
    }

    private List<Bind> getList(UserAudit userAudit)
    {
        List<Bind> bindList = null;
        try
        {
            bindList = auditMgr.searchBinds( userAudit );
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return bindList;
    }
}
