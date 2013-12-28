/*
 * Copyright (c) 2013-2014, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

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
import us.jts.fortress.DelAdminMgr;
import us.jts.fortress.rbac.OrgUnit;
import us.jts.fortress.util.attr.VUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 7/27/13
 */
public class OUDetailPanel extends FormComponentPanel
{
    @SpringBean
    private DelAdminMgr delAdminMgr;
    private static final Logger log = Logger.getLogger(OUDetailPanel.class.getName());
    private Form editForm;
    private Displayable display;
    private boolean isUser;

    public Form getForm()
    {
        return this.editForm;
    }

    public OUDetailPanel(String id, Displayable display, boolean isUser)
    {
        super(id);
        this.delAdminMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.isUser = isUser;
        OrgUnit ou = new OrgUnit();
        if(isUser)
            ou.setType( OrgUnit.Type.USER );
        else
            ou.setType( OrgUnit.Type.PERM );
        this.editForm = new OUDetailForm(GlobalIds.EDIT_FIELDS, new CompoundPropertyModel<OrgUnit>(ou));
        this.display = display;
        add(editForm);
    }

    public class OUDetailForm extends Form
    {
        private static final String PARENTS = "parents";
        private static final String PARENTS_SELECTION = "parentsSelection";
        private String internalId;
        private ComboBox<String> parentsCB;
        private String parentsSelection;
        private Component component;
        private List<String> parents = new ArrayList<String>();
        private OrgUnit parent = new OrgUnit();

        public OUDetailForm(String id, final IModel<OrgUnit> model)
        {
            super(id, model);

            if(isUser)
                parent.setType( OrgUnit.Type.USER );
            else
                parent.setType( OrgUnit.Type.PERM );

            add( new SecureIndicatingAjaxButton( GlobalIds.ADD, GlobalIds.DEL_ADMIN_MGR, "addOU" )
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    log.debug(".onSubmit Add");
                    OrgUnit orgUnit = (OrgUnit)form.getModel().getObject();
                    updateEntityWithComboData(orgUnit);
                    try
                    {
                        delAdminMgr.add( orgUnit );
                        SaveModelEvent.send(getPage(), this, orgUnit, target, SaveModelEvent.Operations.ADD);
                        component = editForm;
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been added";
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
                    log.info("OUDetailPanel.add.onError caught");
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
            add( new SecureIndicatingAjaxButton( GlobalIds.COMMIT, GlobalIds.DEL_ADMIN_MGR, "updateOU" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit Commit" );
                    OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                    try
                    {
                        updateEntityWithComboData( orgUnit );
                        delAdminMgr.update( orgUnit );
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been updated";
                        SaveModelEvent.send( getPage(), this, orgUnit, target, SaveModelEvent.Operations.UPDATE );
                        component = editForm;
                        display.setMessage( msg );
                        parentsSelection = "";
                    }
                    catch ( us.jts.fortress.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "OUDetailPanel.update.onError" );
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
            add( new SecureIndicatingAjaxButton( GlobalIds.DELETE, GlobalIds.DEL_ADMIN_MGR, "deleteOU" )
            {
                @Override
                protected void onSubmit( AjaxRequestTarget target, Form form )
                {
                    log.debug( ".onSubmit delete" );
                    OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                    try
                    {
                        delAdminMgr.delete( orgUnit );
                        OrgUnit newOrgUnit = new OrgUnit();

                        if ( isUser )
                        {
                            newOrgUnit.setType( OrgUnit.Type.USER );
                        }
                        else
                        {
                            newOrgUnit.setType( OrgUnit.Type.PERM );
                        }
                        form.setModelObject( newOrgUnit );
                        modelChanged();
                        String msg = "OrgUnit: " + orgUnit.getName() + " has been deleted";
                        parentsSelection = "";
                        parents = new ArrayList<String>();
                        parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( form,
                            PARENTS_SELECTION ), parents );
                        editForm.addOrReplace( parentsCB );
                        component = editForm;
                        SaveModelEvent.send( getPage(), this, orgUnit, target, SaveModelEvent.Operations.DELETE );
                        display.setMessage( msg );
                    }
                    catch ( us.jts.fortress.SecurityException se )
                    {
                        String error = ".onSubmit caught SecurityException=" + se;
                        log.error( error );
                        display.setMessage( error );
                        display.display();
                    }
                }

                @Override
                public void onError( AjaxRequestTarget target, Form form )
                {
                    log.warn( "OUDetailPanel.delete.onError" );
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
            add(new AjaxSubmitLink(GlobalIds.CANCEL)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form)
                {
                    OrgUnit ou = new OrgUnit();
                    if(isUser)
                        ou.setType( OrgUnit.Type.USER );
                    else
                        ou.setType( OrgUnit.Type.PERM );

                    setModelObject(ou);
                    parentsSelection = "";
                    parents = new ArrayList<String>();
                    parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( form, PARENTS_SELECTION ),parents );
                    editForm.addOrReplace( parentsCB );
                    component = editForm;
                    String msg = "OU Detail cancelled input form";
                    display.setMessage(msg);
                }

                @Override
                public void onError(AjaxRequestTarget target, Form form)
                {
                    log.warn( "OUDetailPanel.cancel.onError" );
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

            if(isUser)
            {
                add( new Label( "ouAssignmentsLabel", "User Organizational Detail" ) );
            }
            else
            {
                add( new Label( "ouAssignmentsLabel", "Permission Organizational Detail" ) );
            }

            TextField name = new TextField("name");
            add( name );
            TextField description = new TextField("description");
            description.setRequired(false);
            add(description);
            Label iid = new Label("id");
            add(iid);
            parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ), parents );
            add(parentsCB);
            setOutputMarkupId(true);
            addParentSearchModal();

            add( new AjaxButton( "parents.delete" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on members.delete";
                    if ( VUtil.isNotNullOrEmpty( parentsSelection ) )
                    {
                        msg += " selection:" + parentsSelection;
                        OrgUnit orgUnit = ( OrgUnit ) form.getModel().getObject();
                        if ( orgUnit.getParents() != null )
                        {
                            orgUnit.getParents().remove( parentsSelection );
                            parents.remove( parentsSelection );
                            parentsSelection = "";
                            parents = new ArrayList<String>();
                            parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( this,
                                PARENTS_SELECTION ), parents );
                            component = editForm;
                            msg += ", was removed from local, commit to persist changes on server";
                        }
                        else
                        {
                            msg += ", no action taken because org unit does not have parent set";
                        }
                    }
                    else
                    {
                        msg += ", no action taken because parents selection is empty";
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

        private void addParentSearchModal()
        {
            final ModalWindow parentsModalWindow;
            add( parentsModalWindow = new ModalWindow( "ouparentsmodal" ) );
            final OUSearchModalPanel parentSearchModalPanel = new OUSearchModalPanel( parentsModalWindow.getContentId(), parentsModalWindow, isUser );
            parentsModalWindow.setContent( parentSearchModalPanel );
            parentsModalWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback()
            {
                @Override
                public void onClose( AjaxRequestTarget target )
                {
                    parent = parentSearchModalPanel.getSelection();
                    if ( parent != null )
                    {
                        parentsSelection = parent.getName();
                        component = editForm;
                    }
                }
            } );

            add( new AjaxButton( "parents.search" )
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    String msg = "clicked on parents search";
                    msg += parentsSelection != null ? ": " + parentsSelection : "";
                    parentSearchModalPanel.setSearchVal( parentsSelection );
                    display.setMessage( msg );
                    log.debug( msg );
                    target.prependJavaScript( GlobalIds.WICKET_WINDOW_UNLOAD_CONFIRMATION_FALSE );
                    parentsModalWindow.show( target );
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

            parentsModalWindow.setTitle( "OU Parent Selection Modal" );
            parentsModalWindow.setInitialWidth( 550 );
            parentsModalWindow.setInitialHeight( 450 );
            parentsModalWindow.setCookieName( "parent-assign-modal" );
        }

        private void updateEntityWithComboData(OrgUnit orgUnit)
        {
            if(VUtil.isNotNullOrEmpty(parentsSelection))
            {
                orgUnit.setParent( parentsSelection );
                parents.add( parentsSelection );
            }
        }

        @Override
        public void onEvent(final IEvent<?> event)
        {
            if (event.getPayload() instanceof SelectModelEvent)
            {
                SelectModelEvent modelEvent = (SelectModelEvent) event.getPayload();
                OrgUnit orgUnit = (OrgUnit) modelEvent.getEntity();
                this.setModelObject(orgUnit);
                if(VUtil.isNotNullOrEmpty(orgUnit.getParents()))
                {
                    parents = new ArrayList<String>(orgUnit.getParents());
                    parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),parents );
                }
                else
                {
                    parents = new ArrayList<String>();
                    parentsCB = new ComboBox<String>( PARENTS, new PropertyModel<String>( this, PARENTS_SELECTION ),parents );
                }
                editForm.addOrReplace(parentsCB);
                String msg = "OrgUnit: " + orgUnit.getName() + " has been selected";
                log.debug(".onEvent SelectModelEvent: " + orgUnit.getName());
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
