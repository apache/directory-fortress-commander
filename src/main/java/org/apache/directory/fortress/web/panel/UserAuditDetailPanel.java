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


import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.core.model.User;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * Date: 8/10/13
 */
public class UserAuditDetailPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( UserAuditDetailPanel.class.getName() );


    public UserAuditDetailPanel( String id, final IModel userModel )
    {
        super( id, userModel );
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
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected byte[] getPhoto()
            {
                User user = ( User ) getModelObject();
                return user.getJpegPhoto();
            }
        } );
        setOutputMarkupId( true );

    }

}