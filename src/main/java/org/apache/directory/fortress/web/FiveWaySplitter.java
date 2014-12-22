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
 * Date: 5/21/13
 */
public class FiveWaySplitter implements IBorderLayout
{
    public void addBorderLayout(MarkupContainer container)
    {
        System.out.println("addBorderLayout");


        container.add(new SplitterBehavior("#vertical").setOption("panes", this.getVerticalPanes()).setOption("orientation", "'vertical'"));
        container.add(new SplitterBehavior("#horizontal").setOption("panes", this.getHorizontalPanes()));

        /*
        SplitterBehavior verticalSplitter = new SplitterBehavior("#vertical");
        verticalSplitter.setOption("orientation", "'vertical'");
        verticalSplitter.setOption("min", "'400px'");
        verticalSplitter.setOption("max", "'800px'");
        verticalSplitter.setOption("height", "'100%'");
        verticalSplitter.setOption("size", "'60px'");
        verticalSplitter.setOption("collapsible", "'false'");
        container.add(verticalSplitter);
        */

        /*
        SplitterBehavior horizontalSplitter = new SplitterBehavior("#horizontal");
        horizontalSplitter.setOption("orientation", "'horizontal'");
        horizontalSplitter.setOption("min", "'400px'");
        horizontalSplitter.setOption("max", "'800px'");
        horizontalSplitter.setOption("height", "'100%'");
        horizontalSplitter.setOption("size", "'20%'");
        horizontalSplitter.setOption("collapsible", "'true'");
        container.add(horizontalSplitter);
        */


        //container.add(new SplitterBehavior("#vertical").setOption("panes", this.getVerticalPanes()).setOption("orientation", "'vertical'"));
        //container.add(new SplitterBehavior("#horizontal").setOption("panes", this.getHorizontalPanes()));
    }

    @Override
    public String getVerticalPanes()
    {
         System.out.println("getVerticalPanes");
        //return "[ { resizable: false, size: '25%' }, {  }, { collapsible: true, size: '25%' } ]";
        //return "[ { resizable: false, size: '15%' }, { resizable: true, size: '70%' }, { collapsible: true, size: '15%' } ]";
        return "[ { collapsible: true, resizable: false, size: '50px', min: '50px', max: '50px', scrollable: false }, { resizable: true, size: '80%' }, { collapsible: true, size: '5%', min: '50px'} ]";
        //return "vertical";
    }

    @Override
    public String getHorizontalPanes()
    {
         System.out.println("getHorizontalPanes");
        //return "[ { size: '15%' }, { }, { size: '15%' } ]";
        //return "[ { collapsible: true, size: '78%' }, { size: '20%', max: '300px' } ]";
        //return "[ { collapsible: true, size: '78%' }, { collapsible: true, size: '20%', min: '200px', max: '300px' } ]";
        //return "[ { collapsible: true, resizable: false, size: '85px', min: '85x', max: '85px'}, { collapsible: true, size: '73%' }, { collapsible: true, size: '18%'} ]";
        return "[ { collapsible: true, resizable: false, size: '85px', min: '85x', max: '85px'}, { collapsible: true, size: '70%' }, { collapsible: true, size: '21%'} ]";
    }
}
