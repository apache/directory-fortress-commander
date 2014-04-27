/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.commander.panel;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.openldap.commander.GlobalIds;
import org.openldap.fortress.rbac.User;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 8/10/13
 */
public class UserAuditDetailPanel extends FormComponentPanel
{
    private static final Logger LOG = Logger.getLogger(UserAuditDetailPanel.class.getName());

    public UserAuditDetailPanel(String id, final IModel userModel)
    {
        super(id, userModel);
        add( new Label( GlobalIds.USER_ID ) );
        add( new Label( GlobalIds.DESCRIPTION ) );
        add( new Label( GlobalIds.NAME ) );
        add( new Label( GlobalIds.OU ) );
        add( new Label( GlobalIds.TITLE ) );
        add( new Label( GlobalIds.ADDRESS_ADDRESSES ) );
        add( new Label( GlobalIds.ADDRESS_CITY ) );
        add( new Label( GlobalIds.ADDRESS_STATE ) );
        add( new Label( GlobalIds.ADDRESS_COUNTRY ) );
        add( new JpegImage( GlobalIds.JPEGPHOTO )
        {
            @Override
            protected byte[] getPhoto()
            {
                User user = (User)getModelObject();
                return user.getJpegPhoto();
            }
        } );
        setOutputMarkupId( true );

    }

}