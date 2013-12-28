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
import us.jts.fortress.DelReviewMgr;
import us.jts.fortress.rbac.OrgUnit;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class OUSearchModalPanel extends Panel
{
    @SpringBean
    private DelReviewMgr delReviewMgr;
    private static final Logger LOG = Logger.getLogger(OUSearchModalPanel.class.getName());
    private ModalWindow window;
    private OrgUnit ouSelection;
    private String ouSearchVal;
    private boolean isUser;

    /**
     * @param id
     */
    public OUSearchModalPanel( String id, ModalWindow window, boolean isUser)
    {
        super( id );
        this.delReviewMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.window = window;
        this.isUser = isUser;
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
                final OrgUnit modelObject = ( OrgUnit ) item.getModelObject();
                item.add( new AjaxLink<Void>( "select" )
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        ouSelection = modelObject;
                        window.close( target );
                    }
                } );
                item.add( new Label( "name", new PropertyModel( item.getModel(), "name" ) ) );
                item.add( new Label( "description", new PropertyModel( item.getModel(), "description" ) ) );
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
                List<?> ous = null;
                try
                {
                    ouSelection = null;
                    if(ouSearchVal == null)
                        ouSearchVal = "";
                    if(isUser)
                        ous = delReviewMgr.search( OrgUnit.Type.USER, ouSearchVal );
                    else
                        ous = delReviewMgr.search( OrgUnit.Type.PERM, ouSearchVal );
                }
                catch ( us.jts.fortress.SecurityException se )
                {
                    String error = "loadPanel caught SecurityException=" + se;
                    LOG.error( error );
                }
                return ous;
            }
        };
        return ret;
    }

    public OrgUnit getSelection()
    {
        return ouSelection;
    }

    public void setSearchVal( String ouSearchVal )
    {
        this.ouSearchVal = ouSearchVal;
    }
}