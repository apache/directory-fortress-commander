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
package org.apache.directory.fortress.web.control;

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;
import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.model.Permission;


/**
 * ...
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class FtIndicatingAjaxButton extends IndicatingAjaxButton
{
    Permission perm;

    @SpringBean
    private AccessMgr accessMgr;

    private static final Logger LOG = Logger.getLogger( SecureIndicatingAjaxButton.class.getName() );

    public FtIndicatingAjaxButton(String id)
    {
        super( id );
        String[] parts = id.split( "\\." );
        if( parts.length > 1)
        {
            String objName = parts[0];
            String opName = parts[1];
            this.perm = new Permission(objName, opName);
            if ( SecUtils.isFound( perm, this ) )
            {
                LOG.debug( "FtIndicatingAjaxButton id: " + id + ", status found" );
            }
            else
            {
                LOG.debug( "FtIndicatingAjaxButton id: " + id + ", status NOT found" );
                setVisible( false );
            }

        }
        else
        {
            throw new RuntimeException( "FtIndicatingAjaxButton Constructor ID: " + id + ", requires objectname.opname format" );
        }
    }

    protected boolean checkAccess( )
    {
        boolean isAuthorized = false;
        try
        {
            WicketSession session = ( WicketSession )getSession();
            isAuthorized = accessMgr.checkAccess( session.getSession(), perm );
            LOG.info( "Fortress checkAccess objName: " + this.perm.getObjName() + " opName: " + this.perm.getOpName() + " userId: " + session.getSession().getUserId() + " result: " + isAuthorized);
        }
        catch(org.apache.directory.fortress.core.SecurityException se)
        {
            String error = "Fortress SecurityException checkAccess objName: " + this.perm.getObjName() + " opName: " + this.perm.getOpName() + " error=" + se;
            LOG.error( error );
        }
        return isAuthorized;
    }


    protected boolean checkAccess( String objectId )
    {
        boolean isAuthorized = false;
        try
        {
            WicketSession session = ( WicketSession )getSession();
            Permission finePerm = new Permission(perm.getObjName(), perm.getOpName(), objectId);
            isAuthorized = accessMgr.checkAccess( session.getSession(), finePerm );
            LOG.info( "Fortress checkAccess objName: " + this.perm.getObjName() + " opName: " + this.perm.getOpName() + ", objId: " + finePerm.getObjId() + ", userId: " + session.getSession().getUserId() + " result: " + isAuthorized);
        }
        catch(org.apache.directory.fortress.core.SecurityException se)
        {
            String error = "Fortress SecurityException checkAccess objectName: " + this.perm.getObjName() + " opName: " + this.perm.getOpName() + ", objId: " + objectId + ", error=" + se;
            LOG.error( error );
        }
        return isAuthorized;
    }
}