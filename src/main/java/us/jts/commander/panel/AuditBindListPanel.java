/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import com.googlecode.wicket.jquery.ui.kendo.datetime.DatePicker;
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
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import us.jts.commander.AuditBindListModel;
import us.jts.commander.AuditBindPage;
import us.jts.commander.GlobalIds;
import us.jts.commander.GlobalUtils;
import us.jts.commander.SecureIndicatingAjaxButton;
import us.jts.commander.SecureIndicatingAjaxLink;
import us.jts.commander.SelectModelEvent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import us.jts.fortress.rbac.Bind;
import us.jts.fortress.rbac.User;
import us.jts.fortress.rbac.UserAudit;
import us.jts.fortress.util.attr.AttrHelper;
import us.jts.fortress.util.attr.VUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 8/11/13
 */
public class AuditBindListPanel extends FormComponentPanel
{
    private static final Logger LOG = Logger.getLogger(AuditBindListPanel.class.getName());
    private Form listForm;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode node;
    private TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String> grid;
    private DefaultMutableTreeNode rootNode;
    private TextField userFld;
    protected DatePicker beginDateDP;
    protected DatePicker endDateDP;
    private IModel<AuditBindListModel> pageModel;

    public AuditBindListPanel(String id, UserAudit userAudit )
    {
        super( id );
        init( userAudit );
    }

    private void init( UserAudit userAudit )
    {
        pageModel = new AuditBindListModel(userAudit, GlobalUtils.getRbacSession( this ) );
        setDefaultModel(pageModel);
        createAndLoadGrid();
        this.listForm = new Form("bindform");
        this.listForm.addOrReplace(grid);
        this.listForm.setModel( new CompoundPropertyModel<UserAudit>( userAudit ) );
        addEditFields();
        addButtons();
        add(this.listForm);
    }

    private void addEditFields()
    {
        userFld = new TextField(GlobalIds.USER_ID);
        userFld.setOutputMarkupId( true );
        AjaxFormComponentUpdatingBehavior ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
          @Override
          protected void onUpdate(final AjaxRequestTarget target)
          {
              target.add( userFld );
          }
        };
        userFld.add( ajaxUpdater );
        this.listForm.add(userFld);
        addUserSearchModal();

        final CheckBox failedOnlyCB = new CheckBox( GlobalIds.FAILED_ONLY );
        failedOnlyCB.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
          @Override
          protected void onUpdate(final AjaxRequestTarget target)
          {
              target.add( failedOnlyCB );
          }
        };
        failedOnlyCB.add( ajaxUpdater );
        failedOnlyCB.setRequired( false );
        this.listForm.add( failedOnlyCB );

        // Begin Date
        beginDateDP = new DatePicker(GlobalIds.BEGIN_DATE);
        beginDateDP.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
          @Override
          protected void onUpdate(final AjaxRequestTarget target)
          {
              target.add( beginDateDP );
          }
        };
        beginDateDP.add( ajaxUpdater );
        beginDateDP.setRequired( false );
        this.listForm.add( beginDateDP );

        // End Date
        endDateDP = new DatePicker(GlobalIds.END_DATE);
        endDateDP.setOutputMarkupId( true );
        ajaxUpdater = new AjaxFormComponentUpdatingBehavior( GlobalIds.ONBLUR )
        {
          @Override
          protected void onUpdate(final AjaxRequestTarget target)
          {
              target.add( endDateDP );
          }
        };
        endDateDP.add( ajaxUpdater );
        endDateDP.setRequired( false );
        this.listForm.add( endDateDP );
    }

    private void addButtons()
    {
        this.listForm.add(new SecureIndicatingAjaxButton( GlobalIds.SEARCH, GlobalIds.AUDIT_MGR, GlobalIds.GET_USER_BINDS )
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
            {
                LOG.debug( ".search onSubmit" );
                UserAudit userAudit = (UserAudit)listForm.getModelObject();
                if(!VUtil.isNotNullOrEmpty(userAudit.getUserId()))
                {
                    userAudit.setUserId( "" );
                }
                setResponsePage( new AuditBindPage( userAudit ) );
            }

            @Override
            public void onError(AjaxRequestTarget target, Form form)
            {
                LOG.warn( ".search.onError" );
                throw new RuntimeException( "error submitting form" );
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
        });
        this.listForm.add(new AjaxSubmitLink(GlobalIds.CLEAR)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form)
            {
                setResponsePage( new AuditBindPage( new UserAudit() ) );
            }

            @Override
            public void onError(AjaxRequestTarget target, Form form)
            {
                LOG.warn("AuditBindListPanel.clear.onError");
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
        });
    }

    private void createAndLoadGrid()
    {
        List<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>> columns =
            new ArrayList<IGridColumn<DefaultTreeModel, DefaultMutableTreeNode, String>>();
        PropertyColumn reqStart = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                    Model.of("Timestamp"), "userObject.reqStart");
        reqStart.setInitialSize(200);
        columns.add(reqStart);

        PropertyColumn requAuthzId = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                    Model.of("User ID"), "userObject.reqDN");
        requAuthzId.setInitialSize(200);
        columns.add(requAuthzId);

        PropertyColumn reqResult = new PropertyColumn<DefaultTreeModel, DefaultMutableTreeNode, String, String>(
                    Model.of("Result"), "userObject.reqResult");
        reqResult.setInitialSize(80);
        columns.add(reqResult);

        List<Bind> binds = (List<Bind>) getDefaultModel().getObject();
        treeModel = createTreeModel(binds);
        grid = new TreeGrid<DefaultTreeModel, DefaultMutableTreeNode, String>("bindtreegrid", treeModel, columns)
        {
            @Override
            public void selectItem(IModel itemModel, boolean selected)
            {
                node = (DefaultMutableTreeNode) itemModel.getObject();
                if(!node.isRoot())
                {
                    Bind bind = (Bind) node.getUserObject();
                    LOG.debug( "TreeGrid.addGrid.selectItem selected bind =" + bind.getReqDN() );
                    if (super.isItemSelected(itemModel))
                    {
                        LOG.debug( "TreeGrid.addGrid.selectItem item is selected" );
                        super.selectItem(itemModel, false);
                    }
                    else
                    {
                        super.selectItem(itemModel, true);
                        SelectModelEvent.send(getPage(), this, bind);
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
        grid.setOutputMarkupId(true);
    }

    private void addUserSearchModal()
    {
        final ModalWindow usersModalWindow;
        listForm.add( usersModalWindow = new ModalWindow( "usersearchmodal" ) );
        final UserSearchModalPanel userSearchModalPanel = new UserSearchModalPanel( usersModalWindow.getContentId(), usersModalWindow );
        usersModalWindow.setContent( userSearchModalPanel );
        usersModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
        {
            @Override
            public void onClose( AjaxRequestTarget target )
            {
                User userSelection = userSearchModalPanel.getUserSelection();
                if ( userSelection != null )
                {
                    LOG.debug( "modal selected:" + userSelection.getUserId() );
                    UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                    userAudit.setUserId( userSelection.getUserId() );
                    target.add( userFld );
                }
            }
        } );
        listForm.add( new SecureIndicatingAjaxLink( "userAssignLinkLbl", GlobalIds.REVIEW_MGR, GlobalIds.FIND_USERS )
        {
            public void onClick( AjaxRequestTarget target )
            {
                UserAudit userAudit = ( UserAudit ) listForm.getModelObject();
                String msg = "clicked on users search";
                msg += "userSelection: " + userAudit.getUserId();
                userSearchModalPanel.setSearchVal( userAudit.getUserId() );
                LOG.debug( msg );
                target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                usersModalWindow.show( target );
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
        usersModalWindow.setTitle( "User Search Modal" );
        usersModalWindow.setInitialWidth( 1000 );
        usersModalWindow.setInitialHeight( 700 );
        usersModalWindow.setCookieName( "user-search-modal" );
    }

    private DefaultTreeModel createTreeModel(List<Bind> binds)
    {
        DefaultTreeModel model;
        Bind root = new Bind();
        //root.setReqAuthzID( "Authentications" );
        rootNode = new DefaultMutableTreeNode(root);
        model = new DefaultTreeModel(rootNode);
        if (binds == null)
            LOG.debug("no Authentications found");
        else
        {
            LOG.debug("Binds found:" + binds.size());
            info("Loading " + binds.size() + " objects into list panel");
            loadTree( binds );
        }
        return model;
    }

    private void loadTree(List<Bind> binds)
    {
        for (Bind bind : binds)
        {
            Date start = null;
            try
            {
                start = AttrHelper.decodeGeneralizedTime( bind.getReqStart() );
            }
            catch (ParseException pe)
            {
                LOG.warn( "ParseException=" + pe.getMessage() );
            }
            if(start != null)
            {
                SimpleDateFormat formatter = new SimpleDateFormat( GlobalIds.AUDIT_TIMESTAMP_FORMAT );
                String formattedDate = formatter.format(start);
                bind.setReqStart( formattedDate );
            }
            if(bind.getReqResult().equals( GlobalIds.BIND_SUCCESS_CODE ))
            {
                bind.setReqResult( GlobalIds.SUCCESS );
            }
            else
            {
                bind.setReqResult( GlobalIds.FAILURE );
            }
            bind.setReqDN( GlobalUtils.getAuthZId( bind.getReqDN() ) );
            rootNode.add(new DefaultMutableTreeNode(bind));
        }
    }
}
