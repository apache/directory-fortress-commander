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
import org.apache.directory.fortress.web.model.ObjectListModel;
import org.apache.directory.fortress.web.event.SaveModelEvent;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxButton;
import org.apache.directory.fortress.web.control.SecureIndicatingAjaxLink;
import org.apache.directory.fortress.web.event.SelectModelEvent;
import org.apache.directory.fortress.core.model.FortEntity;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.PermObj;

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
public class ObjectListPanel extends FormComponentPanel
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger( ObjectListPanel.class.getName() );
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
    private static final char OUS = 'O';


    public ObjectListPanel( String id, final boolean isAdmin )
    {
        super( id );
        ObjectListModel objectListModel = new ObjectListModel( new PermObj( "" ), isAdmin,
            SecUtils.getSession( this ) );
        setDefaultModel( objectListModel );
        addGrid();
        radioGroup = new RadioGroup( "searchOptions", new PropertyModel( this, "selectedRadioButton" ) );
        add( radioGroup );
        Radio objectRb = new Radio( "objectRb", new Model( Character.valueOf( NAMES ) ) );
        radioGroup.add( objectRb );
        Radio ouRb = new Radio( "ouRb", new Model( Character.valueOf( OUS ) ) );
        radioGroup.add( ouRb );
        addOUSearchModal( ouRb );
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

        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.REVIEW_MGR, "findPermObjs" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search.onSubmit selected radio button: " + selectedRadioButton );
                info( "Searching Permission Objects..." );
                if ( !StringUtils.isNotEmpty( searchVal ) )
                {
                    searchVal = "";
                }
                PermObj srchObject = new PermObj();
                switch ( selectedRadioButton )
                {
                    case NAMES:
                        log.debug( ".onSubmit OBJECT RB selected" );
                        srchObject.setObjName( searchVal );
                        break;
                    case OUS:
                        log.debug( ".onSubmit OUS RB selected" );
                        srchObject.setOu( searchVal );
                        break;
                }
                setDefaultModel( new ObjectListModel( srchObject, isAdmin,
                    SecUtils.getSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<PermObj> permObjs = ( List<PermObj> ) getDefaultModelObject();
                if ( CollectionUtils.isNotEmpty( permObjs ) )
                {
                    for ( PermObj permObj : permObjs )
                        rootNode.add( new DefaultMutableTreeNode( permObj ) );
                    info( "Search returned " + permObjs.size() + " matching objects" );
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
            log.debug( ".onEvent AJAX - ObjectListPanel - SaveModelEvent: " + target.toString() );
        }
    }


    private void removeSelectedItems( TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid )
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for ( IModel<DefaultMutableTreeNode> model : selected )
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent( node );
            PermObj permObj = ( PermObj ) node.getUserObject();
            log.debug( ".removeSelectedItems user node: " + permObj.getObjName() );
            List<PermObj> permObjs = ( ( List<PermObj> ) getDefaultModel().getObject() );
            permObjs.remove( permObj );
        }
    }


    private DefaultTreeModel createTreeModel( List<PermObj> permObjs )
    {
        DefaultTreeModel model;
        rootNode = new DefaultMutableTreeNode( null );
        model = new DefaultTreeModel( rootNode );
        if ( permObjs == null )
            log.debug( "no Permission Objects found" );
        else
        {
            log.debug( ".createTreeModel Permission Objects found:" + permObjs.size() );
            for ( PermObj permObj : permObjs )
                rootNode.add( new DefaultMutableTreeNode( permObj ) );
        }
        return model;
    }


    private void addGrid()
    {
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<>();

        PropertyColumn objName = new PropertyColumn<>(
            Model.of( "Object Name" ), "userObject.ObjName" );
        objName.setInitialSize( 300 );
        columns.add( objName );

        PropertyColumn ou = new PropertyColumn<>(
            Model.of( "Perm Organization" ), "userObject.Ou" );
        ou.setInitialSize( 200 );
        columns.add( ou );

        PropertyColumn description = new PropertyColumn<>(
            Model.of( "Description" ), "userObject.Description" );
        description.setInitialSize( 500 );
        columns.add( description );

        PropertyColumn type = new PropertyColumn( new Model( "Type" ), "userObject.Type" );
        type.setInitialSize( 200 );
        columns.add( type );

        List<PermObj> permObjs = ( List<PermObj> ) getDefaultModel().getObject();
        treeModel = createTreeModel( permObjs );
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>( "objecttreegrid", treeModel, columns )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void selectItem( IModel itemModel, boolean selected )
            {
                node = ( DefaultMutableTreeNode ) itemModel.getObject();
                if ( !node.isRoot() )
                {
                    PermObj permObj = ( PermObj ) node.getUserObject();
                    log.debug( "TreeGrid.addGrid.selectItem selected permission object =" + permObj.getObjName() );
                    if ( super.isItemSelected( itemModel ) )
                    {
                        log.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem( itemModel, false );
                    }
                    else
                    {
                        super.selectItem( itemModel, true );
                        SelectModelEvent.send( getPage(), this, permObj );
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
        this.listForm = new Form( "objectlistform" );
        this.listForm.add( grid );
        add( this.listForm );
        grid.setOutputMarkupId( true );
    }


    private void addOUSearchModal( Radio ouRb )
    {
        final ModalWindow ousModalWindow;
        listForm.add( ousModalWindow = new ModalWindow( "ousearchmodal" ) );
        final OUSearchModalPanel ouSearchModalPanel = new OUSearchModalPanel( ousModalWindow.getContentId(),
            ousModalWindow, false );
        ousModalWindow.setContent( ouSearchModalPanel );
        ousModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClose( AjaxRequestTarget target )
            {
                OrgUnit ou = ouSearchModalPanel.getSelection();
                if ( ou != null )
                {
                    searchVal = ou.getName();
                    selectedRadioButton = OUS;
                    target.add( radioGroup );
                }
            }
        } );

        ouRb.add( new SecureIndicatingAjaxLink( "ouAssignLinkLbl", GlobalIds.DEL_REVIEW_MGR, "searchOU" )
        {
            /** Default serialVersionUID */
            private static final long serialVersionUID = 1L;


            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on ou search";
                msg += "ouSelection: " + searchVal;
                ouSearchModalPanel.setSearchVal( searchVal );
                log.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                ousModalWindow.show( target );
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

        ousModalWindow.setTitle( "Permission Object Organizational Unit Search Modal" );
        ousModalWindow.setInitialWidth( 450 );
        ousModalWindow.setInitialHeight( 450 );
        ousModalWindow.setCookieName( "permou-modal" );
    }


    public void add( FortEntity entity )
    {
        if ( getDefaultModelObject() != null )
        {
            List<PermObj> permObjs = ( ( List<PermObj> ) getDefaultModelObject() );
            permObjs.add( ( PermObj ) entity );
            treeModel.insertNodeInto( new DefaultMutableTreeNode( entity ), rootNode, 0 );
            //treeModel.insertNodeInto(new DefaultMutableTreeNode(entity), rootNode, permObjs.size());
        }
    }


    public void prune()
    {
        removeSelectedItems( grid );
    }
}