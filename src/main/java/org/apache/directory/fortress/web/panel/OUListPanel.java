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


import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.model.OUListModel;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.model.OrgUnit;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class OUListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( OUListPanel.class.getName() );
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String searchVal;


    public OUListPanel( String id, final boolean isUser )
    {
        super( id );
        OrgUnit orgUnit = new OrgUnit();
        orgUnit.setName( "" );
        String searchLabel;
        if ( isUser )
        {
            orgUnit.setType( OrgUnit.Type.USER );
            searchLabel = "User OU Name";
        }
        else
        {
            orgUnit.setType( OrgUnit.Type.PERM );
            searchLabel = "Perm OU Name";
        }

        OUListModel ouListModel = new OUListModel( orgUnit, SecUtils.getSession( this ) );
        setDefaultModel( ouListModel );
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns = new ArrayList<>();
        PropertyColumn name = new PropertyColumn<>( Model.of( searchLabel ), "userObject.name" );
        name.setInitialSize( 400 );
        columns.add( name );

        PropertyColumn description = new PropertyColumn<>( Model.of( "Description" ), "userObject.Description" );

        description.setInitialSize( 400 );
        columns.add( description );

        PropertyColumn parents = new PropertyColumn<>( Model.of( "Parents" ), "userObject.parents" );
        parents.setInitialSize( 400 );
        columns.add( parents );

        List<OrgUnit> orgUnits = ( List<OrgUnit> ) getDefaultModel().getObject();
        treeModel = createTreeModel( orgUnits );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "outreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    OrgUnit orgUnit1 = ( OrgUnit ) node.getUserObject();
                    log.debug( "TreeGrid.addGrid.selectItem selected sdSet =" + orgUnit1.getName() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, orgUnit1 );
                    }
                }
            }
        };
        //grid.setContentHeight( 50, SizeUnit.EM );
        grid.setAllowSelectMultiple( false );
        grid.setClickRowToSelect( true );
        grid.setClickRowToDeselect( false );
        grid.setSelectToEdit( false );
        // expand the root node
        grid.getTreeState().expandAll();
        Form listForm = new Form( "form" );
        listForm.add( grid );
        grid.setOutputMarkupId( true );
        TextField searchValFld = new TextField( GlobalIds.SEARCH_VAL, new PropertyModel<String>( this,
            GlobalIds.SEARCH_VAL ) );
        listForm.add( searchValFld );

        //this.listForm.add( new AjaxSubmitLink( "search" )
        listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.DEL_REVIEW_MGR, "searchOU" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
            {
                log.debug( ".search onSubmit" );
                info( "Searching OrgUnits..." );
                if ( !StringUtils.isNotEmpty( searchVal ) )
                {
                    searchVal = "";
                }
                final OrgUnit srchOu = new OrgUnit();
                if ( isUser )
                {
                    srchOu.setType( OrgUnit.Type.USER );
                }
                else
                {
                    srchOu.setType( OrgUnit.Type.PERM );
                }
                srchOu.setName( searchVal );
                setDefaultModel( new OUListModel( srchOu, SecUtils.getSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<OrgUnit> orgUnits1 = ( List<OrgUnit> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( orgUnits1 ) )
                {
                    for ( OrgUnit ou : orgUnits1 )
                    {
                        rootNode.add( new DefaultMutableTreeNode( ou ) );
                    }
                    info( "Search returned " + orgUnits1.size() + " matching objects" );
                }
                else
                {
                    info( "No matching objects found" );
                }
                target.add( grid );
            }


            @Override
            public void onError(AjaxRequestTarget target, Form form)
            {
                log.warn( ".search.onError" );
                target.add();
            }


            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
            {
                super.updateAjaxAttributes( attributes );
                AjaxCallListener ajaxCallListener = new AjaxCallListener()
                {
                    /** Default serialVersionUID */
                    private static final long serialVersionUID = 1L;


                    @Override
                    public CharSequence getFailureHandler(Component component)
                    {
                        return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                    }
                };
                attributes.getAjaxCallListeners().add( ajaxCallListener );
            }
        } );
        add( listForm );
    }


    @Override
    public void onEvent( IEvent event )
    {
        if ( event.getPayload() instanceof SaveModelEvent )
        {
            SaveModelEvent modelEvent = ( SaveModelEvent ) event.getPayload();
            switch ( modelEvent.getOperation() )
            {
                case ADD:
                    add( modelEvent.getEntity() );
                    break;
                case UPDATE:
                    modelChanged();
                    break;
                case DELETE:
                    prune();
                    break;
                default:
                    break;
            }
            AjaxRequestTarget target = ( ( SaveModelEvent ) event.getPayload() ).getAjaxRequestTarget();
            target.add( grid );
            log.debug( ".onEvent SaveModelEvent: " + target.toString() );
        }
    }


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            OrgUnit orgUnit = ( OrgUnit ) node.getUserObject();
            log.debug( ".removeSelectedItems ou node: " + orgUnit.getName() );
            List<OrgUnit> orgUnits = ( ( List<OrgUnit> ) getDefaultModel().getObject() );
            orgUnits.remove( orgUnit );
        }
    }


    private DefaultTreeModel createTreeModel( List<OrgUnit> orgUnits )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( orgUnits == null )
        {
            log.debug( "no OrgUnits found" );
        }
        else
        {
            log.debug( "OrgUnits found:" + orgUnits.size() );
            for ( OrgUnit orgUnit : orgUnits )
            {
                rootNode.add( new DefaultMutableTreeNode( orgUnit ) );
            }
        }
        return model;
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<OrgUnit> orgUnits = ( ( List<OrgUnit> ) getDefaultModelObject() );
            orgUnits.add( ( OrgUnit ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, orgUnits.size() );
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }
}
