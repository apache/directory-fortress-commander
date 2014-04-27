/*
 * This work is part of OpenLDAP Software <http://www.openldap.org/>.
 *
 * Copyright 1998-2014 The OpenLDAP Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted only as authorized by the OpenLDAP
 * Public License.
 *
 * A copy of this license is available in the file LICENSE in the
 * top-level directory of the distribution or, alternatively, at
 * <http://www.OpenLDAP.org/license.html>.
 */

package org.openldap.commander.panel;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.treegrid.TreeGrid;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.openldap.commander.GlobalIds;
import org.openldap.commander.GlobalUtils;
import org.openldap.commander.PermListModel;
import org.openldap.commander.SaveModelEvent;
import org.openldap.commander.SecureIndicatingAjaxButton;
import org.openldap.commander.SecureIndicatingAjaxLink;
import org.openldap.commander.SelectModelEvent;
import org.openldap.fortress.rbac.FortEntity;
import org.openldap.fortress.rbac.PermObj;
import org.openldap.fortress.rbac.Permission;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.openldap.fortress.util.attr.VUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Shawn McKinney
 * @version $Rev$
 */
public class PermListPanel extends FormComponentPanel
{
    private static final Logger log = Logger.getLogger(PermListPanel.class.getName());
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private String permOperation;
    private String permObject;
    private TextField permObjectFld;
    private boolean isAdmin;

    public PermListPanel(String id, final boolean isAdmin)
    {
        super(id);
        this.isAdmin = isAdmin;
        PermListModel permListModel = new PermListModel(new Permission("", ""), isAdmin, GlobalUtils.getRbacSession(
                    this ));
        setDefaultModel(permListModel);
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>>();
        PropertyColumn objName = new PropertyColumn(new Model("Object Name"), "userObject.ObjName");
        objName.setInitialSize( 250 );
        columns.add(objName);
        columns.add(new PropertyColumn(new Model("Object Id"), "userObject.ObjId"));
        columns.add(new PropertyColumn(new Model("Operation Name"), "userObject.OpName"));
        String roleAssignLabel;
        if(isAdmin)
        {
            roleAssignLabel = "Admin Role Assignments";
        }
        else
        {
            roleAssignLabel = "RBAC Role Assignments";
        }

        PropertyColumn roles = new PropertyColumn(new Model(roleAssignLabel), "userObject.Roles");
        roles.setInitialSize(500);
        columns.add(roles);
        PropertyColumn abstractPermName = new PropertyColumn(new Model("Abstract Perm Name"), "userObject.AbstractName");
        abstractPermName.setInitialSize( 300 );
        columns.add(abstractPermName);

        List<Permission> perms = (List<Permission>) getDefaultModel().getObject();
        treeModel = createTreeModel(perms);
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>("permtreegrid", treeModel, columns)
        {
            @Override
            public void selectItem(IModel itemModel, boolean selected)
            {
                node = (DefaultMutableTreeNode) itemModel.getObject();
                if(!node.isRoot())
                {
                    Permission perm = (Permission) node.getUserObject();
                    log.debug("TreeGrid.addGrid.selectItem selected perm objNm: " + perm.getObjName() + " opNm: " + perm.getOpName());
                    if (super.isItemSelected(itemModel))
                    {
                        log.debug("TreeGrid.addGrid.selectItem item is selected");
                        super.selectItem(itemModel, false);
                    }
                    else
                    {
                        super.selectItem(itemModel, true);
                        SelectModelEvent.send(getPage(), this, perm);
                    }
                }
            }
        };
        grid.setContentHeight(50, SizeUnit.EM);
        grid.setAllowSelectMultiple(false);
        grid.setClickRowToSelect(true);
        grid.setClickRowToDeselect(false);
        grid.setSelectToEdit(false);
        // expand the root node
        grid.getTreeState().expandNode((TreeNode) treeModel.getRoot());
        this.listForm = new Form("form");
        this.listForm.add(grid);
        grid.setOutputMarkupId(true);
        add(this.listForm);
        permObjectFld = new TextField("permObject", new PropertyModel<String>(this, "permObject"));
        permObjectFld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior(GlobalIds.ONBLUR)
        {
          @Override
          protected void onUpdate(final AjaxRequestTarget target)
          {
              target.add( permObjectFld );
          }
        };
        permObjectFld.add( ajaxUpdater );
        this.listForm.add(permObjectFld);
        TextField permOperationFld = new TextField("permOperation", new PropertyModel<String>(this, "permOperation"));
        this.listForm.add(permOperationFld);
        addObjectSearchModal();

        this.listForm.add( new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.REVIEW_MGR, GlobalIds.FIND_PERMISSIONS )
        {
            @Override
            protected void onSubmit( AjaxRequestTarget target, Form form )
            {
                log.debug( ".search onSubmit" );
                info( "Searching Perms..." );
                if ( !VUtil.isNotNullOrEmpty( permObject ) )
                {
                    permObject = "";
                }
                if ( !VUtil.isNotNullOrEmpty( permOperation ) )
                {
                    permOperation = "";
                }
                Permission srchPerm = new Permission( permObject, permOperation );
                setDefaultModel( new PermListModel( srchPerm, isAdmin, GlobalUtils.getRbacSession( this ) ) );
                treeModel.reload();
                rootNode.removeAllChildren();
                List<Permission> perms = ( List<Permission> ) getDefaultModelObject();
                if ( VUtil.isNotNullOrEmpty( perms ) )
                {
                    for ( Permission perm : perms )
                        rootNode.add( new DefaultMutableTreeNode( perm ) );
                    info( "Search returned " + perms.size() + " matching objects" );
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

    private void addObjectSearchModal()
    {
        final ModalWindow objectsModalWindow;
        listForm.add( objectsModalWindow = new ModalWindow( "objectsearchmodal" ) );
        final ObjectSearchModalPanel objectSearchModalPanel = new ObjectSearchModalPanel( objectsModalWindow.getContentId(), objectsModalWindow, isAdmin );
        objectsModalWindow.setContent( objectSearchModalPanel );
        objectsModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            @Override
            public void onClose( AjaxRequestTarget target )
            {
                PermObj permObj = objectSearchModalPanel.getSelection();
                if ( permObj != null )
                {
                    permObject = permObj.getObjName();
                    target.add( permObjectFld );
                }
            }
        } );

        listForm.add( new SecureIndicatingAjaxLink( "objectAssignLinkLbl", GlobalIds.REVIEW_MGR, "findPermObjs" )
        {
            public void onClick( AjaxRequestTarget target )
            {
                String msg = "clicked on object search";
                msg += "objectSelection: " + permObject;
                objectSearchModalPanel.setSearchVal( permObject );
                objectSearchModalPanel.setAdmin( isAdmin );
                log.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                objectsModalWindow.show( target );
            }
            @Override
            protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
            {
                super.updateAjaxAttributes( attributes );
                AjaxCallListener ajaxCallListener = new AjaxCallListener()
                {
                    @Override
                    public CharSequence getFailureHandler( Component component )
                    {
                        return GlobalIds.WINDOW_LOCATION_REPLACE_COMMANDER_HOME_HTML;
                    }
                };
                attributes.getAjaxCallListeners().add( ajaxCallListener );
            }
        } );

        String modalLabel;
        if(isAdmin)
        {
            modalLabel = "Admin Permission Object Search Modal";
        }
        else
        {
            modalLabel = "RBAC Permission Object Search Modal";
        }
        objectsModalWindow.setTitle( modalLabel );
        objectsModalWindow.setInitialWidth( 700 );
        objectsModalWindow.setInitialHeight( 450 );
        objectsModalWindow.setCookieName( "objects-modal" );
    }

    @Override
    public void onEvent(IEvent event)
    {
        if (event.getPayload() instanceof SaveModelEvent)
        {
            SaveModelEvent modelEvent = (SaveModelEvent) event.getPayload();
            switch(modelEvent.getOperation())
            {
                case ADD:
                    add(modelEvent.getEntity());
                    break;
                case UPDATE:
                    /*
                    grid.markItemDirty(itemThatHasChanged);
                    grid.update();
                     */
                    modelChanged();
                    break;
                case DELETE:
                    prune();
                    break;
                case SEARCH:
                    setDefaultModel(new PermListModel((Permission) modelEvent.getEntity(), isAdmin, GlobalUtils.getRbacSession( this )));
                    treeModel.reload();
                    rootNode.removeAllChildren();
                    for (Permission perm : (List<Permission>) getDefaultModelObject())
                        rootNode.add(new DefaultMutableTreeNode(perm));
                    break;
                default:
                    break;
            }
            AjaxRequestTarget target = ((SaveModelEvent) event.getPayload()).getAjaxRequestTarget();
            target.add(grid);
            log.debug(".onEvent SaveModelEvent: " + target.toString());
        }
    }

    private void removeSelectedItems(TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid)
    {
        Collection<IModel<DefaultMutableTreeNode>> selected = grid.getSelectedItems();
        for (IModel<DefaultMutableTreeNode> model : selected)
        {
            DefaultMutableTreeNode node = model.getObject();
            treeModel.removeNodeFromParent(node);
            Permission perm = (Permission) node.getUserObject();
            log.debug(".removeSelectedItems perm objNm: " + perm.getObjName() + " opNm: " + perm.getOpName());
            List<Permission> perms = ((List<Permission>) getDefaultModel().getObject());
            perms.remove(perm);
        }
    }

    private DefaultTreeModel createTreeModel(List<Permission> perms)
    {
        DefaultTreeModel model;
        //Permission root = new Permission("Perms");
        Permission root = new Permission();
        rootNode = new DefaultMutableTreeNode(root);
        model = new DefaultTreeModel(rootNode);
        if (perms == null)
            log.debug(".createTreeModel no Perms found");
        else
        {
            log.debug(".createTreeModel Perms found:" + perms.size());
            for (Permission perm : perms)
                rootNode.add(new DefaultMutableTreeNode(perm));
        }
        return model;
    }

    public void add(FortEntity entity)
    {
        if (getDefaultModelObject() != null)
        {
             List<Permission> perms = ((List<Permission>) getDefaultModelObject());
            perms.add((Permission) entity);
            treeModel.insertNodeInto(new DefaultMutableTreeNode(entity), rootNode, perms.size());
        }
    }

    public void prune()
    {
        removeSelectedItems(grid);
    }
}