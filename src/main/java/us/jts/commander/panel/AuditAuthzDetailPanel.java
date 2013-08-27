/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import us.jts.commander.*;
import us.jts.commander.GlobalIds;
import us.jts.fortress.*;
import us.jts.fortress.rbac.AuthZ;
import us.jts.fortress.rbac.User;
import us.jts.fortress.util.attr.VUtil;

/**
 * @author Shawn McKinney
 * @version $Rev$
 * Date: 8/6/13
 */
public class AuditAuthzDetailPanel extends FormComponentPanel
{
    @SpringBean
    private AuditMgr auditMgr;
    @SpringBean
    private ReviewMgr reviewMgr;
    private static final Logger LOG = Logger.getLogger(AuditAuthzDetailPanel.class.getName());
    private Form detailForm;
    private Displayable display;
    private UserAuditDetailPanel userPanel;

    public Form getForm()
    {
        return this.detailForm;
    }

    public AuditAuthzDetailPanel( String id, Displayable display )
    {
        super(id);
        this.auditMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.reviewMgr.setAdmin( GlobalUtils.getRbacSession( this ) );
        this.detailForm = new AuditAuthzDetailForm(GlobalIds.DETAIL_FIELDS, new CompoundPropertyModel<AuthZ>(new AuthZ()));
        this.display = display;
        add( detailForm );
    }

    public class AuditAuthzDetailForm extends Form
    {
        private Component component;

        public AuditAuthzDetailForm(String id, final IModel<AuthZ> model)
        {
            super(id, model);
            add( new Label( GlobalIds.REQ_RESULT ) );
            add(  new Label( GlobalIds.REQ_START ) );
            add( new Label( GlobalIds.REQ_ATTR ) );
            add( new Label( GlobalIds.REQ_ATTRS_ONLY ) );
            add( new Label( GlobalIds.REQ_DEREF_ALIASES ) );
            userPanel = new UserAuditDetailPanel( GlobalIds.USERAUDITDETAILPANEL, new CompoundPropertyModel<User>(new User()) );
            add( userPanel );
            setOutputMarkupId( true );
        }

        @Override
        public void onEvent(final IEvent<?> event)
        {
            if (event.getPayload() instanceof SelectModelEvent)
            {
                SelectModelEvent modelEvent = (SelectModelEvent) event.getPayload();
                AuthZ authZ = (AuthZ) modelEvent.getEntity();
                this.setModelObject(authZ);
                String msg = "AuthZ: " + authZ.getReqAuthzID() + " has been selected";
                LOG.debug( ".onEvent SelectModelEvent: " + authZ.getReqAuthzID() );
                GlobalUtils.getAuthZPerm(authZ.getReqDN() );
                display.setMessage(msg);
                component = detailForm;

            }
            else if (event.getPayload() instanceof AjaxRequestTarget)
            {
                // only add the form to ajax target if something has changed...
                if (component != null)
                {
                    AjaxRequestTarget target = ((AjaxRequestTarget) event.getPayload());
                    LOG.debug( ".onEvent AjaxRequestTarget: " + target.toString() );
                    target.add(component);
                    component = null;
                }
                display.display((AjaxRequestTarget) event.getPayload());
            }
        }

        @Override
        protected void onBeforeRender()
        {
            if ( getModel() != null )
            {
                User user = null;
                // necessary to push the 'changed' model down into the aggregated panel:
                AuthZ authZ = (AuthZ)detailForm.getModelObject();
                if( VUtil.isNotNullOrEmpty( authZ.getReqAuthzID() ))
                {
                    user = GlobalUtils.getUser(reviewMgr, authZ.getReqAuthzID());
                }
                if(user == null)
                {
                    user = new User();
                }
                IModel<User> userModel = new CompoundPropertyModel<User>(user);
                userPanel.setDefaultModel( userModel );
            }
            else
            {
                LOG.info( ".onBeforeRender null model object" );
            }
            super.onBeforeRender();
        }
    }
}
