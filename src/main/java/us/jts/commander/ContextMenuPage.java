/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander;


import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.widget.menu.MenuItem;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;

import com.googlecode.wicket.jquery.ui.widget.menu.ContextMenu;
import com.googlecode.wicket.jquery.ui.widget.menu.ContextMenuBehavior;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin McKinney
 * @version $Rev$
 */
public class ContextMenuPage extends CommanderBasePage
{
    private static final long serialVersionUID = 1L;

    public ContextMenuPage()
    {
        // Menu //
        final ContextMenu menu = new ContextMenu( "menu", newMenuList() )
        {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onContextMenu( AjaxRequestTarget target, Component component )
            {
                //the menu-item list can be modified here
                //this.getItemList().add(new MenuItem("my new item"));
            }

            @Override
            public void onClick( AjaxRequestTarget target, IMenuItem item )
            {
                this.debug( "Clicked " + item.getTitle().getObject() );

                target.add( this );
                //target.add(feedback);
            }
        };

        this.add( menu );

        // Labels //
        final Label label1 = new Label( "label1", "my label 1" );
        label1.add( new ContextMenuBehavior( menu ) );
        this.add( label1 );

        final Label label2 = new Label( "label2", "my label 2" );
        label2.add( new ContextMenuBehavior( menu ) );
        this.add( label2 );
    }

    private List<IMenuItem> newMenuList()
    {
        List<IMenuItem> list = new ArrayList<IMenuItem>();

        list.add( new MenuItem( "Item with icon", JQueryIcon.FLAG ) );
        list.add( new MenuItem( "Change the title" )
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick( AjaxRequestTarget target )
            {
                this.setTitle( Model.of( "Title changed!" ) );
            }
        } );
        list.add( new MenuItem( "Another menu item" ) );
        list.add( new MenuItem( "Menu item, with sub-menu", JQueryIcon.BOOKMARK,
            this.newSubMenuList() ) ); // css-class are also allowed
        list.add( new MenuItem( "Desactivate me" )
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick( AjaxRequestTarget target )
            {
                this.setEnabled( false );
            }
        } );

        return list;
    }

    private List<IMenuItem> newSubMenuList()
    {
        List<IMenuItem> list = new ArrayList<IMenuItem>();

        list.add( new MenuItem( "Sub-menu #1" ) );
        list.add( new MenuItem( "Sub-menu #2" ) );
        list.add( new MenuItem( "Sub-menu #3" ) );

        return list;
    }
}
