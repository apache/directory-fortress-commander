package org.apache.directory.fortress.web.panel;

import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.rbac.AuthZ;
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.User;
import org.apache.directory.fortress.core.util.attr.VUtil;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by smckinn on 3/10/15.
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
            int eindx = raw.indexOf( '+' );
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
            int eindx = raw.substring( bindx ).indexOf( ',' );
            if ( eindx != -1 )
            {
                eindx += bindx;
                perm.setOpName( raw.substring( bindx + OP_NM.length() + 1, eindx ) );
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
}
