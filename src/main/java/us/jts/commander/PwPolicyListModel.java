/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.PwPolicyMgr;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.PwPolicy;
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
public class PwPolicyListModel<T extends Serializable> extends Model
{
    @SpringBean
    private PwPolicyMgr pwPolicyMgr;
    private static final Logger log = Logger.getLogger(PwPolicyListModel.class.getName());
    private transient PwPolicy policy;
    private transient List<PwPolicy> policies = null;

    /**
     * Default constructor
     */
    public PwPolicyListModel( final Session session )
    {
        Injector.get().inject(this);
        // TODO: enable this after search permission added:
        //this.pwPolicyMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param policy
     */
    public PwPolicyListModel(PwPolicy policy, final Session session )
    {
        Injector.get().inject(this);
        this.policy = policy;
        // TODO: enable this after search permission added:
        //this.pwPolicyMgr.setAdmin( session );
    }

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (policies != null)
        {
            log.debug(".getObject count: " + policy != null ? policies.size() : "null");
            return (T) policies;
        }
        if (policy == null)
        {
            log.debug(".getObject null");
            policies = new ArrayList<PwPolicy>();
        }
        else
        {
            log.debug(".getObject policyNm: " + policy != null ? policy.getName() : "null");
            policies = getList(policy);
        }
        return (T) policies;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<PwPolicy>)object).size() : "null");
        this.policies = (List<PwPolicy>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.policies = null;
        this.policy = null;
    }

    private List<PwPolicy> getList(PwPolicy policy)
    {
        List<PwPolicy> policiesList = null;
        try
        {
            String szPolicyNm = policy != null ? policy.getName() : "";
            log.debug(".getList policyNm: " + szPolicyNm);
            policiesList = pwPolicyMgr.search(szPolicyNm);
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return policiesList;
    }
}
