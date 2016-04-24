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

import com.googlecode.wicket.kendo.ui.widget.splitter.IBorderLayout;
import com.googlecode.wicket.kendo.ui.widget.splitter.SplitterBehavior;
import org.apache.wicket.MarkupContainer;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class FiveWaySplitter implements IBorderLayout
{
    public void addBorderLayout(MarkupContainer container)
    {
        container.add(new SplitterBehavior("#vertical").setOption("panes", this.getVerticalPanes()).setOption("orientation", "'vertical'"));
        container.add(new SplitterBehavior("#horizontal").setOption("panes", this.getHorizontalPanes()));
    }

    @Override
    public String getVerticalPanes()
    {
        return "[ { collapsible: true, resizable: false, size: '50px', min: '50px', max: '50px', scrollable: false }, { resizable: true, size: '80%' }, { collapsible: true, size: '5%', min: '50px'} ]";
    }

    @Override
    public String getHorizontalPanes()
    {
        return "[ { collapsible: true, resizable: false, size: '85px', min: '85x', max: '85px'}, { collapsible: true, size: '70%' }, { collapsible: true, size: '21%'} ]";
    }
}
