/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import us.jts.commander.GlobalIds;
import us.jts.fortress.rbac.User;

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