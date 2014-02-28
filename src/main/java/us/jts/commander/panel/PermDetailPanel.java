/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.form.combobox.ComboBox;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.commander.GlobalIds;
import us.jts.commander.GlobalUtils;
import us.jts.commander.SaveModelEvent;
import us.jts.commander.SecureIndicatingAjaxButton;
import us.jts.commander.SelectModelEvent;
import us.jts.fortress.AdminMgr;
import us.jts.fortress.DelAdminMgr;
import us.jts.fortress.rbac.AdminRole;
import us.jts.fortress.rbac.PermObj;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.UserRole;
import us.jts.fortress.util.attr.VUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kpmckinn
 * Date: 2/26/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PermDetailPanel extends FormComponentPanel
{
    @SpringBean
    private AdminMgr adminMgr;
    private static final String ROLES_SELECTION = "rolesSelection";
    private static final Logger log = Logger.getLogger(PermDetailPanel.class.getName());
    private Form editForm;
    private Displayable display;
    private boolean isAdmin;

    @SpringBean
    private DelAdminMgr delAdminMgr;

    public Form getForm()
    {
        return this.editForm;
    }

    public PermDetailPanel(String id, Displayable display, final boolean isAdmin)
    {
        super(id);
        this.isAdmin = isAdmin;
        this.adminMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.editForm = new PermDetailForm(GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<Permission>(new Permission()));
        this.display = display;
        add(editForm);
    }

    public class PermDetailForm extends Form
    {
        private ComboBox<String> rolesCB;
        private Component component;
        private String rolesSelection;
        private List<String> roles = new ArrayList<String>();
        private UserRole roleConstraint = new UserRole();
        private TextField objectTF;


        public PermDetailForm(String id, final IModel<Permission> model)
        {
            super(id, model);
            add( new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.ADMIN_MGR, "addPermission" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Add");
                    Permission perm = (Permission)form.getModel().getObject();
                    perm.setAdmin( isAdmin );
                    updateEntityWithComboData(perm);
                    try
                    {
                        adminMgr.addPermission(perm);
                        roles.add( rolesSelection );
                        rolesSelection = "";
                        component = editForm;
                        SaveModelEvent.send(getPage(), this, perm, target, SaveModelEvent.Operations.ADD);
                        String msg = "Perm objName: " + perm.getObjName() + " opName: " + perm.getOpName() + " has been added";
                        display.setMessage(msg);
                    }
                    catch (us.jts.fortress.SecurityException se)
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error(error);
                        display.setMessage(error);
                        display.display();
                    }
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.info("PermDetailPanel.add.onError caught");
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
            });
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.ADMIN_MGR, "updatePermission" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Commit");
                    Permission perm = (Permission)form.getModel().getObject();
                    perm.setAdmin( isAdmin );
                    updateEntityWithComboData(perm);
                    try
                    {
                        if(isAdmin )
                        {
                            if(VUtil.isNotNullOrEmpty( rolesSelection ) )
                            {
                                delAdminMgr.grantPermission( perm, new AdminRole(rolesSelection) );
                            }
                            else
                            {
                                delAdminMgr.updatePermission(perm);
                            }
                        }
                        else
                        {
                            adminMgr.updatePermission(perm);
                        }
                        roles.add( rolesSelection );
                        String msg = "Perm objName: " + perm.getObjName() + " opName: " + perm.getOpName() + " has been updated";
                        SaveModelEvent.send(getPage(), this, perm, target, SaveModelEvent.Operations.UPDATE);
                        rolesSelection = "";
                        component = editForm;
                        display.setMessage(msg);
                    }
                    catch (us.jts.fortress.SecurityException se)
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error(error);
                        display.setMessage(error);
                        display.display();
                    }
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.warn("PermDetailPanel.commit.onError");
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.ADMIN_MGR, "deletePermission" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Delete");
                    Permission perm = (Permission)form.getModel().getObject();
                    perm.setAdmin( isAdmin );
                    try
                    {
                        adminMgr.deletePermission(perm);
                        form.setModelObject( new Permission() );
                        rolesSelection = "";
                        roles = new ArrayList<String>();
                        rolesCB = new ComboBox<String>( "roles", new PropertyModel<String>( editForm, ROLES_SELECTION ),roles );
                        editForm.addOrReplace(rolesCB);
                        modelChanged();
                        String msg = "Perm objName: " + perm.getObjName() + " opName: " + perm.getOpName() + " has been deleted";
                        SaveModelEvent.send(getPage(), this, perm, target, SaveModelEvent.Operations.DELETE);
                        component = editForm;
                        display.setMessage(msg);
                    }
                    catch (us.jts.fortress.SecurityException se)
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error(error);
                        display.setMessage(error);
                        display.display();
                    }
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.warn("ControlPanel.delete.onError");
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
            add(new AjaxSubmitLink(GlobalIds.CANCEL)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    setModelObject(new Permission());
                    modelChanged();
                    rolesSelection = "";
                    roles = new ArrayList<String>();
                    rolesCB = new ComboBox<String>( "roles", new PropertyModel<String>( editForm, ROLES_SELECTION ),roles );
                    editForm.addOrReplace(rolesCB);
                    String msg = "Perm cancelled input form";
                    component = editForm;
                    display.setMessage(msg);
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.warn("ControlPanel.cancel.onError");
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

            objectTF = new TextField( GlobalIds.OBJ_NAME );
            // making this required prevents the object modal from opening when field empty:
            //objectTF.setRequired( true );
            objectTF.setOutputMarkupId( true );
            add( objectTF );
            addObjectSearchModal();

            if(isAdmin)
            {
                add( new Label( "permDetailLabel", "Administrative Permission Operation Detail" ) );
            }
            else
            {
                add( new Label( "permDetailLabel", "RBAC Permission Operation Detail" ) );
            }

            TextField opName = new TextField(GlobalIds.OP_NAME);
            add(opName);
            opName.setRequired(false);
            TextField objId = new TextField( GlobalIds.OBJECT_ID );
            add(objId);
            Label internalId = new Label("internalId");
            add(internalId);
            rolesCB = new ComboBox<String>( "roles", new PropertyModel<String>( this, ROLES_SELECTION ), roles );
            add(rolesCB);
            setOutputMarkupId(true);
            addRoleSearchModal();
            add( new AjaxButton( "roles.delete" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles.delete";
                    if ( VUtil.isNotNullOrEmpty( rolesSelection ) )
                    {
                        msg += " selection:" + rolesSelection;
                        Permission perm = ( Permission ) form.getModel().getObject();
                        if ( perm.getRoles() != null )
                        {
                            perm.getRoles().remove( rolesSelection );
                            roles.remove(  rolesSelection );
                            rolesSelection = "";
                            component = editForm;
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because permission does not have role set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because role selection is empty";
                    }
                    display.setMessage( msg );
                    log.debug( msg );
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
            add( objectsModalWindow = new ModalWindow( "objectsmodal" ) );
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
                        Permission perm = ( Permission ) editForm.getModel().getObject();
                        perm.setObjName( permObj.getObjName() );
                        target.add( objectTF );
                    }
                }
            } );

            add( new AjaxButton( "objName.search" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on ObjectNames search";
                    Permission perm = ( Permission ) editForm.getModel().getObject();
                    msg += perm.getObjName() != null ? ": " + perm.getObjName() : "";
                    objectSearchModalPanel.setSearchVal( perm.getObjName() );
                    objectSearchModalPanel.setAdmin( isAdmin );
                    display.setMessage( msg );
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
                modalLabel = "Admin Permission Object Selection Modal";
            }
            else
            {
                modalLabel = "RBAC Permission Object Selection Modal";
            }
            objectsModalWindow.setTitle( modalLabel );
            objectsModalWindow.setInitialWidth( 700 );
            objectsModalWindow.setInitialHeight( 450 );
            objectsModalWindow.setCookieName( "objects-modal" );
        }

        private void addRoleSearchModal()
        {
            final ModalWindow rolesModalWindow;
            add( rolesModalWindow = new ModalWindow( "permrolesmodal" ) );
            final RoleSearchModalPanel roleSearchModalPanel = new RoleSearchModalPanel( rolesModalWindow.getContentId(), rolesModalWindow, isAdmin );
            rolesModalWindow.setContent( roleSearchModalPanel );
            rolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    roleConstraint = roleSearchModalPanel.getRoleSelection();
                    if ( roleConstraint != null )
                    {
                        rolesSelection = roleConstraint.getName();
                        component = editForm;
                    }
                }
            } );

            add( new AjaxButton( "permroles.search" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles search";
                    msg += rolesSelection != null ? ": " + rolesSelection : "";
                    roleSearchModalPanel.setRoleSearchVal( rolesSelection );
                    display.setMessage( msg );
                    log.debug( msg );
                    target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                    rolesModalWindow.show( target );
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
                modalLabel = "Admin Role Selection Modal";
            }
            else
            {
                modalLabel = "RBAC Role Selection Modal";
            }
            rolesModalWindow.setTitle( modalLabel );
            rolesModalWindow.setInitialWidth( 700 );
            rolesModalWindow.setInitialHeight( 450 );
            rolesModalWindow.setCookieName( "role-assign-modal" );
        }

        private void updateEntityWithComboData(Permission perm)
        {
            String szValue = rolesCB.getModelObject();
            if(VUtil.isNotNullOrEmpty(szValue))
            {
                perm.setRole(szValue);
            }
        }

        @Override
        public void onEvent(final IEvent<?> event)
        {
            if (event.getPayload() instanceof SelectModelEvent)
            {
                SelectModelEvent modelEvent = (SelectModelEvent) event.getPayload();
                Permission perm = (Permission) modelEvent.getEntity();
                this.setModelObject(perm);
                rolesSelection = "";
                if(VUtil.isNotNullOrEmpty(perm.getRoles()))
                {
                    roles = new ArrayList<String>(perm.getRoles());
                    rolesCB = new ComboBox<String>( "roles", new PropertyModel<String>( this, ROLES_SELECTION ),roles );
                }
                else
                {
                    roles = new ArrayList<String>();
                    rolesCB = new ComboBox<String>( "roles", new PropertyModel<String>( this, ROLES_SELECTION ),roles );
                }
                editForm.addOrReplace(rolesCB);
                String msg = "Perm objName: " + perm.getObjName() + " opName: " + perm.getOpName() + " has been selected";
                log.debug(msg);
                component = editForm;
            }
            else if (event.getPayload() instanceof AjaxRequestTarget)
            {
                if (component != null)
                {
                    AjaxRequestTarget target = ((AjaxRequestTarget) event.getPayload());
                    log.debug(".onEvent AjaxRequestTarget: " + target.toString());
                    target.add(component);
                    component = null;
                }

                display.display((AjaxRequestTarget) event.getPayload());
            }
        }
    }
}