/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.AuthZ;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.Session;
import us.jts.fortress.rbac.User;
import us.jts.fortress.util.attr.VUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ...
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
        //// ftOpNm=addUser,ftObjNm=us.jts.fortress.rbac.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
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
        //// ftOpNm=addUser,ftObjNm=us.jts.fortress.rbac.AdminMgrImpl,ou=AdminPerms,ou=ARBAC,dc=jts,dc=us
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
        catch ( us.jts.fortress.SecurityException se )
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
        catch ( us.jts.fortress.SecurityException se )
        {
            String error = "SecurityException=" + se;
            LOG.warn( error );

        }
        return user;
    }
}
