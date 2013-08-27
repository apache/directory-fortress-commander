/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.Role;
import us.jts.fortress.rbac.SDSet;
import us.jts.fortress.rbac.Session;
import us.jts.fortress.util.attr.VUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * @param <T>
 */
public class SDListModel<T extends Serializable> extends Model
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger log = Logger.getLogger(SDListModel.class.getName());
    private transient SDSet sdSet;
    private transient List<SDSet> sdSets = null;

    /**
     * Default constructor
     */
    public SDListModel(boolean isStatic, final Session session )
    {
        Injector.get().inject(this);
        this.reviewMgr.setAdmin( session );
    }

    /**
     * User contains the search arguments.
     *
     * @param sdSet
     */
    public SDListModel(SDSet sdSet, final Session session )
    {
        Injector.get().inject(this);
        this.sdSet = sdSet;
        this.reviewMgr.setAdmin( session );
    }

    /**
     * This data is bound for SDListPanel
     *
     * @return T extends List<SDSet> sdSets data will be bound to panel data view component.
     */
    @Override
    public T getObject()
    {
        if (sdSets != null)
        {
            log.debug(".getObject count: " + sdSet != null ? sdSets.size() : "null");
            return (T) sdSets;
        }
        if (sdSet == null)
        {
            log.debug(".getObject null");
            sdSets = new ArrayList<SDSet>();
        }
        else
        {
            log.debug(".getObject sdSetNm: " + sdSet != null ? sdSet.getName() : "null");
            sdSets = getList(sdSet);
        }
        return (T) sdSets;
    }

    @Override
    public void setObject(Object object)
    {
        log.debug(".setObject count: " + object != null ? ((List<SDSet>)object).size() : "null");
        this.sdSets = (List<SDSet>) object;
    }

    @Override
    public void detach()
    {
        //log.debug(".detach");
        this.sdSets = null;
        this.sdSet = null;
    }

    private List<SDSet> getList(SDSet sdSet)
    {
        List<SDSet> sdSetList = null;
        try
        {
            String szSdSetNm = sdSet != null && sdSet.getName() != null ? sdSet.getName() : "";
            log.debug(".getList sdSetNm: " + szSdSetNm);
            if(VUtil.isNotNullOrEmpty(sdSet.getMembers()))
            {
                Object[] roleNms = sdSet.getMembers().toArray();
                String szRoleNm = (String)roleNms[0];
                Role role = new Role(szRoleNm);
                if(sdSet.getType().equals(SDSet.SDType.STATIC))
                {
                    sdSetList = reviewMgr.ssdRoleSets(role);
                }
                else
                {
                    sdSetList = reviewMgr.dsdRoleSets(role);
                }
            }
            else
            {
                if(sdSet.getType().equals(SDSet.SDType.STATIC))
                    sdSetList = reviewMgr.ssdSets(sdSet);
                else
                    sdSetList = reviewMgr.dsdSets(sdSet);
            }
        }
        catch (us.jts.fortress.SecurityException se)
        {
            String error = ".getList caught SecurityException=" + se;
            log.warn(error);
        }
        return sdSetList;
    }
}
