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

package org.apache.directory.fortress.web.panel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.AuthZ;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.GlobalIds;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 3/10/15
 */
final class AuditUtils
{
    private static final Logger LOG = Logger.getLogger( AuditUtils.class.getName() );

    private AuditUtils()
    {
    }

    /**
     *
     * @param raw
     * @return
     */
    static Permission getAuthZPerm( String raw )
    {
        //// ftOpNm=addUser,ftObjNm=org.apache.directory.fortress.core.model.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
        // ftObjId=006+ftOpNm=TOP1_6,ftObjNm=TOB1_4,ou=Permissions,ou=RBAC,dc=jts,dc=us
        Permission perm = new Permission();
        int bindx = raw.indexOf( GlobalIds.POBJ_ID );
        if ( bindx != -1 )
        {
            int eindx = raw.indexOf( '+' );
            if ( eindx != -1 )
            {
                perm.setObjId( raw.substring( bindx + GlobalIds.POBJ_ID.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( GlobalIds.POBJ_NAME );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( "," );
            if ( eindx != -1 )
            {
                eindx += bindx;
                perm.setObjName( raw.substring( bindx + GlobalIds.POBJ_NAME.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( GlobalIds.POP_NAME );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( ',' );
            if ( eindx != -1 )
            {
                eindx += bindx;
                perm.setOpName( raw.substring( bindx + GlobalIds.POP_NAME.length() + 1, eindx ) );
            }
        }
        return perm;
    }

    /**
     *
     * @param authZ
     */
    static void mapAuthZPerm( AuthZ authZ )
    {
        //// ftOpNm=addUser,ftObjNm=org.apache.directory.fortress.core.impl.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
        // ftObjId=006+ftOpNm=TOP1_6,ftObjNm=TOB1_4,ou=Permissions,ou=RBAC,dc=jts,dc=us
        String raw = authZ.getReqDN();

        // TODO: fix this mapping:
        //reqDerefAliases
        //reqAttr
        //reqAttrsOnly

        //Permission perm = new Permission();
        int bindx = raw.indexOf( GlobalIds.POBJ_ID );
        if ( bindx != -1 )
        {
            int eindx = raw.indexOf( '+' );
            if ( eindx != -1 )
            {
                authZ.setReqDerefAliases( raw.substring( bindx + GlobalIds.POBJ_ID.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( GlobalIds.POBJ_NAME );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( ',' );
            if ( eindx != -1 )
            {
                eindx += bindx;
                authZ.setReqAttr( raw.substring( bindx + GlobalIds.POBJ_NAME.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( GlobalIds.POP_NAME );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( ',' );
            if ( eindx != -1 )
            {
                eindx += bindx;
                authZ.setReqAttrsOnly( raw.substring( bindx + GlobalIds.POP_NAME.length() + 1, eindx ) );
            }
        }
    }

    /**
     *
     * @param inputString
     * @return
     */
    static String getAuthZId( String inputString )
    {
        //reqAuthzID: uid=fttu3user4,ou=people,dc=jts,dc=com
        String userId = null;
        if ( inputString != null && inputString.length() > 0 )
        {
            StringTokenizer maxTkn = new StringTokenizer( inputString, "," );
            if ( maxTkn.countTokens() > 0 )
            {
                String val = maxTkn.nextToken();
                int indx = val.indexOf( '=' );
                if ( indx >= 1 )
                {
                    userId = val.substring( indx + 1 );
                }
            }
        }
        return userId;
    }

    /**
     *
     * @param reviewMgr
     * @param userId
     * @return
     */
    static User getUser( ReviewMgr reviewMgr, String userId )
    {
        User user = null;
        try
        {
            user = reviewMgr.readUser( new User( userId ) );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "SecurityException=" + se;
            LOG.warn( error );

        }
        return user;
    }

    /**
     *
     * @param reviewMgr
     * @param internalId
     * @return
     */
    static User getUserByInternalId( ReviewMgr reviewMgr, String internalId )
    {
        User user = null;
        try
        {
            User inUser = new User();
            inUser.setInternalId( internalId );
            List<User> users = reviewMgr.findUsers( inUser );
            if ( CollectionUtils.isNotEmpty( users ) )
            {
                if ( users.size() > 1 )
                {
                    String error = "Found: " + users.size() + " users matching internalId: " + internalId;
                    LOG.warn( error );
                }
                user = users.get( 0 );
            }
            else
            {
                String error = "Can't find user matching internalId: " + internalId;
                LOG.warn( error );
            }
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "SecurityException=" + se;
            LOG.warn( error );
        }
        return user;
    }
}
