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

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.fortress.core.*;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.fortress.core.rbac.AuthZ;
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.User;
import org.apache.directory.fortress.core.util.attr.VUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class GlobalUtils
{
    private static final Logger LOG = Logger.getLogger( GlobalUtils.class.getName() );

    public static Session getRbacSession( Component component )
    {
        return ( ( RbacSession ) component.getSession() ).getRbacSession();
    }

    public static Session createRbacSession( AccessMgr accessMgr, String userId )
    {
        Session session;
        try
        {
            // Create an RBAC session and attach to Wicket session:
            session = accessMgr.createSession( new User( userId ), true );
            String message = "RBAC Session successfully created for userId: " + session.getUserId();
            LOG.debug( message );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "createRbacSession caught SecurityException=" + se;
            LOG.error( error );
            throw new RuntimeException( error );
        }
        return session;
    }

    public static void loadPermissionsIntoSession( DelAccessMgr delAccessMgr, Session session)
    {
        try
        {
            // Retrieve user permissions and attach RBAC session to Wicket session:
            ( ( RbacSession ) RbacSession.get() ).setSession( session );
            List<Permission> permissions = delAccessMgr.sessionPermissions( session );
            ( ( RbacSession ) RbacSession.get() ).setPermissions( permissions );
            String message = "RBAC Session successfully created for userId: " + session.getUserId();
            LOG.debug( message );
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            String error = "loadPermissionsIntoSession caught SecurityException=" + se;
            LOG.error( error );
            throw new RuntimeException( error );
        }
    }

    public static List<Permission> getRbacPermissions( Component component )
    {
        return ( ( RbacSession ) component.getSession() ).getPermissions();
    }

    public static boolean isAuthorized( String roleName, HttpServletRequest servletReq )
    {
        boolean isAuthorized = false;
        if ( servletReq.isUserInRole( roleName ) )
        {
            isAuthorized = true;
        }
        return isAuthorized;
    }

    public static boolean isFound( Permission permission, Component component )
    {
        List<Permission> permissions = GlobalUtils.getRbacPermissions( component );
        return VUtil.isNotNullOrEmpty( permissions ) && permissions.contains( permission );
    }

    /**
     * This utility method can deserialize any object but is used to convert java.security.Principal to Fortress RBAC session object.
     *
     * @param str contains String to deserialize
     * @param cls contains class to use for destination object
     * @return deserialization target object
     */
    public static <T> T deserialize(String str, Class<T> cls)
    {
        // deserialize the object
        try
        {
            // This encoding induces a bijection between byte[] and String (unlike UTF-8)
            byte b[] = str.getBytes("ISO-8859-1");
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return cls.cast(si.readObject());
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            LOG.warn( "deserialize caught UnsupportedEncodingException:" + e);
        }
        catch (IOException e)
        {
            LOG.warn( "deserialize caught IOException:" + e);
        }
        catch (ClassNotFoundException e)
        {
            LOG.warn( "deserialize caught ClassNotFoundException:" + e);
        }
        // this method failed so return null
        return null;
    }

    public static String getPageType( PageParameters parameters )
    {
        String pageType = null;
        if ( parameters != null )
        {
            List<StringValue> values = parameters.getValues( GlobalIds.PAGE_TYPE );
            if ( values != null && values.size() > 0 )
            {
                pageType = values.get( 0 ).toString();
            }
        }
        return pageType;
    }

    public static void mapAuthZPerm( AuthZ authZ )
    {
        //// ftOpNm=addUser,ftObjNm=org.apache.directory.fortress.core.rbac.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
        // ftObjId=006+ftOpNm=TOP1_6,ftObjNm=TOB1_4,ou=Permissions,ou=RBAC,dc=jts,dc=us
        String raw = authZ.getReqDN();

        // TODO: use fortress GlobalIds instead:
        final String OBJ_ID = "ftObjId";
        final String OBJ_NM = "ftObjNm";
        final String OP_NM = "ftOpNm";

        // TODO: fix this mapping:
        //reqDerefAliases
        //reqAttr
        //reqAttrsOnly

        //Permission perm = new Permission();
        int bindx = raw.indexOf( OBJ_ID );
        if ( bindx != -1 )
        {
            int eindx = raw.indexOf( "+" );
            if ( eindx != -1 )
            {
                authZ.setReqDerefAliases( raw.substring( bindx + OBJ_ID.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( OBJ_NM );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( "," );
            if ( eindx != -1 )
            {
                eindx += bindx;
                authZ.setReqAttr( raw.substring( bindx + OBJ_NM.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( OP_NM );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( "," );
            if ( eindx != -1 )
            {
                eindx += bindx;
                authZ.setReqAttrsOnly( raw.substring( bindx + OP_NM.length() + 1, eindx ) );
            }
        }
    }

    public static Permission getAuthZPerm( String raw )
    {
        //// ftOpNm=addUser,ftObjNm=org.apache.directory.fortress.core.rbac.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
        // ftObjId=006+ftOpNm=TOP1_6,ftObjNm=TOB1_4,ou=Permissions,ou=RBAC,dc=jts,dc=us

        // TODO: use fortress GlobalIds instead:
        final String OBJ_ID = "ftObjId";
        final String OBJ_NM = "ftObjNm";
        final String OP_NM = "ftOpNm";
        Permission perm = new Permission();
        int bindx = raw.indexOf( OBJ_ID );
        if ( bindx != -1 )
        {
            int eindx = raw.indexOf( "+" );
            if ( eindx != -1 )
            {
                perm.setObjId( raw.substring( bindx + OBJ_ID.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( OBJ_NM );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( "," );
            if ( eindx != -1 )
            {
                eindx += bindx;
                perm.setObjName( raw.substring( bindx + OBJ_NM.length() + 1, eindx ) );
            }
        }
        bindx = raw.indexOf( OP_NM );
        if ( bindx != -1 )
        {
            int eindx = raw.substring( bindx ).indexOf( "," );
            if ( eindx != -1 )
            {
                eindx += bindx;
                perm.setOpName( raw.substring( bindx + OP_NM.length() + 1, eindx ) );
            }
        }
        return perm;
    }

    public static String getAuthZId( String inputString )
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

    public static User getUser( ReviewMgr reviewMgr, String userId )
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


    public static User getUserByInternalId( ReviewMgr reviewMgr, String internalId )
    {
        User user = null;
        try
        {
            User inUser = new User();
            inUser.setInternalId( internalId );
            List<User> users = reviewMgr.findUsers( inUser );
            if ( VUtil.isNotNullOrEmpty( users ) )
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

    /**
     * Method will retrieve the relative distinguished name from a distinguished name variable.
     *
     * @param szDn contains ldap distinguished name.
     * @return rDn as string.
     */
    public static String getRdn( String szDn )
    {
        String szRdn = null;
        try
        {
            Dn dn = new Dn( szDn );
            Rdn rDn = dn.getRdn();
            szRdn = rDn.getName();
        }
        catch ( LdapInvalidDnException e )
        {
            String error = "GlobalUtils.getRdn dn: " + szDn + ", caught LdapInvalidDnException:" + e;
            throw new RuntimeException( error );

        }
        return szRdn;
    }
}
