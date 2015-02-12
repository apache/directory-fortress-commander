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
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
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
import org.apache.directory.fortress.web.GlobalIds;
import org.apache.directory.fortress.web.GlobalUtils;
import org.apache.directory.fortress.web.OUListModel;
import org.apache.directory.fortress.web.SaveModelEvent;
import org.apache.directory.fortress.web.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.SelectModelEvent;
import org.apache.directory.fortress.core.rbac.FortEntity;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.rbac.OrgUnit;
import org.apache.directory.fortress.core.util.attr.VUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String searchVal;
    private String searchLabel;


    public OUListPanel( String id, final boolean isUser )
    {
        super( id );
        OrgUnit orgUnit = new OrgUnit();
        orgUnit.setName( "" );
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

        OUListModel ouListModel = new OUListModel( orgUnit, GlobalUtils.getRbacSession( this ) );
        setDefaultModel( ouListModel );
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns = new ArrayList<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>>();
        PropertyColumn name = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( searchLabel ), "userObject.name" );
        name.setInitialSize( 400 );
        columns.add( name );

        PropertyColumn description = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "Description" ), "userObject.Description" );

        description.setInitialSize( 400 );
        columns.add( description );

        PropertyColumn parents = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String,
            String>( Model.of( "Parents" ), "userObject.parents" );
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
        grid.getTreeState().expandNode( ( TreeNode ) treeModel.getRoot() );
        this.listForm = new Form( "form" );
        this.listForm.add( grid );
        grid.setOutputMarkupId( true );
        TextField searchValFld = new TextField( GlobalIds.SEARCH_VAL, new PropertyModel<String>( this,
            GlobalIds.SEARCH_VAL ) );
        this.listForm.add( searchValFld );

        //this.listForm.add( new AjaxSubmitLink( "search" )
        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.DEL_REVIEW_MGR, "searchOU" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search onSubmit" );
                info( "Searching OrgUnits..." );
                if ( !VUtil.isNotNullOrEmpty( searchVal ) )
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
                setDefaultModel( new OUListModel( srchOu, GlobalUtils.getRbacSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<OrgUnit> orgUnits1 = ( List<OrgUnit> ) getDefaultModelObject();
                if ( VUtil.isNotNullOrEmpty( orgUnits1 ) )
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
            public void onError( AjaxRequestTarget target, Form form )
            {
                log.warn( ".search.onError" );
                target.add();
            }


            @Override
            protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
            {
                super.updateAjaxAttributes( attributes );
                AjaxCallListener ajaxCallListener = new AjaxCallListener()
                {
                    /** Default serialVersionUID */
                    private static final long serialVersionUID = 1L;


                    @Override
                    public CharSequence getFailureHandler( Component component )
                    {
                        return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                    }
                };
                attributes.getAjaxCallListeners().add( ajaxCallListener );
            }
        } );
        add( this.listForm );
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
            orgUnits.remove( orgUnit.getName() );
        }
    }


    private DefaultTreeModel createTreeModel( List<OrgUnit> orgUnits )
    {
        DefaultTreeModel model;
        OrgUnit root = new OrgUnit();
        //root.setName( searchLabel );
        rootNode = new DefaultMutableTreeNode( root );
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
