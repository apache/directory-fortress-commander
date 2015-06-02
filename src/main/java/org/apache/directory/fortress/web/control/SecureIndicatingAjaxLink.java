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


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.directory.fortress.core.model.Permission;


/**
 * ...
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SecureIndicatingAjaxLink extends IndicatingAjaxLink
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;


    public SecureIndicatingAjaxLink( String id, String objName, String opName )
    {
        super( id );
        if ( !SecUtils.isFound( new Permission( objName, opName ), this ) )
            setEnabled( false );
    }


    @Override
    public void onClick( AjaxRequestTarget target )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
