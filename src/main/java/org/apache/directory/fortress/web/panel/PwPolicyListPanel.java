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
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.model.PwPolicyListModel;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.directory.fortress.core.model.PwPolicy;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * User: Shawn McKinney
 * Date: 6/12/13
 */
public class PwPolicyListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( PwPolicyListPanel.class.getName() );
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String searchVal;


    @SuppressWarnings( "Convert2Diamond" )
    public PwPolicyListPanel( String id )
    {
        super( id );
        PwPolicyListModel policyListModel = new PwPolicyListModel( new PwPolicy( "" ),
            SecUtils.getSession( this ) );
        setDefaultModel( policyListModel );
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>>();
        columns.add( new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Policy Name" ), "userObject.name" ) );

        PropertyColumn minAge = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Min Age" ), "userObject.minAge" );
        minAge.setInitialSize( 60 );
        columns.add( minAge );

        PropertyColumn maxAge = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Max Age" ), "userObject.maxAge" );
        maxAge.setInitialSize( 75 );
        columns.add( maxAge );

        PropertyColumn inHistory = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "History" ), "userObject.inHistory" );
        inHistory.setInitialSize( 50 );
        columns.add( inHistory );

        PropertyColumn minLength = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Min Len" ), "userObject.minLength" );
        minLength.setInitialSize( 60 );
        columns.add( minLength );

        PropertyColumn expireWarning = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Expire Warning" ), "userObject.expireWarning" );
        expireWarning.setInitialSize( 115 );
        columns.add( expireWarning );

        PropertyColumn graceLoginLimit = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Grace" ), "userObject.graceLoginLimit" );
        graceLoginLimit.setInitialSize( 50 );
        columns.add( graceLoginLimit );

        PropertyColumn lockout = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Lockout" ), "userObject.lockout" );
        lockout.setInitialSize( 60 );
        columns.add( lockout );

        PropertyColumn lockoutDuration = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Lockout Duration" ), "userObject.lockoutDuration" );
        lockoutDuration.setInitialSize( 125 );
        columns.add( lockoutDuration );

        PropertyColumn maxFailure = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Max Failure" ), "userObject.maxFailure" );
        maxFailure.setInitialSize( 80 );
        columns.add( maxFailure );

        PropertyColumn failureCountInterval = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Interval" ), "userObject.failureCountInterval" );
        failureCountInterval.setInitialSize( 60 );
        columns.add( failureCountInterval );

        PropertyColumn mustChange = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Must Change" ), "userObject.mustChange" );
        mustChange.setInitialSize( 90 );
        columns.add( mustChange );

        PropertyColumn allowUserChange = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Allow Change" ), "userObject.allowUserChange" );
        allowUserChange.setInitialSize( 95 );
        columns.add( allowUserChange );

        PropertyColumn safeModify = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
            Model.of( "Safe Modify" ), "userObject.safeModify" );
        safeModify.setInitialSize( 90 );
        columns.add( safeModify );

        /*
                PropertyColumn checkQuality = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                            Model.of("Check Quality"), "userObject.checkQuality");
                checkQuality.setInitialSize(100);
                columns.add(checkQuality);
        */

        List<PwPolicy> policies = ( List<PwPolicy> ) getDefaultModel().getObject();
        treeModel = createTreeModel( policies );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "policytreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    PwPolicy policy = ( PwPolicy ) node.getUserObject();
                    log.debug( "TreeGrid.addGrid.selectItem selected policy =" + policy.getName() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, policy );
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

        listForm.add( new AjaxSubmitLink( GlobalIds.SEARCH )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
            {
                log.debug( ".search onSubmit" );
                info( "Searching Policies..." );
                if ( !StringUtils.isNotEmpty( searchVal ) )
                {
                    searchVal = "";
                }
                PwPolicy srchPolicy = new PwPolicy( searchVal );
                setDefaultModel( new PwPolicyListModel( srchPolicy, SecUtils.getSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<PwPolicy> policies = ( List<PwPolicy> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( policies ) )
                {
                    for ( PwPolicy policy : policies )
                        rootNode.add( new DefaultMutableTreeNode( policy ) );
                    info( "Search returned " + policies.size() + " matching objects" );
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
            PwPolicy policy = ( PwPolicy ) node.getUserObject();
            log.debug( ".removeSelectedItems policy node: " + policy.getName() );
            List<PwPolicy> policies = ( ( List<PwPolicy> ) getDefaultModel().getObject() );
            policies.remove( policy );
        }
    }


    private DefaultTreeModel createTreeModel( List<PwPolicy> policies )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( policies == null )
            log.debug( "no Policies found" );
        else
        {
            log.debug( "Policies found:" + policies.size() );
            for ( PwPolicy policy : policies )
                rootNode.add( new DefaultMutableTreeNode( policy ) );
        }
        return model;
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<PwPolicy> policies = ( ( List<PwPolicy> ) getDefaultModelObject() );
            policies.add( ( PwPolicy ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, policies.size() );
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }
}
