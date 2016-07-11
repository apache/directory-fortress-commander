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
import com.googlecode.wicket.kendo.ui.widget.splitter.SplitterAdapter;
import com.googlecode.wicket.kendo.ui.widget.splitter.SplitterBehavior;
import org.apache.wicket.MarkupContainer;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class FourWaySplitter implements IBorderLayout
{
    private String listPercentage;
    private String detailPercentage;

    public FourWaySplitter()
    {
        init( "64", "36" );
    }

    public FourWaySplitter(String listPercentage, String detailPercentage)
    {
        init(listPercentage, detailPercentage);
    }

    private void init(String listPercentage, String detailPercentage)
    {
        this.listPercentage = listPercentage;
        this.detailPercentage = detailPercentage;
    }

    public void addBorderLayout(MarkupContainer container)
    {
        SplitterBehavior vertical = new SplitterBehavior("#vertical", new SplitterAdapter());
        vertical.setOption("panes", this.getVerticalPanes()).setOption("orientation", "'vertical'");
        container.add(vertical);
        SplitterBehavior horizontal = new SplitterBehavior("#horizontal", new SplitterAdapter());
        horizontal.setOption("panes", this.getHorizontalPanes());
        container.add(horizontal);
    }

    @Override
    public String getVerticalPanes()
    {
        return "[ { collapsible: true, size: '80%' }, { collapsible: true, size: '5%'} ]";
    }

    @Override
    public String getHorizontalPanes()
    {
        return "[ { collapsible: true, resizable: false, size: '85px', min: '85x', max: '85px'}, { collapsible: true, size: '" + this.listPercentage + "%' }, { collapsible: true, size: '" + this.detailPercentage + "%'} ]";
    }
}
