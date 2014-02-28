/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;

import com.googlecode.wicket.kendo.ui.widget.splitter.IBorderLayout;
import com.googlecode.wicket.kendo.ui.widget.splitter.SplitterBehavior;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 5/21/13
 */
public class SixWaySplitter implements IBorderLayout
{
    public void addBorderLayout(MarkupContainer container)
    {
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
    }

    @Override
    public String getHorizontalPanes()
    {
         System.out.println("getHorizontalPanes");
        //return "[ { size: '15%' }, { }, { size: '15%' } ]";
        //return "[ { collapsible: true, size: '78%' }, { size: '20%', max: '300px' } ]";
        //return "[ { collapsible: true, size: '78%' }, { collapsible: true, size: '20%', min: '200px', max: '300px' } ]";
        return "[ { collapsible: true, size: '8%'}, { collapsible: true, size: '70%' }, { collapsible: true, size: '18%'} ]";
    }
}
