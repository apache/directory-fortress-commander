/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import com.googlecode.wicket.jquery.ui.form.spinner.Spinner;
import com.googlecode.wicket.jquery.ui.kendo.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.kendo.combobox.ComboBox;
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
import us.jts.fortress.rbac.SDSet;
import us.jts.fortress.rbac.UserRole;
import us.jts.fortress.util.attr.VUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 6/13/13
 */
public class SDDetailPanel extends FormComponentPanel
{
    @SpringBean
    private AdminMgr adminMgr;
    private static final String MEMBERS_SELECTION = "membersSelection";
    private static final Logger log = Logger.getLogger(SDDetailPanel.class.getName());
    private Form editForm;
    private Displayable display;
    private boolean isStatic;

    public Form getForm()
    {
        return this.editForm;
    }

    public SDDetailPanel(String id, Displayable display, boolean isStatic)
    {
        super(id);
        this.adminMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.isStatic = isStatic;
        this.editForm = new SDDetailForm(GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<SDSet>(new SDSet()));
        this.display = display;
        add(editForm);
    }

    public class SDDetailForm extends Form
    {
        private String internalId;
        private ComboBox<String> membersCB;
        private String membersSelection;
        private Component component;
        private List<String> members = new ArrayList<String>();
        private UserRole roleConstraint = new UserRole();

        public SDDetailForm(String id, final IModel<SDSet> model)
        {
            super(id, model);
            String opNameAdd;
            String opNameUpdate;
            String opNameDelete;
            if(isStatic)
            {
                opNameAdd = "createSsdSet";
                opNameUpdate = "updateSsdSet";
                opNameDelete = "deleteSsdSet";
            }
            else
            {
                opNameAdd = "createDsdSet";
                opNameUpdate = "updateDsdSet";
                opNameDelete = "deleteDsdSet";
            }

            add( new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.ADMIN_MGR, opNameAdd )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Add");
                    SDSet sdSet = (SDSet)form.getModel().getObject();
                    updateEntityWithComboData(sdSet);
                    try
                    {
                        if(isStatic)
                        {
                            adminMgr.createSsdSet(sdSet);
                        }
                        else
                        {
                            adminMgr.createDsdSet(sdSet);
                        }
                        SaveModelEvent.send(getPage(), this, sdSet, target, SaveModelEvent.Operations.ADD);
                        component = editForm;
                        String msg = "SDSet: " + sdSet.getName() + " has been added";
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
                    log.info("SDDetailPanel.add.onError");
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.ADMIN_MGR, opNameUpdate )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Commit");
                    SDSet sdSet = (SDSet)form.getModel().getObject();
                    try
                    {
                        updateEntityWithComboData(sdSet);
                        if(isStatic)
                            adminMgr.updateSsdSet(sdSet);
                        else
                            adminMgr.updateDsdSet(sdSet);
                        String msg = "SDSet: " + sdSet.getName() + " has been updated";
                        SaveModelEvent.send(getPage(), this, sdSet, target, SaveModelEvent.Operations.UPDATE);
                        component = editForm;
                        display.setMessage(msg);
                        membersSelection = "";
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
                    log.warn("SDDetailPanel.commmit.onError");
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

            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.ADMIN_MGR, opNameDelete )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Delete");
                    SDSet sdSet = (SDSet)form.getModel().getObject();

                    try
                    {
                        SDSet newSdSet = new SDSet();
                        if(isStatic)
                        {
                            adminMgr.deleteSsdSet(sdSet);
                            newSdSet.setType(SDSet.SDType.STATIC);
                        }
                        else
                        {
                            adminMgr.deleteDsdSet(sdSet);
                            newSdSet.setType(SDSet.SDType.DYNAMIC);
                        }
                        form.setModelObject( newSdSet );
                        modelChanged();
                        String msg = "SDSet: " + sdSet.getName() + " has been deleted";
                        membersSelection = "";
                        members = new ArrayList<String>();
                        membersCB = new ComboBox<String>( "members", new PropertyModel<String>( editForm, MEMBERS_SELECTION ),members );
                        editForm.addOrReplace( membersCB );
                        component = editForm;
                        SaveModelEvent.send(getPage(), this, sdSet, target, SaveModelEvent.Operations.DELETE);
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
                    log.warn("SDDetailPanel.delete.onError");
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
                    SDSet sdSet = new SDSet();
                    if(isStatic)
                        sdSet.setType(SDSet.SDType.STATIC);
                    else
                        sdSet.setType(SDSet.SDType.DYNAMIC);
                    setModelObject(sdSet);
                    membersSelection = "";
                    members = new ArrayList<String>();
                    membersCB = new ComboBox<String>( "members", new PropertyModel<String>( form, MEMBERS_SELECTION ),members );
                    editForm.addOrReplace( membersCB );
                    component = editForm;
                    String msg = "SDSet cancelled input form";
                    display.setMessage(msg);
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.warn("SDDetailPanel.cancel.onError");
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

            if(isStatic)
            {
                add( new Label( "sdAssignmentsLabel", "Static Separation of Duties Detail" ) );
            }
            else
            {
                add( new Label( "sdAssignmentsLabel", "Dynamic Separation of Duties Detail" ) );
            }

            TextField name = new TextField("name");
            add(name);
            TextField description = new TextField("description");
            description.setRequired(false);
            add(description);
            add(new Spinner<Integer>("cardinality"));
            Label iid = new Label("id");
            add(iid);
            membersCB = new ComboBox<String>( "members", new PropertyModel<String>( this, MEMBERS_SELECTION ), members );
            membersCB.setOutputMarkupId(true);
            add(membersCB);
            addRoleSearchModal();

            add( new AjaxButton( "members.delete" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on members.delete";
                    if ( VUtil.isNotNullOrEmpty( membersSelection ) )
                    {
                        msg += " selection:" + membersSelection;
                        SDSet sdSet = ( SDSet ) form.getModel().getObject();
                        if ( sdSet.getMembers() != null )
                        {
                            sdSet.getMembers().remove( membersSelection );
                            members.remove(  membersSelection );
                            membersSelection = "";
                            component = editForm;
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because role does not have members set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because members selection is empty";
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

        private void addRoleSearchModal()
        {
            final ModalWindow rolesModalWindow;
            add( rolesModalWindow = new ModalWindow( "rolesmodal" ) );
            final RoleSearchModalPanel roleSearchModalPanel = new RoleSearchModalPanel( rolesModalWindow.getContentId(), rolesModalWindow, false );
            rolesModalWindow.setContent( roleSearchModalPanel );
            rolesModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    roleConstraint = roleSearchModalPanel.getRoleSelection();
                    if ( roleConstraint != null )
                    {
                        membersSelection = roleConstraint.getName();
                        component = editForm;
                        //target.add( membersCB );
                    }
                }
            } );

            add( new AjaxButton( "roles.search" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on roles search";
                    msg += membersSelection != null ? ": " + membersSelection : "";
                    roleSearchModalPanel.setRoleSearchVal( membersSelection );
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

            rolesModalWindow.setTitle( "RBAC Role Selection Modal" );
            rolesModalWindow.setInitialWidth( 700 );
            rolesModalWindow.setInitialHeight( 450 );
            rolesModalWindow.setCookieName( "role-assign-modal" );
        }

        private void updateEntityWithComboData(SDSet sdSet)
        {
            if(VUtil.isNotNullOrEmpty(membersSelection))
            {
                sdSet.setMember(membersSelection);
                members.add( membersSelection );
            }
        }

        @Override
        public void onEvent(final IEvent<?> event)
        {
            if (event.getPayload() instanceof SelectModelEvent)
            {
                SelectModelEvent modelEvent = (SelectModelEvent) event.getPayload();
                SDSet sdSet = (SDSet) modelEvent.getEntity();
                this.setModelObject(sdSet);
                if(VUtil.isNotNullOrEmpty(sdSet.getMembers()))
                {
                    members = new ArrayList<String>(sdSet.getMembers());
                    membersCB = new ComboBox<String>( "members", new PropertyModel<String>( this, MEMBERS_SELECTION ),members );
                }
                else
                {
                    members = new ArrayList<String>();
                    membersCB = new ComboBox<String>( "members", new PropertyModel<String>( this, MEMBERS_SELECTION ),members );
                }
                editForm.addOrReplace(membersCB);
                String msg = "SDSet: " + sdSet.getName() + " has been selected";
                log.debug(msg);
                display.setMessage(msg);
                component = editForm;
            }
            else if (event.getPayload() instanceof AjaxRequestTarget)
            {
                // only add the form to ajax target if something has changed...
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
