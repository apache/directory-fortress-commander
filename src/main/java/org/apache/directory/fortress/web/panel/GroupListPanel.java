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
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.directory.fortress.web.common.GlobalIds;
import org.apache.directory.fortress.web.control.SecUtils;
import org.apache.directory.fortress.web.model.GroupListModel;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxLink;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.Group;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.model.User;

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
public class GroupListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( GroupListPanel.class.getName() );
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private TextField searchValFld;
    private RadioGroup radioGroup;
    private String searchVal;
    private char selectedRadioButton;
    private static final char NAMES = 'N';
    private static final char MEMBERS = 'M';


    public GroupListPanel( String id )
    {
        super( id );
        GroupListModel groupListModel = new GroupListModel( new Group( "" ), SecUtils.getSession( this ) );
        setDefaultModel( groupListModel );
        addGrid();
        radioGroup = new RadioGroup( "searchOptions", new PropertyModel( this, "selectedRadioButton" ) );
        add( radioGroup );
        Radio groupRb = new Radio( "groupRb", new Model( Character.valueOf( NAMES ) ) );
        radioGroup.add( groupRb );
        Radio memberRb = new Radio( "memberRb", new Model( Character.valueOf( MEMBERS ) ) );
        radioGroup.add( memberRb );
        addMemberSearchModal( memberRb );
        radioGroup.setOutputMarkupId( true );
        radioGroup.setRenderBodyOnly( false );
        searchValFld = new TextField( GlobalIds.SEARCH_VAL, new PropertyModel<String>( this, GlobalIds.SEARCH_VAL ) );
        searchValFld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate( final AjaxRequestTarget target )
            {
                target.add( searchValFld );
            }
        };
        searchValFld.add( ajaxUpdater );
        radioGroup.add( searchValFld );

        this.listForm.add( radioGroup );
        selectedRadioButton = NAMES;

        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.GROUP_MGR, "find" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search.onSubmit selected radio button: " + selectedRadioButton );
                info( "Searching Group Objects..." );
                if ( !StringUtils.isNotEmpty( searchVal ) )
                {
                    searchVal = "";
                }
                Group srchObject = new Group();
                switch ( selectedRadioButton )
                {
                    case NAMES:
                        log.debug( ".onSubmit GROUP RB selected" );
                        srchObject.setName( searchVal );
                        break;
                    case MEMBERS:
                        log.debug( ".onSubmit MEMBERS RB selected" );
                        srchObject.setMember( searchVal );
                        break;
                }
                setDefaultModel( new GroupListModel( srchObject, SecUtils.getSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<Group> groups = ( List<Group> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( groups ) )
                {
                    for ( Group group : groups )
                        rootNode.add( new DefaultMutableTreeNode( group ) );
                    info( "Search returned " + groups.size() + " matching objects" );
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
                    //modelEvent.
                    modelChanged();
                    break;
                case DELETE:
                    prune();
                    break;
                default:
                    log.error( "onEvent caught invalid operation" );
                    break;
            }
            AjaxRequestTarget target = ( ( SaveModelEvent ) event.getPayload() ).getAjaxRequestTarget();
            log.debug( ".onEvent AJAX - GroupListPanel - SaveModelEvent: " + target.toString() );
        }
    }


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            Group group = ( Group ) node.getUserObject();
            log.debug( ".removeSelectedItems user node: " + group.getName() );
            //List<Group> groups = ((List<Group>) getDefaultModel().getObject());
            //groups.remove(group.getName());
        }
    }


    private DefaultTreeModel createTreeModel( List<Group> groups )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( groups == null )
            log.debug( "no Groups found" );
        else
        {
            log.debug( ".createTreeModel Groups found:" + groups.size() );
            for ( Group group : groups )
                rootNode.add( new DefaultMutableTreeNode( group ) );
        }
        return model;
    }


    private void addGrid()
    {
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<>();

        PropertyColumn groupName = new PropertyColumn<>(
            Model.of( "Group Name" ), "userObject.Name" );
        groupName.setInitialSize( 250 );
        columns.add( groupName );

        PropertyColumn description = new PropertyColumn<>(
            Model.of( "Description" ), "userObject.Description" );
        description.setInitialSize( 300 );
        columns.add( description );

/*
        PropertyColumn protocol = new PropertyColumn( new Model( "Protocol" ), "userObject.Protocol" );
        protocol.setInitialSize( 400 );
        columns.add( protocol );
*/

        List<Group> groups = ( List<Group> ) getDefaultModel().getObject();
        treeModel = createTreeModel( groups );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "grouptreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    Group group = ( Group ) node.getUserObject();
                    log.debug( "TreeGrid.addGrid.selectItem selected group =" + group.getName() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, group );
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
        this.listForm = new Form( "grouplistform" );
        this.listForm.add( grid );
        add( this.listForm );
        grid.setOutputMarkupId( true );
    }


    private void addMemberSearchModal( Radio memberRb )
    {
        final ModalWindow memberModalWindow;
        listForm.add( memberModalWindow = new ModalWindow( "membersearchmodal" ) );
        final UserSearchModalPanel userSearchModalPanel = new UserSearchModalPanel( memberModalWindow.getContentId(),
            memberModalWindow );
        memberModalWindow.setContent( userSearchModalPanel );
        memberModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                User user = userSearchModalPanel.getUserSelection();
                if ( user != null )
                {
                    searchVal = user.getUserId();
                    selectedRadioButton = MEMBERS;
                    target.add( radioGroup );
                }
            }
        } );

        memberRb.add( new SecureIndicatingAjaxLink( "memberAssignLinkLbl", GlobalIds.REVIEW_MGR, "findUsers" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on ou search";
                msg += "memberSelection: " + searchVal;
                userSearchModalPanel.setSearchVal( searchVal );
                log.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                memberModalWindow.show( target );
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

        memberModalWindow.setTitle( "Member Search Modal" );
        memberModalWindow.setInitialWidth( 450 );
        memberModalWindow.setInitialHeight( 450 );
        memberModalWindow.setCookieName( "member-modal" );
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            //List<Group> groups = ((List<Group>) getDefaultModelObject());
            //groups.add( ( Group ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, 0 );
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }
}