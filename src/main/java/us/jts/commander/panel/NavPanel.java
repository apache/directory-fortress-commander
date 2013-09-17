/*
 * Copyright (c) 2013, JoshuaTree Software. All rights reserved.
 */

package us.jts.commander.panel;

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import us.jts.commander.AuditAuthzPage;
import us.jts.commander.AuditBindPage;
import us.jts.commander.AuditModPage;
import us.jts.commander.GlobalUtils;
import us.jts.commander.SdDynamicPage;
import us.jts.commander.GlobalIds;
import us.jts.commander.ObjectAdminPage;
import us.jts.commander.OuUserPage;
import us.jts.commander.ObjectPage;
import us.jts.commander.OuPermPage;
import us.jts.commander.PermAdminPage;
import us.jts.commander.PermPage;
import us.jts.commander.PwPolicyPage;
import us.jts.commander.RoleAdminPage;
import us.jts.commander.RolePage;
import us.jts.commander.SdStaticPage;
import us.jts.commander.SecureIndicatingAjaxButton;
import us.jts.commander.TestAuthorization;
import us.jts.commander.UserPage;

/**
 * @author Shawn McKinney
 * @version $Rev$
 *          Date: 5/21/13
 */
public class NavPanel extends FormComponentPanel
{

    public NavPanel( String id )
    {
        super( id );
        add( new NavForm( "navButtons" ) );
        this.setOutputMarkupId( true );
    }

    public class NavForm extends Form
    {
        public NavForm( String id )
        {
            super( id );
            add( new SecureIndicatingAjaxButton( GlobalIds.USERS_PAGE, GlobalIds.ROLE_USERS )
            {
                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    setResponsePage( UserPage.class );
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
            }
        );

        add( new SecureIndicatingAjaxButton(GlobalIds.ROLES_PAGE, GlobalIds.ROLE_ROLES )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( RolePage.class );
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
        }

        );
        final PageParameters parameters = new PageParameters();

        add( new SecureIndicatingAjaxButton(GlobalIds.POBJS_PAGE, GlobalIds.ROLE_PERMOBJS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( ObjectPage.class, parameters );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.PERMS_PAGE, GlobalIds.ROLE_PERMS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( PermPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.PWPOLICIES_PAGE, GlobalIds.ROLE_POLICIES )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( PwPolicyPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.SSDS_PAGE, GlobalIds.ROLE_SSDS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( SdStaticPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.DSDS_PAGE, GlobalIds.ROLE_DSDS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( SdDynamicPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.USEROUS_PAGE, GlobalIds.ROLE_USEROUS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( OuUserPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.PERMOUS_PAGE, GlobalIds.ROLE_PERMOUS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( OuPermPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.ADMROLES_PAGE, GlobalIds.ROLE_ADMINROLES )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( RoleAdminPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.ADMPOBJS_PAGE, GlobalIds.ROLE_ADMINOBJS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( ObjectAdminPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.ADMPERMS_PAGE, GlobalIds.ROLE_ADMINPERMS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( PermAdminPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.AUDIT_BINDS_PAGE, GlobalIds.ROLE_AUDIT_BINDS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( AuditBindPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.AUDIT_AUTHZS_PAGE, GlobalIds.ROLE_AUDIT_AUTHZS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( AuditAuthzPage.class );
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
        }

        );

        add( new SecureIndicatingAjaxButton(GlobalIds.AUDIT_MODS_PAGE, GlobalIds.ROLE_AUDIT_MODS )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                setResponsePage( AuditModPage.class );
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
        }

        );

        //@Authorizable
/*
            add( new SecureIndicatingAjaxButton( "test", GlobalIds.ADMIN_MGR, "test")
            {
                @Override
                public void onSubmit( AjaxRequestTarget target, Form<?> form )
                {
                    // sleep for 5 seconds to show off the busy indicator
                    try
                    {
                        Thread.sleep( 5000 );
                    }
                    catch ( InterruptedException e )
                    {
                        // noop
                    }
                }
            }.setPosition( IndicatingAjaxButton.Position.RIGHT));
*/
/*
        add( new SecureIndicatingAjaxButton("test" )
        {
            @Override public void onSubmit ( AjaxRequestTarget target, Form < ?>form)
            {
                // sleep for 5 seconds to show off the busy indicator
                try
                {
                    TestAuthorization testSecurity = new TestAuthorization();
                    boolean result = testSecurity.checkAccess( GlobalUtils.getRbacSession( this ), "foo", "fighters" );
                    Thread.sleep( 1000 );
                }
                catch ( InterruptedException e )
                {
                    // noop
                }
            }
        }
        .

        setPosition( IndicatingAjaxButton.Position.RIGHT )

        );
*/
    }
}
}