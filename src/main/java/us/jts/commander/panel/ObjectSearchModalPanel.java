/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */
package us.jts.commander.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.commander.GlobalUtils;
import us.jts.fortress.ReviewMgr;
import us.jts.fortress.rbac.PermObj;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class ObjectSearchModalPanel extends Panel
{
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(ObjectSearchModalPanel.class.getName());
    private ModalWindow window;
    private PermObj objectSelection;
    private String objectSearchVal;
    private boolean isAdmin;

    /**
     * @param id
     */
    public ObjectSearchModalPanel( String id, ModalWindow window, final boolean isAdmin )
    {
        super( id );
        this.reviewMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.window = window;
        loadPanel();
    }

    public void loadPanel()
    {
        LoadableDetachableModel requests = getListViewModel();
        PageableListView ouView = createListView( requests );
        add( ouView );
        add( new AjaxPagingNavigator( "navigator", ouView ) );
    }

    private PageableListView createListView( final LoadableDetachableModel requests )
    {
        final PageableListView listView = new PageableListView( "dataview", requests, 16 )
        {
            @Override
            protected void populateItem( final ListItem item )
            {
                final PermObj modelObject = ( PermObj ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        objectSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( "objectName", new PropertyModel( item.getModel(), "objectName" ) ) );
                item.add( new Label( "description", new PropertyModel( item.getModel(), "description" ) ) );
                item.add( new Label( "ou", new PropertyModel( item.getModel(), "ou" ) ) );
                item.add( new Label( "type", new PropertyModel( item.getModel(), "type" ) ) );
            }
        };
        return listView;
    }

    private LoadableDetachableModel getListViewModel()
    {
        final LoadableDetachableModel ret = new LoadableDetachableModel()
        {
            @Override
            protected Object load()
            {
                List<?> objects = null;
                try
                {
                    objectSelection = null;
                    if(objectSearchVal == null)
                        objectSearchVal = "";

                    PermObj permObj = new PermObj( objectSearchVal );
                    permObj.setAdmin( isAdmin );
                    objects = reviewMgr.findPermObjs( permObj );
                }
                catch ( us.jts.fortress.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return objects;
            }
        };
        return ret;
    }

    public void setAdmin( boolean admin )
    {
        isAdmin = admin;
    }

    public PermObj getSelection()
    {
        return objectSelection;
    }

    public void setSearchVal( String objectSearchVal )
    {
        this.objectSearchVal = objectSearchVal;
    }
}